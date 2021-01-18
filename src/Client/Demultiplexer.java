package Client;

import Client.ClientConnection.Message;
import Data.AlarmCovidInterface;
import exceptions.AlreadyRegistedException;
import exceptions.InvalidLocationException;
import exceptions.InvalidLoginException;
import exceptions.SpecialPasswordInvalidException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AlarmCovidInterface {
    private ClientConnection conn;
    private Map<Integer, Notification> notifications = new HashMap<>(); //Integer (type of notification)-> messages
    private Lock lock = new ReentrantLock();
    private Condition wait_notifications = lock.newCondition();
    private IOException exception = null;

    public class Notification{
        int type; /* 1-> location empty     2-> risk contact */
        Queue<byte []> queue = new ArrayDeque<>();

        public void getQueue() {

        }

    }

    public Demultiplexer(ClientConnection conn){
        this.conn = conn;

    }


    // interaction with the server
    Runnable interaction = () -> {
        try {

            // notificacao quando o servidor tem uma notif p mandar p cliente
            byte[] data = this.conn.receive();
            /* check messages tag */

            /*
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
                Message me = get(frame.tag);
                me.queue.add(frame.data);
                ff.c.signal();
            }finally {
                lock.unlock();
            }
*/
        }catch(IOException e) {
            lock.lock(); //e uma variavel partilhada
            try{
                exception = e;
                //notifications.forEach((k, ff) -> ff..signalAll());
            }finally {
                lock.unlock();
            }
        }
    };

    //wait for notification signal
    Runnable notifier = () -> {
        lock.lock();
        try {
            while(notifications.isEmpty()) wait_notifications.await();
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
                while(notifications.isEmpty()) wait_notifications.await();

            } catch(InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };
    }



    //demultiplexer verifica tipo de mensagens recebida pelo servidor

    /* send different types of messages from user */

    //out.write("login username password");


    /*
    public void send(Message message) throws IOException {
        this.conn.send(message);
    }

     */


    /* login and register */
    public void send(String op, String username, String password) throws IOException {
        String line = op + " " + username + " " + " " + password;
        this.conn.send(line.getBytes());

    }

    /* send of location */
    public void send(String op, int nodo) throws IOException{
        String line = op + " " + nodo;
        this.conn.send(line.getBytes());
    }


    public byte[] receive() throws IOException, InterruptedException {
        return this.conn.receive();
    }

    @Override
    public void registration(String username, String password, String special_password) throws AlreadyRegistedException, SpecialPasswordInvalidException {
        String line = "register;" + username + ";" + password + ";" + special_password;
        try {
            this.conn.send(line.getBytes());

            line = new String(this.conn.receive());
            System.out.println(line);
            String[] answers = line.split(";");
            //check da exception
            if(answers[0].equals("e")) throw new AlreadyRegistedException(answers[1]);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void authentication(String username, String password) throws InvalidLoginException {
        String line = "login;" + username + ";" + password;
        try {
            this.conn.send(line.getBytes());

            line = new String(this.conn.receive());
            System.out.println(line);
            String[] answers = line.split(";");
            if(answers[0].equals("e")) throw new InvalidLoginException(answers[1]);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify_positive(String username) {

    }

    @Override
    public int nr_people_location(int node) throws InvalidLocationException {
        return 0;
    }

    @Override
    public void notify_empty_location(String username, int node) throws InvalidLocationException {

    }

    @Override
    public void update_location(String username, int new_location) {

    }

    @Override
    public void download_map() {

    }

    public void close() throws IOException {
        this.conn.close();
    }

}
