package Data;

import Client.Demultiplexer.Notification;
import exceptions.AlreadyRegistedException;
import exceptions.InvalidLocationException;
import exceptions.InvalidLoginException;
import exceptions.SpecialPasswordInvalidException;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock ;

import Data.myMap.Location;

public class Data implements AlarmCovidInterface{
    private Map<String,User> users;
    private myMap map;
    //private HashMap<String, DataOutputStream> notification;
    private final String USER_PATH;
    private final String MAP_PATH;
    private final String special_password = "12345";

    /* notifications for users waiting to log in */
    private Map<String, Notification> users_logged;

    private Lock users_lock = new ReentrantLock();
    private Lock map_lock = new ReentrantLock();
    private Lock notification_lock = new ReentrantLock();

    public Data(){
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        USER_PATH  = s + "/src/DataBase/user.csv";
        MAP_PATH = s + "/src/DataBase/map.csv";
        users = new HashMap<>();
        map = new myMap();
        /* load file information */
        users_logged = new HashMap<>();
    }

    @Override
    public void registration(String username, String password, String special_pass) throws AlreadyRegistedException, SpecialPasswordInvalidException {
        Boolean special;
        System.out.println(special_pass);
        try{
            users_lock.lock();
            if(users.containsKey(username)) throw new AlreadyRegistedException();
            else{
                if(special_pass.equals("null")) special = false;
                else { /*user with special permissions */
                    if(special_pass.equals(special_password)) special = true;
                    else throw new SpecialPasswordInvalidException();
                }
                users.put(username, new User(username, password, special));
                System.out.println("done");

            }
        }finally {
            users_lock.unlock();
        }
    }

    @Override
    public void authentication(String username, String password) throws InvalidLoginException {
        try{
            users_lock.lock();
            if( !users.containsKey(username) || !users.get(username).getPassword().equals(password) )
                throw new InvalidLoginException("Invalid Login");
        }finally {
            users_lock.unlock();
        }
    }

    @Override
    /* when a user notify he has tested positive */
    public void notify_positive(String username) {
        try {
            users_lock.lock();
            //notificar todos os risk contact


            //setSick == true
        }finally {
            users_lock.unlock();
        }
    }

    @Override
    /* when a user wants to be notified how many people are in a location */
    public int nr_people_location(int node) throws InvalidLocationException {
        return 0;
    }

    @Override
    /* when a user wants to be notified when a location gets empty */
    public void notify_empty_location(String username, int node) throws InvalidLocationException {

    }

    @Override
    /* when a user updates location */
    public void update_location(String username, int new_location) {
        try {
            users_lock.lock();
            User u = users.get(username);
            u.lock();
            users_lock.unlock();
            int old_location = u.getLocalizacao();
            u.setLocalizacao(new_location);
            map_lock.lock();
            Location old = map.getLocation(old_location);
            old.exit(username);
            Location new_loc = map.getLocation(new_location);
            // add to riskContact
            List<String> contacts = new_loc.getCurrentUsers();
            u.addRiskContact(contacts);
            u.unlock();
            new_loc.entry(username);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void download_map() {

    }
}
