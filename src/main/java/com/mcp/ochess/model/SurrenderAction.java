package com.mcp.ochess.model;

public class SurrenderAction extends ActionResult {
    private String who;

    public SurrenderAction() {
        super("Surrender");
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }
}
