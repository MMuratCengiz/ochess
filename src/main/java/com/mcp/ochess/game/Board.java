package com.mcp.ochess.game;

import com.mcp.ochess.exceptions.OChessBaseException;

import java.util.*;

public class Board {
    private HashMap<Position, Piece> layout = new HashMap<>();
    private String lastEnPassantMove;
    private Piece whiteKing;
    private Piece blackKing;
    private boolean testing;

    // Castling checks
    private boolean whiteKingMoved = false;
    private boolean whiteLeftRookMoved = false;
    private boolean whiteRightRookMoved = false;
    private boolean blackKingMoved = false;
    private boolean blackLeftRookMoved = false;
    private boolean blackRightRookMoved = false;
    private Position awaitingPawnTransform = null;
    private Side awaitingPawnTransformSide;

    // En passant enabled boxes use class for better readability
    private ArrayList<EnPassantDesc> enPassantEnabledPositions = new ArrayList<>();

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

    public boolean isCellThreatened(Position position, Side threatenedBy) {
        return threateningPieces(position, threatenedBy).size() > 0;
    }

    public boolean isOccupied(Position position) {
        return layout.get(position) != null;
    }

    public Piece getPiece(Position pos) {
        return layout.get(pos);
    }

    public MoveResultStatus move(Position from, Position to) throws OChessBaseException {
        if (awaitingPawnTransform != null) {
            throw new OChessBaseException("Transform the pawn before continuing to move.");
        }

        Piece piece = getPiece(from);

        if (piece == null) {
            return MoveResultStatus.PIECE_DOES_NOT_EXIST;
        }

        boolean castlingMove = checkCastlingPossible(from, to);
        EnPassantDesc enPassantMove = null;

        if (piece.getKind() == PieceKind.Pawn) {
            for (EnPassantDesc desc : enPassantEnabledPositions) {
                if (from.equals(desc.enabledPawn) && to.equals(desc.movedPawnPrevLoc)) {
                    enPassantMove = desc;
                }
            }
            enPassantEnabledPositions.clear();
        }

        if (! piece.isValidMove(to) && enPassantMove == null && !castlingMove) {
            return MoveResultStatus.INVALID_MOVE;
        }

        if (castlingMove) {
            moveCastlingPieces(from, to);
            return MoveResultStatus.CASTLING_MOVE;
        } else {
            invalidateCastling(from, piece);
        }

        Piece tempToPiece = layout.get(to);
        if (enPassantMove != null) {
            tempToPiece = layout.get(enPassantMove.movedPawnNewLoc);
            layout.remove(enPassantMove.movedPawnNewLoc);
        }

        populateEnPassant(piece, to);

        Piece occupied = getPiece(to);
        piece.moveTo(to);

        layout.remove(from);
        layout.put(to, piece);

        if ((piece.getSide() == Side.White && isCellThreatened(whiteKing.getPosition(), opposite(piece.getSide()))) ||
                (piece.getSide() == Side.Black && isCellThreatened(blackKing.getPosition(), opposite(piece.getSide())))) {
            // Revert to last state
            layout.put(to, tempToPiece);
            piece.moveToNoCheck(from);
            layout.put(from, piece);

            return MoveResultStatus.INVALID_MOVE_KING_THREATENED;
        }

        // Handle pawn transform
        if (piece.getKind() == PieceKind.Pawn && (
                (piece.getSide() == Side.Black && piece.getPosition().getRow() == 1) || // black reaches 1st row
                        (piece.getSide() == Side.White && piece.getPosition().getRow() == 8) // white reaches 8th row
        )) {
            awaitingPawnTransform = piece.getPosition();
            awaitingPawnTransformSide = piece.getSide();
            return MoveResultStatus.PAWN_TRANSFORM;
        }

        if ((piece.getSide() == Side.White && isCellThreatened(blackKing.getPosition(), piece.getSide())) ||
                (piece.getSide() == Side.Black && isCellThreatened(whiteKing.getPosition(), piece.getSide()))) {
            return tryCheckMate(opposite(piece.side)) ? MoveResultStatus.CHECKMATE : MoveResultStatus.CHECK;
        }

        if (occupied != null) {
            return MoveResultStatus.KILL;
        }

        if (enPassantMove != null) {
            lastEnPassantMove = enPassantMove.movedPawnNewLoc.toString();
            return MoveResultStatus.EN_PASSANT_MOVE;
        }

        return MoveResultStatus.MOVED_TO_EMPTY;
    }

