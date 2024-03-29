package Client;

import Data.AlarmCovidInterface;
import com.jakewharton.fliptables.FlipTableConverters;
import exceptions.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AlarmeCovid_Stub implements AlarmCovidInterface {
    private ClientConnection conn;
    private final List<String> answers;
    private Lock lock = new ReentrantLock();


    public AlarmeCovid_Stub(ClientConnection conn){
        this.conn = conn;
        this.answers = new ArrayList<>();
    }

    @Override
    public boolean registration(String username, String password, String special_password) throws AlreadyRegistedException, SpecialPasswordInvalidException {
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
        return Boolean.parseBoolean(line);
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
    public void view_map() {
        String line = "map";
        try {
            this.conn.send(line.getBytes());

            int N = Integer.parseInt(new String(this.conn.receive()));

            String[] headers = {"Location", "Address"};
            Object[][] data = new Object[N * N][2];

            data[0][0] = 0;
            data[0][1] = "Home";

            for(int i = 1; i<N*N; i++){
                String[] info =  new String(this.conn.receive()).split(",");
                data[i][0] = Integer.parseInt(info[0]);
                data[i][1] = info[1];
            }

            System.out.println(FlipTableConverters.fromObjects(headers, data));
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

    /* send to server message*/
    public void send(String message) throws IOException {
        this.conn.send(message);
    }

    /* close user connection */
    public void close() throws IOException {
        this.conn.close();
    }

}
