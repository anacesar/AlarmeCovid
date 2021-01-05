package Data;

import java.util.HashMap;
import java.util.Map;

public class Data {
    private Map<String,User> users;
    private Localizacao[][] mapa;


    public Data (){
        this.users= new HashMap<>();
        this.mapa= new Localizacao[][];
    }

    public Data(Map<String,User> users,Localizacao[][] mapa ){
        this.users= users;
        this.mapa=mapa;
    }

    public Data(Data d){
        this.users= d.getUsers();
        this.mapa= d.getMapa();
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    public Localizacao[][] getMapa() {
        return mapa;
    }

    public void setMapa(Localizacao[][] mapa) {
        this.mapa = mapa;
    }
}
