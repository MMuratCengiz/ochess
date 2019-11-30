package com.mcp.ochess.exceptions;

public class OChessBaseException extends Exception {
    public OChessBaseException(String message) {
        super(message);
    }

    public String source() {
        return "";
    }
}
