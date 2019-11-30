package com.mcp.ochess.net;

import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.exceptions.OChessNetException;
import com.mcp.ochess.game.IGameEvents;

public interface IGameServer {
    void init(int port) throws OChessNetException;
    StatusCode getStatusCode();
    void setGameEventsListener(IGameEvents events);
    void playTurn() throws OChessBaseException;
    void close() throws OChessNetException;
}
