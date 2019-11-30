package com.mcp.ochess.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MoveFrame extends ActionFrame {
    public static final int ID = 1;

    private int player;
    private String moveFrom;
    private String moveTo;

    static {
        registeredFrames.put(MoveFrame.ID, MoveFrame.class);
    }

    public MoveFrame() {
        // Used in reflection
    }

    public MoveFrame(int player, String moveFrom, String moveTo) {
        this.player = player;
        this.moveFrom = moveFrom;
        this.moveTo = moveTo;
    }

    public int getPlayer() {
        return player;
    }

    public String getMoveFrom() {
        return moveFrom;
    }

    public String getMoveTo() {
        return moveTo;
    }

    @Override
    protected byte[] toByteArray() {
        // 9 = 1(player) + 4(moveFrom.length) + 4(moveTo.length)
        ByteBuffer buffer = ByteBuffer.allocate(9 + moveFrom.length() + moveTo.length());
        buffer.put((byte) this.player);
        buffer.putInt(this.moveFrom.length());
        buffer.put(this.moveFrom.getBytes());
        buffer.putInt(this.moveTo.length());
        buffer.put(this.moveTo.getBytes());

        return buffer.array();
    }

    @Override
    protected int getId() {
        return 1;
    }

    @Override
    void read(ByteBuffer message) {
        player = message.get();
        int size = message.getInt();
        byte[] buffer = new byte[size];
        message.get(buffer, 0, size);
        this.moveFrom = new String(buffer);

        size = message.getInt();
        buffer = new byte[size];
        message.get(buffer, 0, size);
        this.moveTo = new String(buffer);
    }
}
