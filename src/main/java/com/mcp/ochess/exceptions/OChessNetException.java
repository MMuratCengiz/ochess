package com.mcp.ochess.exceptions;

import java.io.IOException;

public class OChessNetException extends OChessBaseException {
    public OChessNetException(String message) {
        super(message);
    }

    public OChessNetException(Exception ex) {
        super(ex.getMessage());
    }
}
