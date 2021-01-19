package Client;

import exceptions.AlreadyRegistedException;
import exceptions.InvalidLocationException;
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
            }else{
                mainMenu();
            }
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
                switch (input) {
                    /* update location */
                    case "update":
                        userInput = UserInterface.showUpdateLocationMenu();
                        try{
                            demultiplexer.update_location(user, Integer.parseInt(userInput.get(0)));
                            user = userInput.get(0);
                        }catch(InvalidLocationException e){
                            System.out.println(e.getMessage());
                        }
                        break;
                    /* view map */
                    case "view":
                        userInput = UserInterface.showViewLocationMenu();
                        try{
                            demultiplexer.nr_people_location(Integer.parseInt(userInput.get(0)));
                            user = userInput.get(0);

                            if(user.equals("0")){
                                userInput = UserInterface.showViewLocationMenu();
                            } else{
                                userInput = UserInterface.showEmptyLocationMenu();
                                try{
                                    demultiplexer.notify_empty_location(user,Integer.parseInt(userInput.get(0)));
                                    user = userInput.get(0);
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
                            user = userInput.get(0);
                        }catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                        break;
                }
            }
        } while (!input.equals("logout"));

    }


    /* client menu */

   // deml . send ("consulta", nodo);


}





