package Server;

import Client.ClientConnection;
import Client.ClientConnection.Message;
import Data.Data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static ServerSocket serverSocket;
    private static Data data;
    private static volatile boolean shutdown = false;

    public static void main(String[] args) throws Exception {
        System.out.println("Initializing server...");
        serverSocket = new ServerSocket(12345);
        data = new Data();

        while(!shutdown) {
            Socket clientSocket = serverSocket.accept();
            ClientConnection clientConnection = new ClientConnection(clientSocket);

            new Thread(new Worker(data,clientConnection)).start();
        }
    }

        public static void shutdown(){
            boolean shutdown = true;
            System.out.println("Shutting down the server...");
            System.out.println("Shutting down thread pool...");
            //threadPool.shutdownPool();
            //soundCloud.warnClientsAboutShutdown();
            System.out.println("ThreadPool ok");
            System.out.println("Closing server socket...");
            try{
                serverSocket.close();
                System.out.println("Socket ok");
            }catch (IOException io){
                io.printStackTrace();
            }
        }
}