    public String lastEnPassantMoveKill() {
        return lastEnPassantMove;
    }

    private void invalidateCastling(Position from, Piece piece) {
        if (piece.getKind() == PieceKind.Rook && from.getColumn() == 1 && piece.getSide() == Side.Black) {
            blackLeftRookMoved = true;
        }

        if (piece.getKind() == PieceKind.Rook && from.getColumn() == 8 && piece.getSide() == Side.Black) {
            blackRightRookMoved = true;
        }

        if (piece.getKind() == PieceKind.King && from.getColumn() == 5 && piece.getSide() == Side.Black) {
            blackKingMoved = true;
        }

        if (piece.getKind() == PieceKind.Rook && from.getColumn() == 1 && piece.getSide() == Side.White) {
            whiteLeftRookMoved = true;
        }

        if (piece.getKind() == PieceKind.Rook && from.getColumn() == 8 && piece.getSide() == Side.White) {
            whiteRightRookMoved = true;
        }

        if (piece.getKind() == PieceKind.King && from.getColumn() == 5 && piece.getSide() == Side.White) {
            whiteKingMoved = true;
        }
    }

    public boolean tryCheckMate(Side side) throws OChessBaseException {
        Piece king = side == Side.White ? whiteKing : blackKing;

        ArrayList<Piece> threateningPieces = threateningPieces(king.getPosition(), opposite(side));

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
            try {
                anyMoveIsValid = anyMoveIsValid || king.isValidMove(
                        new Position(king.position.getColumn() + move[0], king.position.getRow() + move[1]));
            } catch (OChessBaseException ignored) {
                // Position is out of bounds
            }
        }

        if (anyMoveIsValid) {
            return false;
        }

        // check if the threatening piece can be captured
        Piece piece = threateningPieces.get(0); // there should only be one

