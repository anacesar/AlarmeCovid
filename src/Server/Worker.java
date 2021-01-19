package Server;

import Client.ClientConnection;

import Data.Data;
import exceptions.*;

import java.io.IOException;
import java.net.SocketException;

public class Worker implements Runnable {
    private Data data;
    private final ClientConnection client;
    private String username;
    private boolean log;

    public Worker(Data data, ClientConnection cc){
        this.data = data;
        this.client = cc;
        this.log = false;
    }

    @Override
    //todo its ending server thread when client logs out
    public void run() {
        String msg;
        String[] request;
        try (client) {
            /* authentication */
            while( !log ) {
                msg = new String(client.receive());
                request = msg.split(";");
                System.out.println(msg);
                switch(request[0]) {
                    case "login":
                        try {
                            data.authentication(request[1], request[2]);
                            this.username = request[1];
                            log = true;
                        } catch(InvalidLoginException l) {
                            client.send("e;" + l.getMessage());
                        }
                        break;
                    case "register":
                        try {
                            data.registration(request[1], request[2], request[3]);
                            this.username = request[1];
                            log = true;
                        } catch(AlreadyRegistedException | SpecialPasswordInvalidException e) {
                            client.send("e;" + e.getMessage());
                        }
                        break;
                    case "exit":
                        return;
                }
            }
            data.addToNotification(username, client);
            client.send("Success with Login");

            //comunicar positivo

            //get num pessoas numa localizacao

            //notificar quando estao 0 pessoas

            //atualizar localizacao

            //get num pessoas doentes numa localizacao (cliente especial)

            while( log ) {
                try {
                    msg = new String(client.receive());
                    request = msg.split(";");
                } catch(NullPointerException np) {
                    log = false;
                    return;
                }
                switch(request[0]) {
                    case "update":
                        if(!request[0].equals(username)) System.out.println("something is really wrong");
                        data.update_location(username, Integer.parseInt(request[1]));
                        //task = new SearchTask(soundcloud, out, request[1], request);
                        break;
                    case "view":
                        try{
                            int nr = data.nr_people_location(Integer.parseInt(request[1]));
                            client.send(String.valueOf(nr));
                        }catch(InvalidLocationException e){ client.send("e;" + e.getMessage());}
                        break;
                    case "positive":
                        //task = new UploadTask(soundcloud, out, client, request, uploadState);
                        break;
                    case "download" :
                        //data.downloadMap();
                        break;
                    case "map" : //check for N in matrix

                        break;

                    case "logout": {
                        log = false;
                        this.data.sendNotification(this.username, "stop");
                        this.data.removeNotification(this.username);
                        break;
                    }
                }
            }
        } catch(NullPointerException | SocketException n) {
            log = false;
        } catch (IOException io){
        io.printStackTrace();

    } finally {
        try {
            if(!log){
                System.out.println("Shutting down client...");
                client.close();
                System.out.println("Client closed.");
            }
        }catch (IOException io){
            io.printStackTrace();
        }
    }
    }

    /*
    public void run() {
            while( log ){
                try {
                    request = in.readLine().split(";");
                    uploadState = new UploadState();
                    arrival = System.currentTimeMillis();
                }catch (NullPointerException np){
                    log = false;
                    return;
                }
                switch (request[0]){
                    case "C" : task = new SearchTask(soundcloud,out,request[1],request);
                        uploadState.changeUploadFinished();
                        break;
                    case "D" : task = new DownloadTask(soundcloud,Integer.parseInt(request[1]), client, out);
                        uploadState.changeUploadFinished();
                        break;
                    case "U" : task = new UploadTask(soundcloud, out, client, request, uploadState);
                        break;
                    case "L" : {
                        log = false;
                        this.soundcloud.sendNotification(this.username, "stop");
                        this.soundcloud.removeNotification(this.username);
                        uploadState.changeUploadFinished();
                    }
                }
                if( log ) {
                    time_task = new TimeTask(arrival, task);
                    tasks.put(time_task);
                }

                while(!uploadState.isUploadFinished()){
                    Thread.sleep(100);
                }
            }
        } catch (NullPointerException | SocketException n){
            log = false;
        } catch (IOException | InterruptedException io){
            io.printStackTrace();
        } finally {
            try {
                if(!log){
                    System.out.println("Shutting down client...");
                    client.shutdownOutput();
                    client.shutdownInput();
                    client.close();
                    System.out.println("Client closed.");
                }
            }catch (IOException io){
                io.printStackTrace();
            }
        }
    }
    */
}

