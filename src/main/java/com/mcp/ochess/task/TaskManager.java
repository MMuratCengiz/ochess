package com.mcp.ochess.task;

import com.mcp.ochess.exceptions.OChessNetException;

import java.io.IOException;

public class TaskManager<Result> implements Runnable {
    private final IOnTaskDone<Result> onTaskDone;
    private final ITask<Result> task;


    public static <T> void initNewTask(ITask<T> task, IOnTaskDone<T> onTaskDone) {
        new TaskManager<T>(task, onTaskDone);
    }

    private TaskManager(ITask<Result> task, IOnTaskDone<Result> onTaskDone) {
        this.task = task;
        this.onTaskDone = onTaskDone;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            Thread taskRunner = new Thread(task);
            taskRunner.start();
            taskRunner.join();

            if (task.getError() != null) {
                onTaskDone.onTaskError(task.getError());
            } else {
                onTaskDone.onTaskComplete(task.getResult());
            }
        } catch (InterruptedException | OChessNetException e) {
            onTaskDone.onTaskError(e);
        }
    }
}
