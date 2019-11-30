package com.mcp.ochess.net;

import com.mcp.ochess.exceptions.OChessNetException;

import java.io.IOException;

public interface IPlayer {
    public void setPlayer(int player);
    public void setHost(String host, int port) throws OChessNetException;
    public void move(String from, String to) throws OChessNetException;
    public void close();
}
