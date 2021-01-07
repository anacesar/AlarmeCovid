package Client;

import java.io.DataOutput;
import java.io.DataOutputStream;
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

}
