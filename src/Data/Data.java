package Data;

import Client.Demultiplexer.Notification;

import java.util.HashMap;
import java.util.Map;

public class Data {
    private Map<String,User> users;
    private myMap map;

    /* notifications for users waiting to log in */
    private Map<String, Notification> users_logged;

    public Data(){
        /* load file information */
        users_logged = new HashMap<>();
    }
}
