package com.mcp.ochess.model;

public class MoveResult extends ActionResult {
    private boolean isValidMove;
    private String actionResult;
    private String moveId;
    private String from;
    private String to;
    private String kill;
    private String sender;

    public MoveResult() {
        super("MoveResult");
    }

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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public String getSender() {
        return sender;
    }

    public String getKill() {
        return kill;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setKill(String kill) {
        this.kill = kill;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
