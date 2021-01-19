package Server;

import Client.ClientConnection;

import java.util.*;
import java.util.concurrent.locks.*;

public class Notificator {

    private Map<String, ClientConnection> logged_users = new HashMap<>();
    private Lock lock = new ReentrantLock();
    private Condition printNotifications = lock.newCondition();


    public void addToNotification(String username , ClientConnection cc){

    }

    public void sendNotification(){

    }


    public void run(){

    }


}
