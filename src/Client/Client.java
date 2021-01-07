package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        /* Socket conectado na porta 12345 e com o IP 127.0.0.1 (localhost) */
        Socket socket = new Socket(InetAddress.getLocalHost(), 12345);

        /* Buffer to read from stdin */
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

        /* Create ClientConnection */
        Demultiplexer demultiplexer = new Demultiplexer(new ClientConnection(socket));

        /* Launch the first menu */
        Menu menu = new Menu(demultiplexer);
        menu.firstMenu();

        /* wait for it to end ... */
        buffer.close();

        /* Close socket */
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }
}