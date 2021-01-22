package Server;

import Client.ClientConnection;
import Data.Data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;


public class Server {
    private static ServerSocket serverSocket;
    private static ServerSocket m_serverSocket;
    private static Data data;
    private static volatile boolean shutdown = false;

    public static void main(String[] args) {

        try {
            System.out.println("Initializing server...");
            data = new Data();

            new Thread(() -> { //thread to accept serversocket communications with users
                try {
                    serverSocket = new ServerSocket(12345);

                    while(!shutdown) {
                        Socket clientSocket = serverSocket.accept();
                        ClientConnection clientConnection = new ClientConnection(clientSocket);

                        new Thread(new Worker(data, clientConnection)).start();
                    }
                } catch(IOException e) { e.printStackTrace();}
            }).start();


            new Thread(() -> { //thread to accept users notification socket
                try {
                    m_serverSocket = new ServerSocket(12346);

                    while(!shutdown) {
                        Socket clientSocket = m_serverSocket.accept();
                        ClientConnection clientConnection = new ClientConnection(clientSocket);

                        new Thread(new Worker(data, clientConnection)).start();
                    }
                } catch(IOException e) { e.printStackTrace();}
            }).start();
        }catch(Exception e){ e.printStackTrace();}

    }

        public static void shutdown(){
            shutdown = true;
            System.out.println("Shutting down the server...");
            System.out.println("Closing server socket...");
            try{
                serverSocket.close();
                System.out.println("Server Socket closed");
            }catch (IOException io){
                io.printStackTrace();
            }
        }
}
