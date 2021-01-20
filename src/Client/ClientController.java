package Client;

import exceptions.*;

import java.io.IOException;
import java.util.List;

public class ClientController {
    private Demultiplexer demultiplexer;
    private String user;

    public ClientController(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
        this.user = null;
    }

    public void start() {
        String input = null;
        List<String> userInput;
        do {
            if(user == null){
                input = UserInterface.showWelcomeMenu();
                switch (input) {
                    case "login":
                        userInput = UserInterface.showLoginMenu();
                        try{
                            //todo verification of special user
                            demultiplexer.authentication(userInput.get(0), userInput.get(1));
                            user = userInput.get(0);
                        }catch(InvalidLoginException e){
                            System.out.println(e.getMessage());
                        }//catch(QuarantineException q){ //trying to login but cant access app
                        //    System.out.println("You are still in quarantine! Please stay at home.."); input = "exit";
                        //}
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
                    case "exit" :
                        try {
                            demultiplexer.send("exit");
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                }
            }else {
                System.out.println("You are now logged! ");
                mainMenu();
            }
            //UserInterface.waitEnter();
        } while (!input.equals("exit"));
    }

    public void mainMenu(){
        //fazer get do mapa e devolver interacao
        /*atualizar localizacao , consultar mapa , reportar positivo*/

        String input = null;
        List<String> userInput;
        int node;
        do {
            if(user != null){
                input = UserInterface.showMainMenu();
                switch (input) {
                    /* update location */
                    case "update":
                        node = UserInterface.showUpdateLocationMenu();
                        try{
                            demultiplexer.update_location(user, node);
                        }catch(InvalidLocationException e){
                            System.out.println(e.getMessage());
                        }
                        break;
                    /* view map */
                    case "view":
                        node = UserInterface.showViewLocationMenu();
                        try{
                            int nr = demultiplexer.nr_people_location(node);
                            System.out.println("There are " + nr + " people in this location!");

                            if(UserInterface.showEmptyLocationMenu() == 1){
                                //demultiplexer.notify_empty_location(user, node);
                            }
                        }catch(InvalidLocationException e){
                            System.out.println(e.getMessage());
                        }
                        break;
                    /* report positive */
                    case "positive":
                        if(UserInterface.showReportPositiveMenu() == 1){
                            try{
                                demultiplexer.notify_positive(user);
                            }catch(Exception e){
                                System.out.println(e.getMessage());
                            }
                        }
                        break;
                    case "logout":
                        try {
                            demultiplexer.send("logout");
                            user = null;
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                }
            }else start();
            //UserInterface.waitEnter();
        } while (!input.equals("logout"));

    }


    /* client menu */

   // deml . send ("consulta", nodo);


}





