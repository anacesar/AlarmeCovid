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
    private List<String> answers;
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
        this.answers = new ArrayList<>();
    }


    public void start() {
        new Thread( () -> {
            String message = "";
            while(true){
                try {
                    message = new String(conn.receive());
                    System.out.println(message);
                    lock.lock();
                    try{
                        if(message.charAt(0) == ':'){
                            System.out.println("---------------------------------------------------------------------");
                            System.out.println("Notification " + message);
                            System.out.println("---------------------------------------------------------------------");
                        }else synchronized (answers) {
                            answers.add(message);
                            answers.notify();
                        }
                    }finally {
                        lock.unlock();
                    }

                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String readNext() throws InterruptedException {
        synchronized (answers) {
            while (answers.isEmpty()) {
                answers.wait();
            }
            String next = answers.get(0);
            answers.remove(0);
            return next;
        }
    }

    @Override
    public void registration(String username, String password, String special_password) throws AlreadyRegistedException, SpecialPasswordInvalidException {
        String line = "register;" + username + ";" + password + ";" + special_password;
        try {
            this.conn.send(line.getBytes());

            line = readNext();
            System.out.println(line);
            String[] answers = line.split(";");
            //check da exception
            if(answers[0].equals("e")) throw new AlreadyRegistedException(answers[1]);
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void authentication(String username, String password) throws InvalidLoginException {
        String line = "login;" + username + ";" + password;
        try {
            this.conn.send(line.getBytes());

            line = readNext();
            System.out.println(line);
            String[] answers = line.split(";");
            if(answers[0].equals("e")) throw new InvalidLoginException(answers[1]);
        } catch(IOException | InterruptedException e) {
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
