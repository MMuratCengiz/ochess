package com.mcp.ochess.game;

import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.model.MoveResult;

import java.util.HashMap;

public class Game {
    private static final Object SYNC = new Object();
    private static final HashMap<Integer, Game> games = new HashMap<>();

    private Side turn;
    private Board board;

    private Game() throws OChessBaseException {
        turn = Side.White;
        board = new Board();
    }

    public static void createNewGame(Integer lobbyId) throws OChessBaseException {
        synchronized (SYNC) {
            if (games.containsKey(lobbyId)) {
                throw new OChessBaseException(String.format("A game for this lobby(%s) already exists", lobbyId));
            }

            games.put(lobbyId, new Game());
        }
    }

    public static Game getGame(Integer lobbyId) throws OChessBaseException {
        synchronized (SYNC) {
            Game game = games.get(lobbyId);
            if (game == null) {
                throw new OChessBaseException(String.format("A game with lobby id: %s does not exist.", lobbyId));
            }
            return game;
        }
    }

    public static void ensureNewGame(Integer lobbyId) throws OChessBaseException {
        synchronized (SYNC) {
            if (! games.containsKey(lobbyId)) {
                games.put(lobbyId, new Game());
            }
        }
    }

    // True means checkMate
    public MoveResultStatus move(String from, String to, Side side) throws OChessBaseException {
        Piece piece = board.getPiece(Position.fromString(from));

        if (piece != null && piece.getSide() != turn) {
            return MoveResultStatus.OUT_OF_TURN;
        }

        if (piece != null && side != null && piece.getSide() != side) {
            return MoveResultStatus.MOVING_OPPONENT_PIECE;
        }

        MoveResultStatus status = board.move(Position.fromString(from), Position.fromString(to));

        if (status != MoveResultStatus.INVALID_MOVE
                && status != MoveResultStatus.INVALID_MOVE_KING_THREATENED
                && status != MoveResultStatus.PIECE_DOES_NOT_EXIST
                && status != MoveResultStatus.MOVING_OPPONENT_PIECE) {
            turn = turn == Side.White ? Side.Black : Side.White;
        }

        if (status == MoveResultStatus.PAWN_TRANSFORM) {
            // wait for transform
            return MoveResultStatus.PAWN_TRANSFORM;
        }

        if (board.tryCheckMate(turn)) {
            return MoveResultStatus.CHECKMATE;
        }

        return status;
    }

    public void transformPawn(Position pawnPos, PieceKind transformTo) throws OChessBaseException {
        board.transformPawn(pawnPos, transformTo);
    }

    public boolean validateTurn(Side userSide) {
        return turn == userSide;
    }
}
