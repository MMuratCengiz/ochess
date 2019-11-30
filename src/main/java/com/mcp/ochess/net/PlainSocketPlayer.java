package com.mcp.ochess.net;

import com.mcp.ochess.exceptions.OChessNetException;

import java.io.IOException;
import java.net.Socket;

public class PlainSocketPlayer implements IPlayer {
    private int player = 1;
    private Socket host;

    @Override
    public void setPlayer(int player) {
        this.player = player;
    }

    @Override
    public void setHost(String host, int port) throws OChessNetException {
        try {
            this.host = new Socket(host, port);
        } catch (Exception ex) {
            throw new OChessNetException(ex);
        }
    }

    @Override
    public void move(String from, String to) throws OChessNetException {
        MoveFrame frame = new MoveFrame(player, from, to);
        try {
            frame.write(this.host.getOutputStream());
        } catch (Exception ex) {
            throw new OChessNetException(ex);
        }
    }

    @Override
    public void close() {
        try {
            this.host.close();
        } catch (IOException ignored) {}
    }
}
