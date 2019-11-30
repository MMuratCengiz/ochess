package com.mcp.ochess.game;

public abstract class Piece {
    Position position;
    PieceKind kind;
    Side side;
    boolean isAlive;
    Board board;

    Piece(Board board, Position position, Side side) {
        this.position = position;
        this.isAlive = false;
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
        return new Pawn(board, position, side);
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

    public boolean isAlive() {
        return isAlive;
    }

    public boolean moveTo(Board board, Position to) {
        if (! isValidMove(to)) {
            return false;
        }

        position = to;
        return true;
    }

    public void kill() {
        isAlive = false;
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
    protected boolean isValidMove(Board board, Position to) {
        int dif = side == Side.White ? 1 : -1;
        Piece piece = board.getPiece(to);

        if (threatens(board, to) && piece != null && piece.getSide() != side) {
            return true;
        }

        if (board.isOccupied(to)) {
            return false;
        }

        return position.getRow() + dif == to.getRow();
    }

    @Override
    boolean threatens(Board board, Position target) {
        int dif = side == Side.White ? 1 : -1;

        if (position.getRow() == target.getRow() + dif && Math.abs(position.getColumn() - target.getColumn()) == 1) {
            
        }
        return false;
    }
}

class Knight extends Piece {
    Knight(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.Knight;
    }

    @Override
    boolean isValidMove(Board board, Position to) {
        // Simple logic here, either it should go two rows up/down and 1 left/right or vice-a-versa.
        return (Math.abs(position.getRow() - to.getRow()) + Math.abs(position.getColumn() - to.getColumn())) == 3;
    }
}

class Bishop extends Piece {
    Bishop(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.Bishop;
    }

    @Override
    boolean isValidMove(Board board, Position to) {
        return Math.abs(position.getColumn() - to.getColumn()) == Math.abs(position.getRow() - to.getRow());
    }
}

class Rook extends Piece {
    Rook(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.Rook;
    }

    @Override
    boolean isValidMove(Board board, Position to) {
        return to.getRow() == position.getRow() || to.getColumn() == position.getColumn();
    }
}

class Queen extends Piece {
    Queen(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.Queen;
    }
}

class King extends Piece {
    King(Board board, Position position, Side side) {
        super(board, position, side);
        kind = PieceKind.King;
    }
}


