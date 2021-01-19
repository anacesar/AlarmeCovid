package Client;

import exceptions.*;
import java.util.List;

public class Menu {
    private Demultiplexer demultiplexer;
    private String user;

    public Menu(Demultiplexer demultiplexer) {
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
                }
            }else mainMenu();
        } while (!input.equals("exit"));
    }

    public void mainMenu(){
        //fazer get do mapa e devolver interacao
        /*atualizar localizacao , consultar mapa , reportar positivo*/

        String input = null;
        List<String> userInput;
        do {
            if(user != null){
                input = UserInterface.showMainMenu();
                System.out.println(input);
                switch (input) {
                    /* update location */
                    case "update":
                        userInput = UserInterface.showUpdateLocationMenu();
                        try{
                            demultiplexer.update_location(user, Integer.parseInt(userInput.get(0)));
                        }catch(InvalidLocationException e){
                            System.out.println(e.getMessage());
                        }
                        break;
                    /* view map */
                    case "view":
                        userInput = UserInterface.showViewLocationMenu();
                        try{
                            demultiplexer.nr_people_location(Integer.parseInt(userInput.get(0)));

                            if(user.equals("0")){
                                userInput = UserInterface.showViewLocationMenu();
                            } else{
                                userInput = UserInterface.showEmptyLocationMenu();
                                try{
                                    demultiplexer.notify_empty_location(user,Integer.parseInt(userInput.get(0)));
                                }catch (InvalidLocationException e){
                                    System.out.println(e.getMessage());
                                }
                            }

                        }catch(InvalidLocationException e){
                            System.out.println(e.getMessage());
                        }
                        break;
                    /* report positive */
                    case "positive":
                        userInput = UserInterface.showReportPositiveMenu();
                        try{
                            demultiplexer.notify_positive(user);
                        }catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "logout":
                        user = null;
                        System.out.println("logout pressed");
                }
            }else start();
        } while (!input.equals("logout"));

    }


    /* client menu */

   // deml . send ("consulta", nodo);


}





