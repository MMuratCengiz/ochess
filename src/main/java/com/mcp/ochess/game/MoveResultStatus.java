package com.mcp.ochess.game;

public enum MoveResultStatus {
    PIECE_DOES_NOT_EXIST,
    INVALID_MOVE,
    INVALID_MOVE_KING_THREATENED,
    MOVED_TO_EMPTY,
    KILL,
    CHECK,
    PAWN_TRANSFORM,
    CHECKMATE,
    CASTLING_MOVE,
    EN_PASSANT_MOVE,
}
