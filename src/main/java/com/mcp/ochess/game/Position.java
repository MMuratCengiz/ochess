package com.mcp.ochess.game;

import com.mcp.ochess.exceptions.OChessBaseException;

public class Position {
    private int row;
    private int column;

    public Position(int column, int row) throws OChessBaseException {
        this.column = column;
        this.row = row;

        if (column > 8 || row > 8) {
            throw new OChessBaseException("Requested position is out of bounds.");
        }
    }

    public static Position fromString(String position) throws OChessBaseException {
        Position result = new Position(
                position.charAt(0) - 64,
                Integer.parseInt(position.charAt(1) + "")
        );

        return result;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public int hashCode() {
        return (((char) column + 64) + "" + row).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Position)) {
            return false;
        }

        return ((Position) obj).getColumn() == column && ((Position) obj).getRow() == row;
    }
}
