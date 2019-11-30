package com.mcp.ochess.game;

import com.mcp.ochess.exceptions.OChessBaseException;

import java.util.HashMap;

public class Board {
    private HashMap<Position, Piece> layout = new HashMap<>();

    public Board() throws OChessBaseException {
        // First row
        layout.put(Position.fromString("A1"), Piece.createRook(this, Position.fromString("A1"), Side.White));
        layout.put(Position.fromString("B1"), Piece.createKnight(this, Position.fromString("B1"), Side.White));
        layout.put(Position.fromString("C1"), Piece.createBishop(this, Position.fromString("C1"), Side.White));
        layout.put(Position.fromString("D1"), Piece.createQueen(this, Position.fromString("D1"), Side.White));
        layout.put(Position.fromString("E1"), Piece.createKing(this, Position.fromString("E1"), Side.White));
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
        layout.put(Position.fromString("E8"), Piece.createKing(this, Position.fromString("E8"), Side.Black));
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

    public boolean isThreatenedBy(Position position, Side side) {
        boolean oneThreatens = false;

        for (Piece p: layout.values()) {
            if (p.isAlive() && p.getSide() != side) {
                oneThreatens = p.threatens(position);
                if (oneThreatens) {
                    break;
                }
            }
        }

        return oneThreatens;
    }

    public boolean isOccupied(Position position) {
        return layout.containsKey(position);
    }

    public Piece getPiece(Position to) {
        return layout.get(to);
    }
}
