package Server;

import Client.ClientConnection;

import Data.Data;
import exceptions.*;

import java.io.IOException;

public class Worker implements Runnable {
    private Data data;
    private final ClientConnection client;
    private String username;
    private boolean log;
    private boolean exit;

    public Worker(Data data, ClientConnection cc){
        this.data = data;
        this.client = cc;
        this.log = false;
        this.exit = false;
    }

    public void before_login() {
        String msg;
        String[] request;
        /* authentication */
        while(!log) {
            try {
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
                        exit = true;
                        return;
                }
            } catch(IOException e) {
                exit = true;
                return;
            }
        }
    }

    public void while_logged() throws IOException{
        String msg;
        String[] request;
        while( log ) {
            System.out.println("log menu");
            try {
                msg = new String(client.receive());
                System.out.println(msg);
                request = msg.split(";");
            } catch(NullPointerException np) {
                log = false;
                return;
            }
            switch(request[0]) {
                case "update":
                    //if(!request[0].equals(username)) System.out.println("something is really wrong");
                    data.update_location(username, Integer.parseInt(request[1]));
                    //task = new SearchTask(soundcloud, out, request[1], request);
                    break;
                case "view":
                    try{
                        int nr = data.nr_people_location(Integer.parseInt(request[1]));
                        System.out.println("nr_people " + Integer.parseInt(request[1]) + " : " + nr);
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
                    System.out.println("logging out client");
                    //this.data.sendNotification(this.username, "stop");
                    this.data.removeNotification(this.username);
                    break;
                }
            }
        }
    }

    @Override
    //todo its ending server thread when client logs out
    public void run() {
        try{
            while(!exit) {
                try {
                    before_login();

                    if(! exit) {
                        data.addToNotification(username, client);
                        client.send("Success with Login");

                        while_logged();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            try {
                System.out.println("Shutting down client...");
                client.send("stop");
                client.close();
                System.out.println("Client closed.");
            } catch(IOException e) {
                e.printStackTrace();
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

