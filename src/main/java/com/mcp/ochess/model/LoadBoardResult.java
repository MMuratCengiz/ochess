package com.mcp.ochess.model;

public class LoadBoardResult extends ActionResult {
    private String sender;
    private String board;

    public LoadBoardResult() {
        super("Load");
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }
}
