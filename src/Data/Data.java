package Data;

import Client.ClientConnection;
import Data.myMap.Location;
import exceptions.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock ;


public class Data implements AlarmCovidInterface{
    private Map<String,User> users;
    private myMap map;
    /* logged users and connection associated */
    private HashMap<String, ClientConnection> notification;
    /* notifications for users waiting to log in */
    private Map<String, List<String>> users_logged;

    private Lock users_lock = new ReentrantLock();
    private Lock map_lock = new ReentrantLock();
    private Lock notification_lock = new ReentrantLock();

    private final String USER_PATH;
    private final String MAP_PATH;
    private final String special_password = "12345";
    public int N = 0;

    public Data(){
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        users = new HashMap<>();
        notification = new HashMap<>();
        users_logged = new HashMap<>();

        /* load file information */
        USER_PATH  = s + "/src/DataBase/user.csv";
        MAP_PATH = s + "/src/DataBase/map.csv";
        fillDAO();
    }

    /* Preenche as estruturas de dados users e musics com a informacao dos ficheiros csv */
    public void fillDAO(){
        try {
            //BufferedReader user_br = new BufferedReader(new FileReader(USER_PATH));
            BufferedReader map_br = new BufferedReader(new FileReader(MAP_PATH));
            String line;

            /* MapDAO */
            Location location;
            String[] location_line;

            if( (line = map_br.readLine()) != null )
                this.N = Integer.parseInt(line);
            /* construtor myMap with N*N locations */
            this.map = new myMap(this.N);

            /* construtor locations */
            while( (line = map_br.readLine()) != null ){
                location_line = line.split(";");

                for(String user : location_line[1].split(",")){
                    System.out.println(user);

                }
                System.out.println("nr_ cur " + nr_cur + "nr_hist " + nr_history);
                List<String> cur_users = new ArrayList<>(), his_users = new ArrayList<>();
                for(int i = 1; i < nr_cur; i++)
                    cur_users.add(location_line[i]);
                for(int i = nr_history; i < nr_cur; i++)
                    cur_users.add(location_line[i]);


            }

        }catch(/*FileNotFoundException e*/ Exception e) {
           // e.printStackTrace();
        }
    }

    @Override
    public void registration(String username, String password, String special_pass) throws AlreadyRegistedException, SpecialPasswordInvalidException {
        Boolean special;
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
            }
        }finally {
            users_lock.unlock();
        }
    }

    @Override
    // falta ver se tem notificacoes, ver se esta doente
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
            User user = users.get(username);
            user.lock();
            users_lock.unlock();
            String message = ": Risk Contact Detected! Please pay attention for the next days!";
            user.isSick(LocalDate.now()); //set sick to this moment
            user.getRiskContact().forEach(risk -> {
                if(notification.containsKey(risk)) { //user is logged
                    try {
                        /* notificator*/
                        sendNotification(risk, message);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }else{ //save notification for later log in
                    if(users_logged.containsKey(risk)){
                       users_logged.put(risk, new ArrayList<>());
                    }
                }
            });
            //notificar todos os risk contact

        }finally {
            users_lock.unlock();
        }
    }

    @Override
    /* when a user wants to be notified how many people are in a location */
    public int nr_people_location(int node) throws InvalidLocationException {
        map_lock.lock();
        try{
            return 0;
        }finally {
            map_lock.unlock();
        }
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
            u.unlock();

            map_lock.lock();
            /* lock locations needed changes in min order to avoid deadlock*/
            Location old = map.getLocation(old_location);
            Location new_loc = map.getLocation(new_location);

            if(old_location < new_location){ // necessario????
                old.lock();
                new_loc.lock();
            }else{
                new_loc.lock();
                old.lock();
            }
            map_lock.unlock();

            /* leaving old place */
            old.exit(username); //check if its empty after leaving place
            old.unlock();

            // add list of current users in this place to username risk contacts
            List<String> contacts = new_loc.getCurrentUsers();
            u.lock();
            u.addRiskContact(contacts);
            u.unlock();

            /* entering new place */
            if(new_loc.entry(username) == 0){
                //Notificator not = new Notificator()
            }
            new_loc.unlock();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void download_map() {

    }


    /* Save users connection in notification map*/
    public void addToNotification(String username , ClientConnection cc) {
        notification_lock.lock();
        this.notification.put(username, cc);
        //if(users_logged.containsKey(username)) users_logged.get(username).forEach(notification -> cc.send(notification));
        notification_lock.unlock();
    }

    /* Save users connection in notification map*/
    public void removeNotification(String username) {
        notification_lock.lock();
        this.notification.remove(username);
        //if(users_logged.containsKey(username)) users_logged.get(username).forEach(notification -> cc.send(notification));
        notification_lock.unlock();
    }

    /* Save users connection in notification map*/
    public void sendNotification(String username , String message) throws IOException {
        notification_lock.lock();
        this.notification.get(username).send(message);
        notification_lock.unlock();
    }

    /* Warns clients about shutdown */
    public void warnClientsAboutShutdown() {
        notification_lock.lock();
        this.notification.values().forEach(cc -> { try { cc.send("serverDown");} catch(IOException e) { e.printStackTrace(); }
        });
        this.notification_lock.unlock();
    }


}
