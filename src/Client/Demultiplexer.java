package Client;

import Data.AlarmCovidInterface;
import exceptions.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AlarmCovidInterface {
    private ClientConnection conn;
    private final List<String> answers;
    private Lock lock = new ReentrantLock();
    //private IOException exception = null;


    public Demultiplexer(ClientConnection conn){
        this.conn = conn;
        this.answers = new ArrayList<>();
    }


    //outra thread a espera de mapas????
    public void start() {
        new Thread( () -> {
            String message = "";
            do {
                try {
                    /* Message m = conn.receive()*/
                    /*m.tag == 0 --> notification */
                    /*m.tag == 1 --> answers */
                    /*m.tag == 2 --> download map */
                    message = new String(conn.receive());
                    //System.out.println(message);
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
                    //e.printStackTrace();
                }
            }while(! message.equals("stop") && !message.equals("serverDown"));
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

            line = new String(conn.receive());
            System.out.println(line);
            String[] answers = line.split(";");
            //check da exception
            if(answers[0].equals("e")) throw new AlreadyRegistedException(answers[1]);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authentication(String username, String password) throws InvalidLoginException, QuarantineException {
        String line = "login;" + username + ";" + password;
        try {
            this.conn.send(line.getBytes());

            line = new String(conn.receive());
            String[] answers = line.split(";");
            if(answers[0].equals("e")) throw new InvalidLoginException(answers[1]);
            if(answers[0].equals("q")) throw new QuarantineException(answers[1]);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return Boolean.parseBoolean(line);
    }

    @Override
    public void notify_positive(String username) {
        String line = "positive;" + username;
        try {
            this.conn.send(line.getBytes());

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int nr_people_location(int node) throws InvalidLocationException {
        String line = "view;" + node;
        try {
            this.conn.send(line.getBytes());

            line = new String(conn.receive());
            String[] answers = line.split(";");
            if(answers[0].equals("e")) throw new InvalidLocationException(answers[1]);

        } catch(IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(line);
    }

    @Override
    public void notify_empty_location(String username, int node) throws InvalidLocationException {
        String line = "notify;" + username + ";" + node;
        try {
            this.conn.send(line.getBytes());

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update_location(String username, int new_location) throws InvalidLocationException {
        String line = "update;" + new_location;
        try {
            this.conn.send(line.getBytes());

            line = new String(conn.receive());
            String[] answers = line.split(";");
            if(answers[0].equals("e")) throw new InvalidLocationException(answers[1]);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void download_map(String username) {
        String line = "download;" + username;
        try {
            this.conn.send(line.getBytes());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public void send(String message) throws IOException {
        this.conn.send(message);
    }



    public void close() throws IOException {
        this.conn.close();
    }

}
