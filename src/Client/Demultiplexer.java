package Client;

import Client.ClientConnection.Message;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer {
    private ClientConnection conn;
    private Map<Integer, Notification> flagMap = new HashMap<>(); //Integer (type of notification)-> messages
    private Lock lock = new ReentrantLock();
    private Condition wait_notifications = lock.newCondition();
    private IOException exception = null;

    public class Notification{
        int type; /* 1-> location empty     2-> risk contact */
        Queue<byte []> queue = new ArrayDeque<>();
    }

    public Demultiplexer(ClientConnection conn){
        this.conn = conn;

    }

    Runnable interaction = () -> {
        try {
            Message m = conn.receive();
            /* check messages tag */

            switch(m.type){
                case 1: //risk contact notification
                    break;
                case 2: //location empty notification
                    break;
                default:
                    break;
            }
            lock.lock();
            try{
                FlagFrame ff = get(frame.tag);
                ff.queue.add(frame.data);
                ff.c.signal();
            }finally {
                lock.unlock();
            }

        } catch(IOException e) {
            lock.lock(); //e uma variavel partilhada
            try{
                exception = e;
                flagMap.forEach((k, ff) -> ff.c.signalAll());
            }finally {
                lock.unlock();
            }
        }
    };

    Runnable notifier = () -> {
        lock.lock();
        try {
            while(flagMap.isEmpty()) wait_notifications.await();
            /* do stuff with notifications */

        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    };


    public void start() {
        Runnable notifier = () -> {
            lock.lock();
            try {
                while(flagMap.isEmpty()) wait_notifications.await();

            } catch(InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };
    }



    //demultiplexer verifica tipo de mensagens recebida pelo servidor

    /* send different types of messages from user */

    out.write("login username password")

    public void send(){
        conn.send();
    }

}