        if (isCellThreatened(piece.getPosition(), opposite(side)) || king.threatens(piece.getPosition())) {
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

    public PawnTransformStatus transformPawn(PieceKind transformTo, Side side) throws OChessBaseException {
        if (awaitingPawnTransform == null) {
            return PawnTransformStatus.NO_PAWN_WAITING_TRANSFORM;
        }

        if (side != awaitingPawnTransformSide) {
            return PawnTransformStatus.OPPONENT_PIECE;
        }

        switch (transformTo) {
            case Pawn:
            case King:
                return PawnTransformStatus.INVALID_TRANSFORM;
            case Rook:
                // why
                layout.put(awaitingPawnTransform, Piece.createRook(this, awaitingPawnTransform, side));
                break;
            case Knight:
                layout.put(awaitingPawnTransform, Piece.createKnight(this, awaitingPawnTransform, side));
                break;
            case Bishop:
                // why
                layout.put(awaitingPawnTransform, Piece.createBishop(this, awaitingPawnTransform, side));
                break;
            case Queen:
                layout.put(awaitingPawnTransform, Piece.createQueen(this, awaitingPawnTransform, side));
                break;
        }

        awaitingPawnTransform = null;
        awaitingPawnTransformSide = null;

        if (tryCheckMate(opposite(side))) {
            return PawnTransformStatus.CHECKMATE;
        }

        Piece oppositeKing = side == Side.White ? blackKing : whiteKing;

        if (isCellThreatened(oppositeKing.getPosition(), side)) {
            return PawnTransformStatus.CHECK;
        }

        return PawnTransformStatus.TRANSFORMED;
    }

    public Position pieceWaitingTransform() {
        return awaitingPawnTransform;
    }

    private boolean checkCastlingPossible(Position from, Position to) throws OChessBaseException {
        Piece piece = layout.get(from);

        if (piece == null || piece.getKind() != PieceKind.King) {
            return false;
        }

        if (piece.getSide() == Side.White && whiteKingMoved) {
            return false;
        }

        if (piece.getSide() == Side.Black && blackKingMoved) {
            return false;
        }

        int[] requiredSafeCells = null;
        boolean relativeRookMoved = false;

        if (to.getColumn() == 7) {
            requiredSafeCells = new int[] { 6, 7 };
            relativeRookMoved = piece.getSide() == Side.White ? whiteRightRookMoved : whiteLeftRookMoved;
        } else if (to.getColumn() == 3) {
            requiredSafeCells = new int[] { 2, 3, 4 };
            relativeRookMoved = piece.getSide() == Side.White ? blackRightRookMoved : blackLeftRookMoved;
        } else {
            return false;
        }

        if (relativeRookMoved) {
            return false;
        }
        boolean anyOccupiedOrThreatened = false;

        for (int requiredSafeCell: requiredSafeCells) {
            Position pos = new Position(requiredSafeCell, to.getRow());
            if (isOccupied(pos) || isCellThreatened(pos, opposite(piece.getSide()))) {
                anyOccupiedOrThreatened = true;
            }
        }

        return !anyOccupiedOrThreatened && !isCellThreatened(piece.getPosition(), opposite(piece.getSide()));
    }

    private void moveCastlingPieces(Position from, Position to) throws OChessBaseException {
        Piece piece = layout.get(from);
        layout.put(to, piece);
        layout.remove(from);

        int row = piece.getSide() == Side.White ? 1 : 8;
        int rookCol = to.getColumn() == 3 ? 1 : 8;

        Position rookPos = new Position(rookCol, row);
        Piece rook = layout.get(rookPos);

        layout.remove(rookPos);
        layout.put(new Position(to.getColumn() == 3 ? 4 : 6, piece.getPosition().getRow()), rook);
    }

    private void populateEnPassant(Piece piece, Position to) throws OChessBaseException {
        if (piece.getKind() == PieceKind.Pawn && Math.abs(piece.getPosition().getRow() -  to.getRow()) == 2) {
            Piece leftPawn = null;
            try {
                leftPawn = layout.get(new Position(to.getColumn()  - 1, to.getRow()));
            } catch (OChessBaseException ignored) {}
            Piece rightPawn = null;
            try {
                rightPawn = layout.get(new Position(to.getColumn()  + 1, to.getRow()));
            } catch (OChessBaseException ignored) {}

            createEnPassantDesc(piece, to, rightPawn);
            createEnPassantDesc(piece, to, leftPawn);
        }
    }

    private void createEnPassantDesc(Piece piece, Position to, Piece adjacentPawn) throws OChessBaseException {
        if (adjacentPawn != null && adjacentPawn.getKind() == PieceKind.Pawn) {
            EnPassantDesc enPassantDesc = new EnPassantDesc();
            enPassantDesc.enabledPawn = adjacentPawn.getPosition();
            enPassantDesc.movedPawnPrevLoc = new Position(piece.getPosition().getColumn(), to.getRow() - getDirection(piece.getSide()));
            enPassantDesc.movedPawnNewLoc = to;
            enPassantEnabledPositions.add(enPassantDesc);
        }
    }

    private boolean tryBishopCheckMate(Position threatPos, Piece king) throws OChessBaseException {
        int colDif = threatPos.getColumn() > king.getPosition().getColumn() ? -1 : 1;
        int rowDif = threatPos.getRow() > king.getPosition().getRow() ? -1 : 1;

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

    private ArrayList<Piece> threateningPieces(Position position, Side threatenedBy) {
        ArrayList<Piece> pieces = new ArrayList<>();

        for (Piece p: layout.values()) {
             if (p != null && p.getKind() != PieceKind.King && p.getSide() == threatenedBy && p.threatens(position)) {
                pieces.add(p);
            }
        }

        return pieces;
    }

    private Side opposite(Side side) {
        return side == Side.White ? Side.Black : Side.White;
    }

    private int getDirection(Side side) {
        return side == Side.White ? 1 : -1;
    }

    public Position queryPiece(Side side, PieceKind kind, String colHelper) throws OChessBaseException {
        ArrayList<Piece> pieces = new ArrayList<>();

        for (Piece p: layout.values()) {
            if (p.getKind() == kind && p.getSide() == side) {
                if (colHelper == null || (Position.fromString(colHelper + "1").getColumn() == p.getPosition().getColumn())) {
                    pieces.add(p);
                }
            }
        }

        if (pieces.size() > 1) {
            throw new OChessBaseException("Ambigious piece description");
        }

        return pieces.get(0).getPosition();
    }

    // Pawns are prioritized
    public Position queryPiece(Side side, PieceKind kind, Position canMoveTo) throws OChessBaseException {
        ArrayList<Piece> pieces = new ArrayList<>();

        for (Piece p: layout.values()) {
            if (p.getSide() == side) {
                if (p.isValidMove(canMoveTo) && (kind == null || kind == p.getKind())) {
                    pieces.add(p);
                }
            }
        }

        int pawnIndex = -1;
        if (pieces.size() > 0 && kind == null) {
            for (int i = 0; i < pieces.size(); ++i) {
                if (pieces.get(i).kind == PieceKind.Pawn) {
                    if (pawnIndex != -1) {
                        throw new OChessBaseException("Ambigious piece description");
                    }
                    pawnIndex = i;
                }
            }

            return pieces.get(pawnIndex).getPosition();
        }

        if (pieces.size() > 1) {
            throw new OChessBaseException("Ambigious piece description");
        }

        return pieces.get(0).getPosition();
    }

    // Risky use with caution
    public void moveToNoCheck(Position from, Position to) {
        this.layout.put(to, layout.get(from));
        this.layout.remove(from);
    }

    // Risky use with caution
    public void placeNoCheck(Position pos, Piece piece) {
        this.layout.put(pos, piece);
    }

    /*
    Marshall format
    Single position is represented like so:
    Position|Side|PieceLetter ie. A1BK
    The king is noted as X
    No spaces or delimiters.
     */
    public String marshall() {
        StringBuilder result = new StringBuilder();

        Iterator<Map.Entry<Position, Piece>> en = layout.entrySet().iterator();

        while (en.hasNext()) {
            Map.Entry<Position, Piece> tuple = en.next();

            Piece piece = tuple.getValue();

            if (piece != null) { // possible on a dead cell
                result.append(tuple.getKey().toString());
                result.append(piece.getSide() == Side.White ? 'W' : 'B');

                switch (piece.getKind()) {
                    case Pawn:
                        result.append("P");
                        break;
                    case Rook:
                        result.append("R");
                        break;
                    case Knight:
                        result.append("K");
                        break;
                    case Bishop:
                        result.append("B");
                        break;
                    case Queen:
                        result.append("Q");
                        break;
                    case King:
                        result.append("X");
                        break;
                }
            }
        }

        return result.toString();
    }

    // TESTING
    public Board(boolean testing) throws OChessBaseException {
        this.testing = testing;

        whiteKing = Piece.createKing(this, Position.fromString("E1"), Side.White);
        blackKing = Piece.createKing(this, Position.fromString("E8"), Side.Black);

        layout.put(whiteKing.getPosition(), whiteKing);
        layout.put(blackKing.getPosition(), blackKing);
    }

    public void addTestingPiece(Piece piece) {
        if (testing) {
            layout.put(piece.getPosition(), piece);
        }
    }

    //--
}

class EnPassantDesc {
    Position enabledPawn;
    Position movedPawnPrevLoc;
    Position movedPawnNewLoc;
}