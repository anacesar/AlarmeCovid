package Data;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private int localizacao;
    private List<String> riskContact;

    public User(){
        this.username = "";
        this.password = "";
        this.localizacao = 0;
        this.riskContact = new ArrayList<>();
    }

    public User(String username, String password, int localizacao, List<String> riskContact) {
        this.username = username;
        this.password = password;
        this.localizacao = localizacao;
        this.riskContact = riskContact;
    }

    public User(User u) {
        this.username = u.getUsername();
        this.password = u.getPassword();
        this.localizacao = u.getLocalizacao();
        this.riskContact = u.getRiskContact();

    }

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

    public void setRiskContact(List<String> riskContact){
        this.riskContact = riskContact;
    }

}
