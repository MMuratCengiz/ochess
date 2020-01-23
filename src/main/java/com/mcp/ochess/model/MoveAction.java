package com.mcp.ochess.model;

public class LobbyAction {
    private ActionType actionType;
    private String sender;
    private String lobbyId;
    private String content;

    public LobbyAction(ActionType actionType, String lobbyId, String sender, String content) {
        this.actionType = actionType;
        this.sender = sender;
        this.lobbyId = lobbyId;
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

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getLobbyId() {
        return lobbyId;
    }
}
