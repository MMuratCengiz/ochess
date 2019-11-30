import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.exceptions.OChessNetException;
import com.mcp.ochess.game.IGameEvents;
import com.mcp.ochess.net.*;

class ServerTest implements IGameEvents {
    public static void main(String[] args) throws OChessBaseException {
        GameServerFactory factory = new GameServerFactory(ServerType.PlainSocket);
        factory.createLobby("123", 45213);
        IGameServer server = factory.getServer("123");
        server.setGameEventsListener(new ServerTest());

        while (server.getStatusCode() == StatusCode.GettingReady) {

        }

        new Thread(new Player1()).start();
        new Thread(new Player2()).start();


        while (server.getStatusCode() == StatusCode.WaitingForP1 || server.getStatusCode() == StatusCode.WaitingForP2) {

        }

        while (server.getStatusCode() != StatusCode.Closed) {
            server.playTurn();
        }
//        server.close();
    }

    @Override
    public void onSurrender() {

    }

    @Override
    public void onMove(int player, String from, String to) {
        System.out.print(player + " " + from + " " + to);
    }
}

class Player1 implements Runnable {
    @Override
    public void run() {
        IPlayer player = new PlainSocketPlayer();
        try {
            player.setHost("127.0.0.1", 45213);
            player.move("A6", "A7");
        } catch (OChessNetException ignore) {
        }
    }
}

class Player2 implements Runnable {
    @Override
    public void run() {
        IPlayer player = new PlainSocketPlayer();
        try {
            player.setHost("127.0.0.1", 45213);
            player.move("A2", "A3");
        } catch (OChessNetException ignore) {
        }
    }
}