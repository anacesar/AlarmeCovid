package Client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class ClientConnection implements AutoCloseable{
    private Socket socket;        /** Socket do cliente **/
    private DataOutputStream out; /** Extremidade de escrita **/
    private DataInputStream in;   /** Extremidade de leitura **/
    private ReentrantLock wlock = new ReentrantLock();
    private ReentrantLock rlock = new ReentrantLock();

    public class Message{
        int type;
        byte[] data;
    }



    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    @Override
    public void close() throws Exception {

    }



    /*metodos para enviar resultados para o servidor */
}
