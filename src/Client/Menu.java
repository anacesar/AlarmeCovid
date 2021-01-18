package Client;

import exceptions.AlreadyRegistedException;
import exceptions.InvalidLoginException;
import exceptions.SpecialPasswordInvalidException;

import java.io.*;

public class Menu {
    private Demultiplexer demultiplexer;
    private String user;
    private BufferedReader bufferedReader;

    public Menu(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
        this.user = null;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }


    public void firstMenu() throws IOException {
        int option = -1;

        while (option != 3) {
            System.out.println("|----------------|------------------------------|");
            System.out.println("|  Menu Inicial  |                              |");
            System.out.println("|----------------|                              |");
            System.out.println("|                                               |");
            System.out.println("|     [ 1 ] - Autenticação                      |");
            System.out.println("|     [ 2 ] - Registo                           |");
            System.out.println("|-----------------------------------------------|");
            System.out.println("$ Opção : ");
            try {
                option = Integer.parseInt(this.bufferedReader.readLine());
            } catch (NumberFormatException n) {
                //Utility.clearScreen();
                System.out.println("Formato inválido!");
                firstMenu();
                break;
            }
            if (option == 1 || option == 2) {
                System.out.println("Nome de utilizador:");
                String username = this.bufferedReader.readLine();
                System.out.println("Password:");
                String password = this.bufferedReader.readLine();
                if(option == 1){
                    try{
                        demultiplexer.authentication(username, password);
                        this.user = username;
                    }catch(InvalidLoginException e){
                        System.out.println(e.getMessage());
                    }
                }else{

                    try{
                        demultiplexer.registration(username, password, "null");
                    }catch(AlreadyRegistedException | SpecialPasswordInvalidException e){
                        System.out.println(e.getMessage());
                    }
                }
            }

        }
    }


    /* client menu */

   // deml . send ("consulta", nodo);


}





