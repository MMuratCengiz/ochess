package com.mcp.ochess.game;

import com.mcp.ochess.exceptions.OChessBaseException;

import java.util.HashMap;

public class Game {
    private static final Object SYNC = new Object();
    private static final HashMap<String, Game> games = new HashMap<>();

    private Game() {

    }

    public static void createNewGame(String lobbyId) throws OChessBaseException {
        synchronized (SYNC) {
            if (games.containsKey(lobbyId)) {
                throw new OChessBaseException(String.format("A game for this lobby(%s) already exists", lobbyId));
            }

            games.put(lobbyId, new Game());
        }
    }

    public static Game getGame(String lobbyId) throws OChessBaseException {
        synchronized (SYNC) {
            Game game = games.get(lobbyId);
            if (game == null) {
                throw new OChessBaseException(String.format("A game with lobby id: %s does not exist.", lobbyId));
            }
            return game;
        }
    }


}
