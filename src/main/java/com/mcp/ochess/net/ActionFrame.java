package com.mcp.ochess.net;

import com.mcp.ochess.exceptions.OChessNetException;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class ActionFrame {
    protected static HashMap<Integer, Class> registeredFrames = new HashMap<>();

    public static ActionFrame create(Socket socket) throws OChessNetException {
        ActionFrame frame = null;
        ByteBuffer readBuffer = null;

        try {
            InputStream stream = socket.getInputStream();
            // Read message type
            byte[] typeBuffer = new byte[4];
            int read = stream.read(typeBuffer);
            int type = ByteBuffer.wrap(typeBuffer, 0, read).getInt();

            // Read message length
            byte[] sizeBuffer = new byte[4];
            read = stream.read(sizeBuffer);
            int size = ByteBuffer.wrap(sizeBuffer, 0, read).getInt(); // Todo safe check read = 4

            byte[] buffer = new byte[size];
            read = stream.read(buffer);
            readBuffer = ByteBuffer.wrap(buffer, 0, read);

            Class<ActionFrame> cls = registeredFrames.get(type);
            frame = cls.getConstructor().newInstance();
        } catch (Exception ex) {
            throw new OChessNetException(ex);
        }


        if (frame == null) {
            throw new OChessNetException("Invalid frame!");
        }

        frame.read(readBuffer);
        return frame;
    }

    public void write(OutputStream stream) throws IOException {
        byte[] message = toByteArray();

        ByteBuffer writer = ByteBuffer.allocate(8);
        writer.putInt(getId());
        writer.putInt(message.length);

        stream.write(writer.array());
        stream.write(message);
    }

    protected abstract byte[] toByteArray();
    protected abstract int getId();
    abstract void read(ByteBuffer message);
}
