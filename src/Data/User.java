package Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class User {
    private boolean special_user;
    private boolean sick; //ou LocalDateTime sick ??? null quando nao esta doente
    private String username;
    private String password;
    //private String address;
    private int localizacao;
    private List<String> riskContact;
    private ReentrantLock lockUser;
    private Condition hasNotifications;
    private List<String> notifications;

    public User(){
        this.special_user = false;
        this.username = "";
        this.password = "";
        this.localizacao = 0;
        this.riskContact = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    public User(String username, String password, Boolean special) {
        this.special_user = special;
        this.sick = false;
        this.username = username;
        this.password = password;
        this.localizacao = 0; /* default location for new user */
        this.riskContact = new ArrayList<>();
    }

    public User(User u) {
        this.sick = u.isSick();
        this.username = u.getUsername();
        this.password = u.getPassword();
        this.localizacao = u.getLocalizacao();
        this.riskContact = u.getRiskContact();

    }


    public boolean isSpecial_user(){ return special_user;}

    public void isSpecial_user(boolean special_user){ this.special_user = special_user;}

    public boolean isSick(){ return sick;}

    public void isSick(boolean sick){ this.sick = sick;}

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLocalizacao() {
        return this.localizacao;
    }

    public void setLocalizacao(int localizacao) {
        this.localizacao = localizacao;
    }

    public List<String> getRiskContact(){
        return this.riskContact;
    }

    public void addRiskContact(List<String> contacts){
        contacts.forEach(contact -> {
            if(!this.riskContact.contains(contact)) riskContact.add(contact);
        });
    }

    public void setRiskContact(List<String> riskContact){
        this.riskContact = riskContact;
    }

    public void lock(){
        this.lock.lock();
    }

    public void unlock(){
        this.lock.unlock();
    }

}
