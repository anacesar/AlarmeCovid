package Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private static Scanner scanner = new Scanner(System.in);

    public static class Menu {
        private List<String> options;
        private String name;

        public Menu(List<String> options, String name){
            setOptions(options);
            this.name = name;
        }

        public void setOptions(List<String> ops){
            this.options = new ArrayList<>();
            ops.forEach(o -> {this.options.add(o);});
        }


        public void show(){
            System.out.println("--------------------------------------------------------------------");
            System.out.println("|                          " + this.name + "                          |");
            System.out.println("--------------------------------------------------------------------");
            for(String op: this.options){
                System.out.println(op.toString());
            }
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Select one available option");
        }
    }

    public static void waitEnter() {
        System.out.println("Press enter to continue");
        scanner.nextLine();
    }

    public static String showWelcomeMenu() {
        List<String> options = new ArrayList<>();
        options.add("1 - Login");
        options.add("2 - Register");
        options.add("3 - Exit");
        Menu welcomeMenu = new Menu(options, "Welcome Menu  ");
        welcomeMenu.show();
        String selectedOption = scanner.nextLine();
        String res = null;
        switch (selectedOption) {
            case "1":
                res = "login";
                break;
            case "2":
                res = "register";
                break;
            case "3":
                res = "exit";
                break;
            default:
                System.out.println("Please select one of the available options");
                waitEnter();
                res = showWelcomeMenu();
        }
        return res;
    }

    public static List<String> showRegisterMenu() {
        List<String> answers = new ArrayList<>();
        System.out.println("Insert an username");
        answers.add(scanner.nextLine());
        System.out.println("Insert a password ");
        answers.add(scanner.nextLine());
        System.out.println("Special Permissions Password (Press 0 to ignore)");
        String password = scanner.nextLine();
        if(password.equals("0")) answers.add("null");
        answers.add(password);

        return answers;
    }

    public static List<String> showLoginMenu() {
        List<String> answers = new ArrayList<>();
        System.out.println("Insert an username ");
        answers.add(scanner.nextLine());
        System.out.println("Insert a password ");
        answers.add(scanner.nextLine());

        return answers;
    }
}
