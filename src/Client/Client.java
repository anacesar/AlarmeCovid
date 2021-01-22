package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    //todo socket must be connected in clientConnection not in client main
    public static void main(String[] args) throws IOException {
        /* Socket conectado na porta 12345 e com o IP 127.0.0.1 (localhost) */
        Socket socket = new Socket(InetAddress.getLocalHost(), 12345);

        /* Create ClientConnection */
        ClientConnection clientConnection = new ClientConnection(socket);

        /* Create Demultiplexer */
        Demultiplexer demultiplexer = new Demultiplexer(clientConnection);
        //demultiplexer.start(); //o cliente vai estar pronto para comunicar com sev e esperar por not

        /* Launch the first menu */
        ClientController controller = new ClientController(demultiplexer);
        controller.start(); //cliente tem interacao com o user

        /* Close Comunication */
        demultiplexer.close();
    }
}