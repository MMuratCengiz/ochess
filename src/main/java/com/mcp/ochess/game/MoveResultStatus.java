package com.mcp.ochess.game;

public enum MoveResultStatus {
    PIECE_DOES_NOT_EXIST,
    INVALID_MOVE,
    MOVED_TO_EMPTY,
    KILL,
    CHECK,
    CHECKMATE,
}
