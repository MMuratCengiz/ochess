package com.mcp.ochess.controller;

import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.game.Game;
import com.mcp.ochess.game.MoveResultStatus;
import com.mcp.ochess.game.Side;
import com.mcp.ochess.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
    private final Object SYNC = new Object();

    private final SimpMessagingTemplate messagingTemplate;
    private final SimpMessageSendingOperations messageOps;
    private HashMap<Integer, HashMap<Side, String>> clients = new HashMap<>();

    @Autowired
    public GameEvents(SimpMessageSendingOperations messageOps, SimpMessagingTemplate messagingTemplate) {
        this.messageOps = messageOps;
        this.messagingTemplate = messagingTemplate;
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
                sides.put(Side.Black, user.getName());
            } else {
                // todo throw lobby full
            }
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        // todo destroy
    }

    @MessageMapping("/lobby.move")
    @SendTo("/ingame")
    public ActionResult move(@Payload MoveAction action, Principal user, @Header("simpSessionId") String sessionId)
            throws OChessBaseException {
        // Need to make this autoload state.
        synchronized (SYNC) {
            User u = (User) ((UsernamePasswordAuthenticationToken) user).getPrincipal();
            HashMap<Side, String> players = clients.get(u.getPlayer().getInGameLobby().getId());

            if (players.size() != 2) {
                return new ActionResult("NoOpponent");
            }

            Side side = players.get(Side.White).equals(u.getName()) ? Side.White : Side.Black;

            Game game = Game.getGame(action.getLobbyId());
            MoveResultStatus status = game.move(action.getFrom(), action.getTo(), side);

            MoveResult result = GameRestController.createMoveResult(status);

            if (status ==  MoveResultStatus.EN_PASSANT_MOVE) {
                result.setKill(game.lastEnPassantMoveKill());
            }

            if (result.isValidMove()) {
                result.setMoveId(action.getMoveId());
                result.setFrom(action.getFrom());
                result.setTo(action.getTo());
            }

            return result;
        }
    }
}
