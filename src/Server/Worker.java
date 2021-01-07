package Server;

import Client.ClientConnection;
import Data.Data;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Worker {
    private Data data;
    private ClientConnection client;
    private String username;
    private boolean log;
    private MinHeap tasks;


    public Worker(ClientConnection cc, Data data){

    }
}
