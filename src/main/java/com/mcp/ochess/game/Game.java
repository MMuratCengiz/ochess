package com.mcp.ochess.game;

import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.model.MoveResult;

import java.util.HashMap;

public class Game {
    private static final Object SYNC = new Object();
    private static final HashMap<String, Game> games = new HashMap<>();

    private Side turn;
    private Board board;

    private Game() {
        // black since we flip the turn in the beginning of the move for code simplicity
        turn = Side.Black;
    }

    public static void createNewGame(String lobbyId) throws OChessBaseException {
        synchronized (SYNC) {
            if (games.containsKey(lobbyId)) {
                throw new OChessBaseException(String.format("A game for this lobby(%s) already exists", lobbyId));
            }

            games.put(lobbyId, new Game());
        }
    }

    public static Game getGame(String lobbyId) throws OChessBaseException {
        synchronized (SYNC) {
            Game game = games.get(lobbyId);
            if (game == null) {
                throw new OChessBaseException(String.format("A game with lobby id: %s does not exist.", lobbyId));
            }
            return game;
        }
    }

    // True means checkMate
    public MoveResultStatus move(String from, String to) throws OChessBaseException {
        turn = turn == Side.White ? Side.Black : Side.White;

        Piece piece = board.getPiece(Position.fromString(from));

        if (piece != null && piece.getSide() != turn) {
            throw new OChessBaseException("Moving out of turn.");
        }

        MoveResultStatus status = board.move(Position.fromString(from), Position.fromString(to));

        if (board.tryCheckMate(turn)) {
            return MoveResultStatus.CHECKMATE;
        }

        return status;
    }
}
