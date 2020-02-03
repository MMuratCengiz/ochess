package com.mcp.ochess.controller;

import com.mcp.ochess.dao.LobbyService;
import com.mcp.ochess.dao.UserPlayerService;
import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.game.*;
import com.mcp.ochess.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.HashMap;

@Controller
@Component
public class GameEvents {
    private UserPlayerService userService;
    private LobbyService lobbyService;

    private final Object SYNC = new Object();

    private final SimpMessagingTemplate messagingTemplate;
    private HashMap<Integer, HashMap<Side, String>> clients = new HashMap<>();

    @Autowired
    public GameEvents(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Autowired
    public void setUserPlayerService(UserPlayerService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setLobbyService(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }


    @EventListener
    public void handleClientConnect(SessionConnectedEvent event) throws OChessBaseException {
        synchronized (SYNC) {
            User user = (User) ((UsernamePasswordAuthenticationToken) event.getUser()).getPrincipal();
            int lobbyId = user.getPlayer().getInGameLobby().getId();
            Game.ensureNewGame(lobbyId);

            HashMap<Side, String> sides = clients.get(lobbyId);

            if (sides == null) {
                clients.put(lobbyId, new HashMap<>());
                sides = clients.get(lobbyId);
            }

            if (! sides.containsKey(Side.White)) {
                sides.put(Side.White, user.getName());
            } else if (! sides.containsKey(Side.Black)) {
                OpponentConnectedAction payload = new OpponentConnectedAction();
                payload.setWho(user.getName());
                messagingTemplate.convertAndSend("/ingame/" + lobbyId, payload);

                ChatAction systemMessage = new ChatAction();
                systemMessage.setFrom("System");
                systemMessage.setMessage(user.getName() + " joined the room.");

                messagingTemplate.convertAndSend("/ingame/" + lobbyId, systemMessage);
                sides.put(Side.Black, user.getName());
            }
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        User user = (User) ((UsernamePasswordAuthenticationToken) event.getUser()).getPrincipal();

    }

    @MessageMapping("/lobby.move/{lobbyId}")
    @SendTo("/ingame/{lobbyId}")
    public ActionResult move(@DestinationVariable int lobbyId, @Payload MoveAction action, Principal user, @Header("simpSessionId") String sessionId)
            throws OChessBaseException {
        // Need to make this autoload state.
        User u = (User) ((UsernamePasswordAuthenticationToken) user).getPrincipal();
        Lobby lobby = u.getPlayer().getInGameLobby();

        HashMap<Side, String> players = clients.get(lobby.getId());

        if (players.size() != 2) {
            return new ActionResult("NoOpponent");
        }

        Side side = players.get(Side.White).equals(u.getName()) ? Side.White : Side.Black;

        Game game = Game.getGame(action.getLobbyId());
        MoveResultStatus status = game.move(action.getFrom(), action.getTo(), side);

        if (status == MoveResultStatus.CHECKMATE) {
            String[] winnerLoser = getWinnerLoserBySide(lobby, getSide(u));
            sendGameOver(lobby, u.getName() + " has won with checkmate.", winnerLoser[0], winnerLoser[1]);
        }

        MoveResult result = GameRestController.createMoveResult(status);

        if (status ==  MoveResultStatus.EN_PASSANT_MOVE) {
            result.setKill(game.lastEnPassantMoveKill());
        }

        if (result.isValidMove()) {
            result.setMoveId(action.getMoveId());
            result.setFrom(action.getFrom());
            result.setTo(action.getTo());
            result.setSender(action.getSender());
        }

        return result;
    }

    @MessageMapping("/lobby.ptransform/{lobbyId}")
    @SendTo("/ingame/{lobbyId}")
    public ActionResult pawnTransform(@DestinationVariable int lobbyId, @Payload PawnTransformAction action, Principal user, @Header("simpSessionId") String sessionId)
            throws OChessBaseException {
        User u = (User) ((UsernamePasswordAuthenticationToken) user).getPrincipal();
        Lobby lobby = u.getPlayer().getInGameLobby();

        Game game = Game.getGame(lobby.getId());
        PawnTransformResult pawnTransformResult = new PawnTransformResult();
        pawnTransformResult.setTransformPos(game.getPieceWaitingTransform().toString());

        PawnTransformStatus status = game.transformPawn(PieceKind.valueOf(action.getTransformTo()), getSide(u));

        switch (status) {
            case CHECK:
                pawnTransformResult.setActionResult("Check");
                break;
            case CHECKMATE:
                pawnTransformResult.setActionResult("Checkmate");
                String[] winnerLoser = getWinnerLoserBySide(lobby, getSide(u));
                sendGameOver(lobby, u.getName() + " has won with checkmate.", winnerLoser[0], winnerLoser[1]);
                break;
            case OPPONENT_PIECE:
                pawnTransformResult.setActionResult("OpponentPiece");
                break;
            case INVALID_TRANSFORM:
                pawnTransformResult.setActionResult("InvalidTransform");
                break;
            case NO_PAWN_WAITING_TRANSFORM:
                pawnTransformResult.setActionResult("NoPawnWaitingTransform");
                break;
            case TRANSFORMED:
                pawnTransformResult.setActionResult("Transformed");
                break;
        }

        pawnTransformResult.setNewPieceType(action.getTransformTo());
        return pawnTransformResult;
    }

    @MessageMapping("/lobby.chat/{lobbyId}")
    @SendTo("/ingame/{lobbyId}")
    public ChatAction chat(@Payload ChatAction action) {
        return action;
    }

    @MessageMapping("/lobby.load/{lobbyId}")
    @SendTo("/ingame/{lobbyId}")
    public LoadBoardResult chat(@DestinationVariable int lobbyId, @Payload LoadBoardAction action) throws OChessBaseException {
        Game game = Game.getGame(lobbyId);

        LoadBoardResult result = new LoadBoardResult();
        result.setSender(action.getSender());
        result.setBoard(game.marshallBoard());

        return result;
    }

    @MessageMapping("/lobby.surrender/{lobbyId}")
    @SendTo("/ingame/{lobbyId}")
    public GameOverMessage surrender(@DestinationVariable int lobbyId, @Payload SurrenderAction action, Principal user) {
        User u = (User) ((UsernamePasswordAuthenticationToken) user).getPrincipal();

        String winner;
        String loser;

        synchronized (SYNC) {
            HashMap<Side, String> players = clients.get(lobbyId);
            String whiteUser = players.get(Side.White);
            String blackUser = players.get(Side.Black);

            winner = whiteUser.equals(u.getName()) ? whiteUser : blackUser;
            loser  = whiteUser.equals(u.getName()) ? blackUser : whiteUser;
        }

        String reason = u.getName() + " has surrendered.";

        sendGameOver(u.getPlayer().getInGameLobby(), reason, winner, loser);
        GameOverMessage gameOverMessage = new GameOverMessage();
        gameOverMessage.setReason(reason);

        return gameOverMessage;
    }

    public String[] getWinnerLoserBySide(Lobby lobby, Side winningSide) {
        String winner;
        String loser;

        synchronized (SYNC) {
            HashMap<Side, String> players = clients.get(lobby.getId());

            winner = players.get(winningSide);
            loser  = players.get(winningSide == Side.White ? Side.Black : Side.White);
        }

        return new String[] { winner, loser };
    }

    public void sendGameOver(Lobby lobby, String reason, String winner, String loser) {
        Game.endGame(lobby.getId());
        ChatAction chat = new ChatAction();
        chat.setFrom("System");
        chat.setMessage(reason + " Going back to lobby in 10 seconds.");

        Player winnerPlayer = userService.loadUserByUsername(winner).getPlayer();
        Player loserPlayer  = userService.loadUserByUsername(loser).getPlayer();

        winnerPlayer.setWins(winnerPlayer.getWins() + 1);
        winnerPlayer.setInGameLobby(null);
        loserPlayer.setLosses(loserPlayer.getLosses() + 1);
        loserPlayer.setInGameLobby(null);

        if (lobby.getType() == Lobby.LOBBY_TYPE_RANKED) {
            winnerPlayer.setMmr(winnerPlayer.getMmr() + 25);
            loserPlayer.setMmr(loserPlayer.getMmr() - 25);
        }

        userService.updatePlayer(winnerPlayer);
        userService.updatePlayer(loserPlayer);

        clients.remove(lobby.getId());

        lobbyService.delete(lobby);
        messagingTemplate.convertAndSend("/ingame/" + lobby.getId(), chat);

        GameOverMessage gameOverMessage = new GameOverMessage();
        gameOverMessage.setReason(reason);
        messagingTemplate.convertAndSend("/ingame/" + lobby.getId(), gameOverMessage);
    }

    private Side getSide(User u) {
        return u.getPlayer().getInGameLobby().getWhiteUser().getId() == u.getId() ? Side.White : Side.Black;
    }
}
