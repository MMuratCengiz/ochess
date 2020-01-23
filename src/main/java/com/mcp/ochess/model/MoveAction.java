package com.mcp.ochess.model;

public class MoveAction {
    private String sender;
    private int lobbyId;
    private String from;
    private String moveId;
    private String to;

    public MoveAction(String moveId, int lobbyId, String sender, String from, String to) {
        this.moveId     = moveId;
        this.sender     = sender;
        this.lobbyId    = lobbyId;
        this.from       = from;
        this.to         = to;
    }

    public String getSender() {
        return sender;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public String getMoveId() {
        return moveId;
    }

    public void setMoveId(String moveId) {
        this.moveId = moveId;
    }
}
