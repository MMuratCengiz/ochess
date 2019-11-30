package com.mcp.ochess.net;

import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.exceptions.OChessNetException;
import com.mcp.ochess.game.IGameEvents;
import com.mcp.ochess.task.IOnTaskDone;
import com.mcp.ochess.task.ITask;
import com.mcp.ochess.task.TaskManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PlainSocketGameServer implements IGameServer, IOnTaskDone<Socket> {
    private ServerSocket serverSocket;
    private Socket p1;
    private Socket p2;
    private Turn turn;

    private StatusCode code;
    private int port;
    private IGameEvents events;

    @Override
    public void init(int port) throws OChessNetException {
        try {
            this.turn = Turn.White;
            this.port = port;
            code = StatusCode.GettingReady;
            createSocket();
        } catch (IOException ex) {
            throw new OChessNetException(ex.getMessage());
        }
    }

    @Override
    public StatusCode getStatusCode() {
        return code;
    }

    @Override
    public void setGameEventsListener(IGameEvents events) {
        this.events = events;
    }

    private void createSocket() throws IOException {
        serverSocket = new ServerSocket(port);
        code = StatusCode.WaitingForP1;
        TaskManager.initNewTask(new StartAccept(serverSocket), this);
    }

    @Override
    public void playTurn() throws OChessBaseException {
        ActionFrame frame = null;
        switch (turn) {
            case White:
                frame = ActionFrame.create(p1);
                break;
            case Black:
                frame = ActionFrame.create(p2);
                break;
        }

        if (frame.getId() == MoveFrame.ID) {
            MoveFrame moveFrame = (MoveFrame) frame;
            events.onMove(moveFrame.getPlayer(), moveFrame.getMoveFrom(), moveFrame.getMoveTo());
        } else if (frame.getId() == 1) {
            // Not yet implemented, this should be a surrender frame
        }

        turn = turn == Turn.White ? Turn.Black : Turn.White;
    }

    public void close() throws OChessNetException {
        try {
            code = StatusCode.Closed;
            serverSocket.close();
            p1.close();
            p2.close();
        } catch (Exception e) {
            throw new OChessNetException(e);
        }
    }

    @Override
    public void onTaskComplete(Socket socket) {
        if (p1 == null) {
            p1 = socket;
            TaskManager.initNewTask(new StartAccept(serverSocket), this);
            code = StatusCode.WaitingForP2;
        } else {
            p2 = socket;
            code = StatusCode.Playing;
        }
    }

    @Override
    public void onTaskError(Exception e) {
        // todo read accept error
    }
}

enum Turn {
    White,
    Black
}

class StartAccept implements ITask<Socket> {
    private final ServerSocket serverSocket;
    private Socket result;
    private IOException ex;

    StartAccept(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            result = serverSocket.accept();
        } catch (IOException e) {
            this.ex = e;
        }
    }

    @Override
    public Socket getResult() {
        return result;
    }

    @Override
    public Exception getError() {
        return ex;
    }
}