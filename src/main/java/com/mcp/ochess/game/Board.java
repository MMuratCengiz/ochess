package com.mcp.ochess.game;

import com.mcp.ochess.exceptions.OChessBaseException;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    private HashMap<Position, Piece> layout = new HashMap<>();

    private Piece whiteKing;
    private Piece blackKing;

    public Board() throws OChessBaseException {
        whiteKing = Piece.createKing(this, Position.fromString("E1"), Side.White);
        blackKing = Piece.createKing(this, Position.fromString("E8"), Side.Black);

        // First row
        layout.put(Position.fromString("A1"), Piece.createRook(this, Position.fromString("A1"), Side.White));
        layout.put(Position.fromString("B1"), Piece.createKnight(this, Position.fromString("B1"), Side.White));
        layout.put(Position.fromString("C1"), Piece.createBishop(this, Position.fromString("C1"), Side.White));
        layout.put(Position.fromString("D1"), Piece.createQueen(this, Position.fromString("D1"), Side.White));
        layout.put(Position.fromString("E1"), whiteKing);
        layout.put(Position.fromString("F1"), Piece.createBishop(this, Position.fromString("F1"), Side.White));
        layout.put(Position.fromString("G1"), Piece.createKnight(this, Position.fromString("G1"), Side.White));
        layout.put(Position.fromString("H1"), Piece.createRook(this, Position.fromString("H1"), Side.White));
        // Second row
        layout.put(Position.fromString("A2"), Piece.createPawn(this, Position.fromString("A2"), Side.White));
        layout.put(Position.fromString("B2"), Piece.createPawn(this, Position.fromString("B2"), Side.White));
        layout.put(Position.fromString("C2"), Piece.createPawn(this, Position.fromString("C2"), Side.White));
        layout.put(Position.fromString("D2"), Piece.createPawn(this, Position.fromString("D2"), Side.White));
        layout.put(Position.fromString("E2"), Piece.createPawn(this, Position.fromString("E2"), Side.White));
        layout.put(Position.fromString("F2"), Piece.createPawn(this, Position.fromString("F2"), Side.White));
        layout.put(Position.fromString("G2"), Piece.createPawn(this, Position.fromString("G2"), Side.White));
        layout.put(Position.fromString("H2"), Piece.createPawn(this, Position.fromString("H2"), Side.White));

        // Eighth row
        layout.put(Position.fromString("A8"), Piece.createRook(this, Position.fromString("A8"), Side.Black));
        layout.put(Position.fromString("B8"), Piece.createKnight(this, Position.fromString("B8"), Side.Black));
        layout.put(Position.fromString("C8"), Piece.createBishop(this, Position.fromString("C8"), Side.Black));
        layout.put(Position.fromString("D8"), Piece.createQueen(this, Position.fromString("D8"), Side.Black));
        layout.put(Position.fromString("E8"), blackKing);
        layout.put(Position.fromString("F8"), Piece.createBishop(this, Position.fromString("F8"), Side.Black));
        layout.put(Position.fromString("G8"), Piece.createKnight(this, Position.fromString("G8"), Side.Black));
        layout.put(Position.fromString("H8"), Piece.createRook(this, Position.fromString("H8"), Side.Black));
        // Seventh row
        layout.put(Position.fromString("A7"), Piece.createPawn(this, Position.fromString("A7"), Side.Black));
        layout.put(Position.fromString("B7"), Piece.createPawn(this, Position.fromString("B7"), Side.Black));
        layout.put(Position.fromString("C7"), Piece.createPawn(this, Position.fromString("C7"), Side.Black));
        layout.put(Position.fromString("D7"), Piece.createPawn(this, Position.fromString("D7"), Side.Black));
        layout.put(Position.fromString("E7"), Piece.createPawn(this, Position.fromString("E7"), Side.Black));
        layout.put(Position.fromString("F7"), Piece.createPawn(this, Position.fromString("F7"), Side.Black));
        layout.put(Position.fromString("G7"), Piece.createPawn(this, Position.fromString("G7"), Side.Black));
        layout.put(Position.fromString("H7"), Piece.createPawn(this, Position.fromString("H7"), Side.Black));
    }

    public boolean isCellThreatened(Position position, Side side) {
        return threateningPieces(position, side).size() > 0;
    }

    public boolean isOccupied(Position position) {
        return layout.containsKey(position);
    }

    public Piece getPiece(Position pos) {
        return layout.get(pos);
    }

    public MoveResultStatus move(Position from, Position to) {
        Piece piece = getPiece(from);

        if (piece == null) {
            return MoveResultStatus.PIECE_DOES_NOT_EXIST;
        }

        if (! piece.isValidMove(to)) {
            return MoveResultStatus.INVALID_MOVE;
        }

        Piece occupied = getPiece(to);

        layout.remove(from);
        layout.put(to, piece);

        if (occupied != null) {
            return occupied.getKind() == PieceKind.King ?
                    MoveResultStatus.CHECK :
                    MoveResultStatus.KILL;
        }


        return MoveResultStatus.MOVED_TO_EMPTY;
    }

    public boolean tryCheckMate(Side side) throws OChessBaseException {
        Piece king = side == Side.White ? whiteKing : blackKing;

        ArrayList<Piece> threateningPieces = threateningPieces(king.getPosition(), side);

        // 0 == col, 1 == row
        int[][] moveTries = new int[][] {
                {-1, 1},{0, 1},{1, 1}, // top side
                {-1, 0},{1, 0}, // left-right
                {-1, -1},{0, -1},{1, -1}, // bottom side
        };

        if (threateningPieces.size() == 0) {
            return false;
        }

        // check if the king can move anywhere
        boolean anyMoveIsValid = false;

        for (int[] move: moveTries) {
            anyMoveIsValid = king.isValidMove(new Position(move[0], move[1]));
        }

        if (anyMoveIsValid) {
            return false;
        }

        // check if the threatening piece can be captured
        Piece piece = threateningPieces.get(0); // there should only be one
        if (isCellThreatened(piece.getPosition(), opposite(side))) {
            return false;
        }

        // check the move can be blocked
        switch (piece.getKind()) {
            // can't runaway from these dudes
            case Pawn:
            case Knight:
            case King:
                return true;
            case Rook:
                return tryRookCheckMate(piece.getPosition(), king);
            case Bishop:
                return tryBishopCheckMate(piece.getPosition(), king);
            case Queen:
                if (piece.getPosition().getRow() == king.getPosition().getRow() ||
                    piece.getPosition().getColumn() == king.getPosition().getColumn()) {
                    return tryRookCheckMate(piece.getPosition(), king);
                }
                return tryBishopCheckMate(piece.getPosition(), king);
        }

        return false;
    }

    private boolean tryBishopCheckMate(Position threatPos, Piece king) throws OChessBaseException {
        int colDif = threatPos.getColumn() > king.getPosition().getColumn() ? 1 : -1;
        int rowDif = threatPos.getRow() > king.getPosition().getRow() ? 1 : -1;

        int colPos = threatPos.getColumn();
        int rowPos = threatPos.getRow();

        while (rowPos != king.getPosition().getRow()) {
            colPos += colDif;
            rowPos += rowDif;

            if (isCellThreatened(new Position(colPos, rowPos), king.getSide())) {
                return false;
            }
        }

        return true;
    }

    private boolean tryRookCheckMate(Position threatPos, Piece king) throws OChessBaseException {
        boolean rowsEqual = threatPos.getRow() == king.getPosition().getRow();

        int max = rowsEqual
                ? Math.max(threatPos.getColumn(), king.getPosition().getColumn())
                : Math.max(threatPos.getRow(), king.getPosition().getRow());

        int min = rowsEqual
                ? Math.min(threatPos.getColumn(), king.getPosition().getColumn())
                : Math.min(threatPos.getRow(), king.getPosition().getRow());

        for (int from = min; from < max - 1; from++) {
            if (isCellThreatened(new Position(
                    rowsEqual ? from : threatPos.getColumn(),
                    rowsEqual ? from : threatPos.getRow()), king.getSide())) {
                return false;
            }
        }

        return true;
    }

    private ArrayList<Piece> threateningPieces(Position position, Side side) {
        ArrayList<Piece> pieces = new ArrayList<>();

        for (Piece p: layout.values()) {
            if (p.isAlive() && p.getSide() != side) {
                if (p.threatens(position)) {
                    pieces.add(p);
                }
            }
        }

        return pieces;
    }

    private Side opposite(Side side) {
        return side == Side.White ? Side.Black : Side.White;
    }
}
