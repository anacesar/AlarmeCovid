package Client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class ClientConnection implements AutoCloseable {
    private Socket socket;        /** Socket do cliente **/
    private DataOutputStream out; /** Extremidade de escrita **/
    private DataInputStream in;   /** Extremidade de leitura **/
    private ReentrantLock wlock = new ReentrantLock();
    private ReentrantLock rlock = new ReentrantLock();

    public class Message{
        String type;
        byte[] data;

        public Message(byte[] data){
            data = data;
        }

        /* serialize and deserialize methods */
    }

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }


    /*metodos para enviar resultados para o servidor */

    /* send and receive */

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

    public void close() throws IOException {
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
    }


}
