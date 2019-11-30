package com.mcp.ochess.task;

public interface IOnTaskDone<Result> {
    void onTaskComplete(Result result);
    void onTaskError(Exception e);
}
