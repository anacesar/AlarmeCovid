package Server;

import Client.ClientConnection;
import Data.Data;

import java.io.IOException;

public class MessageWorker implements Runnable {
    private Data data;
    private final ClientConnection client;
    private String username;

    public MessageWorker(Data data, ClientConnection cc){
        this.data = data;
        this.client = cc;
    }


    @Override
    public void run() {
        try{
            //waiting for client to send username
            String msg =  new String(client.receive());
            System.out.println(msg);

            data.addToNotification(msg, client);

            msg = new String(client.receive());
            if(msg.equals("exit")) System.out.println("messageworker bye bye...");

            data.sendNotification(username, new ClientConnection.Message(4, "exit".getBytes()));

        }catch(IOException e){
        }finally {
            try{
                System.out.println("Shutting down notify server for client " + username);
                client.close();
                System.out.println("Notify Server closed");
            }catch(IOException e) {}
        }
    }
}
