package Server;

import Client.ClientConnection;
import Data.Data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

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
            ClientConnection c = new ClientConnection(clientSocket);

            Runnable worker = () -> {
                try (c) {
                    for (;;) {
                        Frame frame = c.receive();
                        //System.out.println("frame received: " + frame.tag + " : " + new String(frame.data));
                        int tag = frame.tag;
                        String data = new String(frame.data);
                        if (frame.tag == 0)
                            System.out.println("Got one-way: " + data);
                        else if (frame.tag % 2 == 1) {
                            System.out.println("Replying to: " + data);
                            c.send(frame.tag, data.toUpperCase().getBytes());
                        } else {
                            for (int i = 0; i < data.length(); ++i) {
                                String str = data.substring(i, i+1);
                                System.out.println("Streaming: " + str);
                                c.send(tag, str.getBytes());
                                Thread.sleep(100);
                            }
                            //enviar mensagem de ultimo byte a 0
                            c.send(tag, new byte[0]);
                        }
                    }
                } catch (Exception ignored) { }
            };

            for (int i = 0; i < WORKERS_PER_CONNECTION; ++i)
                new Thread(worker).start();
        }

        public static void shutdown(){
            shutdown = true;
            System.out.println("Shutting down the server...");
            System.out.println("Shutting down thread pool...");
            //threadPool.shutdownPool();
            soundCloud.warnClientsAboutShutdown();
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
