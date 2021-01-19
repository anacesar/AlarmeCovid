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

    public Worker(Data data, ClientConnection cc){
        this.data = data;
        this.client = cc;
        this.log = false;
    }

    @Override
    public void run() {
        try (client) {

            /** authentication **/
            while( !log ) {
                String msg = new String(client.receive());
                String[] request = msg.split(";");
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
                    case "logout":
                        return;
                    default:
                        break;
                }
            }
            data.addToNotification(username, client);
            client.send("Success with Login");
            System.out.println("sended success message");

            while(true){

            }

                //comunicar positivo

                //get num pessoas numa localizacao

                //notificar quando estao 0 pessoas

                //atualizar localizacao

                //get num pessoas doentes numa localizacao (cliente especial)
            } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public void run() {
        String[] request;
        try{
            Task task = null;
            request = in.readLine().split(";");
            // autenticar
            while( !log ){
                switch (request[0]){
                    case "A" : try{
                        soundcloud.authentication(request[1], request[2]);
                        this.username = request[1];
                        log = true;
                    }catch (InvalidLoginException l){
                        out.println("e;" + l.getMessage());
                        out.flush();
                    }
                        break;
                    case "R" : try{
                        soundcloud.registration(request[1],request[2]);
                        this.username = request[1];
                        log = true;
                    }catch (AlreadyRegistedException r){
                        out.println("e;" + r.getMessage());
                        out.flush();
                    }
                        break;
                    case "N" : this.log = true;
                        this.soundcloud.addToNotifications(request[1], this.out);
                        return;
                    case "L" : return;
                }
                if( !log ) {
                    try {
                        request = in.readLine().split(";");
                    }catch (NullPointerException np){
                        return;
                    }
                }
            }
            out.println("Login com sucesso");
            out.flush();

            // Username = getUser()
            // Adicionar correspodencia username socket

            UploadState uploadState;
            long arrival;
            TimeTask time_task;
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

