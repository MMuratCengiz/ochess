package com.mcp.ochess.task;

import com.mcp.ochess.exceptions.OChessNetException;

public interface ITask<Result> extends Runnable {
    Result getResult() throws OChessNetException;
    Exception getError();
}
