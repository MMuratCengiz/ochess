package com.mcp.ochess.model;

public class GameOverMessage extends ActionResult {
    private String reason;

    public GameOverMessage() {
        super("GameOver");
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
