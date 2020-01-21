package com.mcp.ochess.net;

public class LobbyAction {
    private ActionType actionType;
    private String content;
    private String sender;

    public LobbyAction(ActionType actionType, String sender, String content) {
        this.actionType = actionType;
        this.sender = sender;
        this.content = content;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
    
    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
