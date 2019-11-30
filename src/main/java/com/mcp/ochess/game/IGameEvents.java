package com.mcp.ochess.game;

public interface IGameEvents {
    void onSurrender();
    void onMove(int player, String from, String to);
}
