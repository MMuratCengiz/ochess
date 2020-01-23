package com.mcp.ochess.model;

public class MoveResult {
    private String type = "MoveResult";

    private boolean isValidMove;
    private String actionResult;
    private String moveId;

    public String getMoveId() {
        return moveId;
    }

    public void setMoveId(String moveId) {
        this.moveId = moveId;
    }

    public boolean isValidMove() {
        return isValidMove;
    }

    public void setValidMove(boolean validMove) {
        isValidMove = validMove;
    }

    public String getActionResult() {
        return actionResult;
    }

    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }

    public String getType() {
        return type;
    }
}
