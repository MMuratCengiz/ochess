package com.mcp.ochess.model;

public class ActionResult {
    protected String type;

    public ActionResult(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
