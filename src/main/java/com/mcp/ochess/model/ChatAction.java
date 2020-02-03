package com.mcp.ochess.model;

public class ChatAction extends ActionResult {
    private String from;
    private String message;

    public ChatAction() {
        super("Chat");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
