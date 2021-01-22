package Data;

import Client.ClientConnection;
import Client.ClientConnection.Message;
import Data.myMap.Location;
import com.jakewharton.fliptables.FlipTableConverters;
import exceptions.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Data implements AlarmCovidInterface{
    private Map<String,User> users;
    private myMap map;
    /* logged users and connection associated */
    private HashMap<String, ClientConnection> notification; //cc associada ao socket de notificacoes
    /* notifications for users waiting to log in */
    private Map<String, List<Message>> users_not_logged; //todo passar para csv

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
        users_not_logged = new HashMap<>();
        users_not_logged.put("user3", new ArrayList<>());
        users_not_logged.get("user3").add(new Message(0, "user4".getBytes()));
        /* load files information */
        USER_PATH  = s + "/src/DataBase/users.csv";
        MAP_PATH = s + "/src/DataBase/map.csv";
        fillDAO();
    }

    /* Preenche as estruturas de dados users e musics com a informacao dos ficheiros csv */
    public void fillDAO(){
        try {
            BufferedReader user_br = new BufferedReader(new FileReader(USER_PATH));
            BufferedReader map_br = new BufferedReader(new FileReader(MAP_PATH));
            String line;

            /* UserDAO*/
            User user;
            String[] userLine;
            List<String> riskContact;
            while( (line = user_br.readLine()) != null ){
                userLine = line.split(";");

                riskContact = new ArrayList<>(Arrays.asList(userLine[4].split(",")));

                LocalDate time = userLine[3].equals("0") ? null : LocalDate.parse(userLine[3]);

                user = new User(userLine[0], userLine[1], Boolean.parseBoolean(userLine[2]), time, Integer.parseInt(userLine[4]), riskContact);
                users.put(user.getUsername(), user);
            }

            /* MapDAO */
            String[] location_line;

            if( (line = map_br.readLine()) != null )
                this.N = Integer.parseInt(line);
            /* construtor myMap with N*N locations */
            this.map = new myMap(this.N);

            List<String> cur_users, hist_users;

            /* construtor locations */
            while((line = map_br.readLine()) != null) {
                location_line = line.split(";");

                cur_users = new ArrayList<>(Arrays.asList(location_line[2].split(",")));
                hist_users = new ArrayList<>(Arrays.asList(location_line[3].split(",")));

                this.map.putLocation(Integer.parseInt(location_line[0]), new Location(location_line[1], cur_users, hist_users));
            }

        } catch(IOException e) {
            System.out.println("An error ocurred during load of csv files.. \n" + e.getMessage());
        }
    }

    @Override
    public void registration(String username, String password, String special_pass) throws AlreadyRegistedException, SpecialPasswordInvalidException {
        boolean special;
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
    public boolean authentication(String username, String password) throws InvalidLoginException, QuarantineException{
        try{
            users_lock.lock();
            User user = users.get(username);
            if(user == null || !user.getPassword().equals(password)) throw new InvalidLoginException("Invalid Login");
            if(user.isSick() != null){
                System.out.println("is sick");
                if (Period.between(user.isSick(), LocalDate.now()).getDays() < 14) throw new QuarantineException();
                else user.isSick(null);
            }
            return user.isSpecial_user();
        }finally {
            users_lock.unlock();
        }
    }

    @Override
    /* when a user notify he has tested positive */
    public void notify_positive(String username) {
            users_lock.lock();
            User user = users.get(username);
            user.lock();
            users_lock.unlock();
            user.isSick(LocalDate.now()); //set sick to this moment
            Message m = new Message(0, username.getBytes());
            user.getRiskContact().forEach(risk -> {
                if(notification.containsKey(risk)) { //user is logged
                    try {/* notificator*/
                        sendNotification(risk, m);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }else{ //save notification for later log in
                    if(! users_not_logged.containsKey(risk)){
                        users_not_logged.put(risk, new ArrayList<>());
                    }
                    users_not_logged.get(risk).add(m);
                }
            });
            user.unlock();
    }

    @Override
    /* when a user wants to be notified how many people are in a location */
    public int nr_people_location(int node) throws InvalidLocationException {
        map_lock.lock();
        try{
            if(node >= N*N) throw new InvalidLocationException();
            return map.nr_people(node);
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
    public void download_map(String username) {

        File file = new File("/Users/anacesar/Desktop/" + username + ".txt");

        map_lock.lock();

        // Write the content in file
        try(FileWriter fileWriter = new FileWriter(file)) {
            String fileContent=null;
            String[] headers = { "Localização", "NPessoas"};
            Object[][] data = new Object[N*N][2];
            data[0][0]= "Casa";
            data[0][1]= "0";
            for(int i=1; i<N*N;i++){
                data[i][0] = map.getLocation(i).getAdress();
                data[i][1] = String.valueOf(map.nr_people(i));
            }
            String table = FlipTableConverters.fromObjects(headers,data);
            fileContent=table;

            System.out.println(fileContent);

            fileWriter.write(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            map_lock.unlock();
        }
    }


    /* Save users connection in notification map*/
    public void addToNotification(String username , ClientConnection cc) {
        notification_lock.lock();
        this.notification.put(username, cc);
        System.out.println(username + "added to notification ");
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

    /* Send notification in Message format to username */
    public void sendNotification(String username , Message message) throws IOException {
        notification_lock.lock();
        System.out.println("sending message to " + username + " : " + message.toString());
        this.notification.get(username).send(message);
        notification_lock.unlock();
    }

    /* Save users connection in notification map*/
    public void sendNotification(String username){
        if(users_not_logged.containsKey(username)){
            users_not_logged.get(username).forEach(not -> {
                try {
                    sendNotification(username, not);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });
            users_not_logged.remove(username);
        }
    }

    /* Warns clients about shutdown */
    public void warnClientsAboutShutdown() {
        notification_lock.lock();
        this.notification.values().forEach(cc -> { try { cc.send("serverDown");} catch(IOException e) { e.printStackTrace(); }
        });
        this.notification_lock.unlock();
    }


}
