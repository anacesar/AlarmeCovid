package Server;

import Client.ClientConnection;

import Data.Data;

public class Worker implements Runnable {
    private Data data;
    private final ClientConnection client;
    private String username;
    private boolean log;

    public Worker(ClientConnection cc, Data data){
        this.data = data;
        this.client = cc;
    }


    @Override
    public void run() {
        try (client) {
            for (;;) {
                byte[] b = client.receive();
                String msg = new String(b);
                System.out.println("Replying to: " + msg);
                //Autenticar Utilizador

                //Registar Utilizador

                //comunicar positivo

                //get num pessoas numa localizacao

                //notificar quando estao 0 pessoas

                //atualizar localizacao

                //get num pessoas doentes numa localizacao (cliente especial)
                client.send(msg.toUpperCase().getBytes());
            }
        } catch (Exception ignored) { }
    };





}
