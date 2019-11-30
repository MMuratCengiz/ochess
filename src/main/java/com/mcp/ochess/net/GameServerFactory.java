package com.mcp.ochess.net;

import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.exceptions.OChessNetException;

import java.util.HashMap;

public class GameServerFactory {
    private final ServerType type;
    private HashMap<String, IGameServer> servers = new HashMap<>();

    public GameServerFactory(ServerType type)  {
        this.type = type;
    }

    public void createLobby(String lobbyId, int port) throws OChessNetException {
        switch (type) {
            case PlainSocket:
                PlainSocketGameServer server = new PlainSocketGameServer();
                server.init(port);
                servers.put(lobbyId, server);
                break;
            case REST:
                break;
        }
    }

    public IGameServer getServer(String lobbyId) throws OChessBaseException {
        if (! servers.containsKey(lobbyId)) {
            throw new OChessBaseException("No server found!");
        }

        return servers.get(lobbyId);
    }
}
