package Data;

import javax.management.Notification;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.List;

import java.net.Socket;

import java.util.Map;
import java.util.Queue;

public class Data {
    private Map<String,User> users;
    private myMap map;

    /* notifications for users waiting to log in */
    private Map<String, Queue<String>> users_logged;

    public Data(){
        /* load file information */
        users_logged = new HashMap<>();
    }
}
