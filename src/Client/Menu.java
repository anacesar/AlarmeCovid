package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Menu{
    private ClientConnection clientConnection;
    private String user;
    private BufferedReader bufferedReader;

    public Menu(ClientConnection cc) {
        this.clientConnection = cc;
        this.user = null;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void firstMenu() throws IOException {
        int option = -1;
        while(option != 3) {
            System.out.println("|----------------|------------------------------|");
            System.out.println("|  Menu Inicial  |                              |");
            System.out.println("|----------------|                              |");
            System.out.println("|                                               |");
            System.out.println("|     [ 1 ] - Autenticação                      |");
            System.out.println("|     [ 2 ] - Registo                           |");
            System.out.println("|     [ 3 ] - Fechar                            |");
            System.out.println("|                                               |");
            System.out.println("|-----------------------------------------------|");
            System.out.println("$ Opção : ");
            try {
                option = Integer.valueOf(this.bufferedReader.readLine());
            } catch(NumberFormatException n) {
                //Utility.clearScreen();
                System.out.println("Formato inválido!");
                firstMenu();
                break;
            }
            if(option == 1 || option == 2) {
                System.out.println("Nome de utilizador:");
                String username = this.bufferedReader.readLine();
                System.out.println("Password:");
                String password = this.bufferedReader.readLine();
                if(option == 1) {
                    try {
                        this.stub.authentication(username, password);
                        this.username = username;
                        Utility.clearScreen();
                        System.out.println("Autenticação completa!");
                    } catch(InvalidLoginException e) {
                        Utility.clearScreen();
                        System.out.println("Dados Inválidos!");
                    }
                } else {
                    try {
                        this.stub.registration(username, password);
                        this.username = username;
                        Utility.clearScreen();
                        System.out.println("Utilizador criado com sucesso!");
                    } catch(AlreadyRegistedException e) {
                        Utility.clearScreen();
                        System.out.println("Username já existente!");
                    }
                }
                mainMenu();
                option = 3;
            } else if(option != 3) {
                Utility.clearScreen();
            }
        }
        Utility.clearScreen();
        this.stub.logout();
    }
}


