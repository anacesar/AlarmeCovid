package Client;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class ClientConnection implements AutoCloseable {
    private Socket socket;        /* Socket do cliente */
    private DataOutputStream out; /* Extremidade de escrita */
    private DataInputStream in;   /* Extremidade de leitura */
    private ReentrantLock wlock = new ReentrantLock();
    private ReentrantLock rlock = new ReentrantLock();

    private final int MAXSIZE = 8192; //maxsize for map download

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }


    public static class Message{
        int tag; //0 --> sick notification |  1 --> place notification | 2 --> download map
        byte[] data;

        public Message(int tag, byte[] data) {
            this.tag = tag;
            this.data = data;
        }

        public void serialize(DataOutputStream out) throws IOException {
            out.writeInt(tag);
            out.writeInt(data.length);
            out.write(data);
        }

        public static Message deserialize(DataInputStream in) throws IOException {
            int tag = in.readInt();
            byte[] data = new byte[in.readInt()];
            in.readFully(data);
            return new Message(tag, data);
        }


        @Override
        public String toString() {
            return "Message{" +
                    "tag=" + tag +
                    ", data=" + Arrays.toString(data) +
                    '}';
        }
    }

    public void send(byte[] data) throws IOException {
        try {
            wlock.lock();
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } finally {
            wlock.unlock();
        }
    }

    public void send(String data) throws IOException {
        try {
            wlock.lock();
            byte[] dataB = data.getBytes();
            out.writeInt(dataB.length);
            out.write(dataB);
            out.flush();
        } finally {
            wlock.unlock();
        }
    }

    public void send(Message message) throws IOException{
        try {
            wlock.lock();
            message.serialize(out);
            out.flush();
        }finally {
            wlock.unlock();
        }
    }

    public byte[] receive() throws IOException {
        byte[] data;
        try {
            rlock.lock();
            data = new byte[in.readInt()];
            in.readFully(data);
        }
        finally {
            rlock.unlock();
        }
        return data;
    }

    public Message receiveMessage() throws IOException {
        try {
            rlock.lock();
            Message m = Message.deserialize(in);
            return m;
        }finally {
            rlock.unlock();
        }
    }

    public void close() throws IOException {
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
    }
}
