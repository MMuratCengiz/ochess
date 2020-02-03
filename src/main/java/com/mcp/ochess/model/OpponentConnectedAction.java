package com.mcp.ochess.model;

public class OpponentConnectedAction extends ActionResult {
    private String who;

    public OpponentConnectedAction() {
        super("OpponentConnected");
    }

    public void setWho(String who) {
        this.who = who;
    }
    public String getWho() {
        return who;
    }
}
