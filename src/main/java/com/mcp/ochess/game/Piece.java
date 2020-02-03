package com.mcp.ochess.game;

import com.mcp.ochess.exceptions.OChessBaseException;

public abstract class Piece {
    Position position;
    PieceKind kind;
    Side side;
    Board board;

    Piece(Board board, Position position, Side side) {
        this.position = position;
        this.side = side;
        this.board = board;
    }

    public static Piece createPawn(Board board, Position position, Side side) {
        return new Pawn(board, position, side);
    }

    public static Piece createRook(Board board, Position position, Side side) {
        return new Rook(board, position, side);
    }

    public static Piece createKnight(Board board, Position position, Side side) {
        return new Knight(board, position, side);
    }

    public static Piece createBishop(Board board, Position position, Side side) {
        return new Bishop(board, position, side);
    }

    public static Piece createQueen(Board board, Position position, Side side) {
        return new Queen(board, position, side);
    }

    public static Piece createKing(Board board, Position position, Side side) {
        return new King(board, position, side);
    }

    public Side getSide() {
        return side;
    }

    public PieceKind getKind() {
        return kind;
    }

    public boolean moveTo(Position to) {
        if (! isValidMove(to)) {
            return false;
        }

        position = to;
        return true;
    }

    public void moveToNoCheck(Position to) {
        position = to;
    }

    public Position getPosition() {
        return position;
    }

    protected Side oppositeSide() {
        return side == Side.Black ? Side.White : Side.Black;
    }
    abstract boolean isValidMove(Position to);

    abstract boolean threatens(Position target);
}


class Pawn extends Piece {
    Pawn(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.Pawn;
    }

    @Override
    protected boolean isValidMove(Position to) {
        int dif = side == Side.White ? 1 : -1;
        int doubleJumpLoc = side == Side.White ? 2 : 7;

        Piece piece = board.getPiece(to);

        if (threatens(to) && piece != null && piece.getSide() != side) {
            return true;
        }

        if (board.isOccupied(to)) {
            return false;
        }

        return  position.getColumn() == to.getColumn() && (
                position.getRow() + dif == to.getRow() ||
                (position.getRow() == doubleJumpLoc && position.getRow() + dif * 2 == to.getRow()));
    }

    @Override
    boolean threatens(Position target) {
        int dif = side == Side.White ? 1 : -1;

        Position pos = this.getPosition();

        boolean leftValidMove = false;
        boolean rightValidMove = false;

        try {
            Piece threatenLeft  = board.getPiece(new Position(pos.getColumn() - 1, pos.getRow() + dif));

            leftValidMove  = threatenLeft != null && threatenLeft.getSide() == oppositeSide() &&
                    threatenLeft.getPosition().equals(target);

        } catch (Exception ignored) {/*out of bounds*/}

        try {
            Piece threatenRight = board.getPiece(new Position(pos.getColumn() + 1, pos.getRow() + dif));

            rightValidMove = threatenRight != null && threatenRight.getSide() == oppositeSide() &&
                    threatenRight.getPosition().equals(target);
        } catch (Exception ignored) {/*out of bounds*/}

        return leftValidMove || rightValidMove;

    }
}

class Knight extends Piece {
    Knight(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.Knight;
    }

    @Override
    boolean isValidMove(Position to) {
        // Simple logic here, either it should go two rows up/down and 1 left/right or vice-a-versa.
        return (Math.abs(position.getRow() - to.getRow())  == 1 && Math.abs(position.getColumn() - to.getColumn()) == 2) ||
                (Math.abs(position.getRow() - to.getRow())  == 2 && Math.abs(position.getColumn() - to.getColumn()) == 1);
    }

    @Override
    boolean threatens(Position target) {
        return isValidMove(target);
    }
}

class Bishop extends Piece {
    Bishop(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.Bishop;
    }

    @Override
    boolean isValidMove(Position to) {
        int colDif = to.getColumn() > position.getColumn() ? 1 : -1;
        int rowDif = to.getRow() > position.getRow() ? 1 : -1;

        return tryMove(position, colDif, rowDif, to);
    }

    public boolean tryMove(Position current, int colDif, int rowDif, Position target) {
        try {
            Position newPosition = new Position(current.getColumn() + colDif, current.getRow() + rowDif);
            Piece piece = board.getPiece(newPosition);

            if (piece != null && !newPosition.equals(target)) {
                return false;
            }

            if (newPosition.equals(target) && (piece == null || piece.getSide() != side)) {
                return true;
            }

            if (newPosition.equals(target)) { // Moving to cell with an ally piece
                return false;
            }

            return tryMove(newPosition, colDif, rowDif, target);
        } catch (OChessBaseException ex) {
            return false;
        }
    }

    @Override
    boolean threatens(Position target) {
        return isValidMove(target);
    }
}

class Rook extends Piece {
    Rook(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.Rook;
    }

    @Override
    boolean isValidMove(Position to) {
        if (position.getRow() != to.getRow() && position.getColumn() != to.getColumn()) {
            return false;
        }

        boolean rowsEqual = to.getRow() == position.getRow();

        int max = rowsEqual
                ? Math.max(position.getColumn(), to.getColumn())
                : Math.max(position.getRow(), to.getRow());

        int min = rowsEqual
                ? Math.min(position.getColumn(), to.getColumn())
                : Math.min(position.getRow(), to.getRow());

        for (int from = min + 1; from < max; from++) {
            try {
                Position pos = rowsEqual
                        ? new Position(from, to.getRow())
                        : new Position(to.getColumn(), from);

                if (board.isOccupied(pos)) {
                    return false;
                }
            } catch (OChessBaseException e) {
                return false;
            }
        }

        Piece piece = board.getPiece(to);
        return piece == null || piece.getSide() == oppositeSide();
    }

    @Override
    boolean threatens(Position target) {
        return isValidMove(target);
    }
}

class Queen extends Piece {
    Queen(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.Queen;
    }

    @Override
    boolean isValidMove(Position to) {
        if (to.getColumn() == position.getColumn() || to.getRow() == position.getRow()) {
            return Piece.createRook(board, position, side).isValidMove(to);
        }

        return Piece.createBishop(board, position, side).isValidMove(to);
    }

    @Override
    boolean threatens(Position target) {
        return isValidMove(target);
    }
}

class King extends Piece {
    King(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.King;
    }

    @Override
    boolean isValidMove(Position to) {
        Piece pieceAtLoc = board.getPiece(to);


        if ((pieceAtLoc == null || pieceAtLoc.getSide() == oppositeSide())) {
            try {
                board.moveToNoCheck(position, to);
                if (board.isCellThreatened(to, oppositeSide())) {
                    return false;
                }
            } finally {
                board.moveToNoCheck(to, position);

                if (pieceAtLoc != null) {
                    board.placeNoCheck(pieceAtLoc.getPosition(), pieceAtLoc);
                }
            }
        }

        if (pieceAtLoc != null && pieceAtLoc.getSide() == side) {
            return false;
        }

        int rowDif = Math.abs(to.getRow() - position.getRow());
        int colDif = Math.abs(to.getColumn() - position.getColumn());

        return rowDif == 1 && colDif == 1 || rowDif == 0 && colDif == 1 || rowDif == 1 && colDif == 0;
    }

    @Override
    boolean threatens(Position target) {
        return isValidMove(target);
    }
}


