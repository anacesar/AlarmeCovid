package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        /* Socket conectado na porta 12345 e com o IP 127.0.0.1 (localhost) */
        Socket socket = new Socket(InetAddress.getLocalHost(), 12345);

        /* Create ClientConnection */
        ClientConnection clientConnection = new ClientConnection(socket);

        /* Create ClientStub */
        AlarmeCovid_Stub alarmeCovidStub = new AlarmeCovid_Stub(clientConnection);

        /* Launch the first menu for user interaction */
        ClientController controller = new ClientController(alarmeCovidStub);
        controller.start(); //cliente tem interacao com o user

        /* Close Comunication */
        alarmeCovidStub.close();
    }
}