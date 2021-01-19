package Client;

import exceptions.AlreadyRegistedException;
import exceptions.InvalidLoginException;
import exceptions.SpecialPasswordInvalidException;

import java.io.*;
import java.util.List;

public class Menu {
    private Demultiplexer demultiplexer;
    private String user;
    private BufferedReader bufferedReader;

    public Menu(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
        this.user = null;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }


    public void start() throws IOException {
        String input = null;
        List<String> userInput;
        do {
            if(user == null){
                input = UserInterface.showWelcomeMenu();
                switch (input) {
                    case "login":
                        userInput = UserInterface.showLoginMenu();
                        try{
                            demultiplexer.authentication(userInput.get(0), userInput.get(1));
                            user = userInput.get(0);
                        }catch(InvalidLoginException e){
                            System.out.println(e.getMessage());
                        }//catch(QuarantineException q) trying to login but cant access app
                        break;
                    case "register":
                        userInput = UserInterface.showRegisterMenu();
                        try{
                            demultiplexer.registration(userInput.get(0), userInput.get(1), userInput.get(2));
                            user = userInput.get(0);
                        }catch(AlreadyRegistedException | SpecialPasswordInvalidException e){
                            System.out.println(e.getMessage());
                        }
                        break;
                }
            } else mainMenu();
        } while (!input.equals("exit"));
    }

    public void mainMenu(){
        //fazer get do mapa e devolver interacao
        /*atualizar localizacao , consultar mapa , reportar positivo*/
    }
    /* client menu */

   // deml . send ("consulta", nodo);


}





