package Client;

import java.util.*;

public class UserInterface {
    private static Scanner scanner = new Scanner(System.in);

    public static class Menu {
        private List<String> options;
        private String name;


        public Menu(List<String> options, String name){
            this.options = new ArrayList<>(options);
            this.name = name;
        }

        public void show(){
            System.out.println("--------------------------------------------------------------------");
            System.out.println("|                          " + this.name + "                         |");
            System.out.println("--------------------------------------------------------------------");
            for(String op: this.options){
                System.out.println(op);
            }
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Select one available option");
        }
    }


    public static void flush(){
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
        String res;
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
        String user = null, pass = null;

        while(user == null || user.isBlank()){
            System.out.println("Insert an username ");
            user = scanner.nextLine();
        }
        answers.add(user);

        while (pass == null || pass.isBlank()) {
            System.out.println("Insert a password ");
            pass = scanner.nextLine();
        }
        answers.add(pass);

        return answers;
    }

    public static String showMainMenu(boolean special) {
        List<String> options = new ArrayList<>();
        options.add("1 - Update Location");
        options.add("2 - Number of people in a location");
        options.add("3 - Report Positive Case");
        options.add("4 - View Map");
        if(special) options.add("5 - Download map");
        options.add("0 - Logout");
        Menu showMainMenu = new Menu(options, "Main Menu  ");
        showMainMenu.show();
        String selectedOption = scanner.nextLine();
        String res;
        switch (selectedOption) {
            case "1":
                res = "update";
                break;
            case "2":
                res = "view";
                break;
            case "3":
                res = "positive";
                break;
            case "4":
                res = "map";
                break;
            case "5":
                res = "download";
                break;
            case "0":
                res = "logout";
                break;
            default:
                System.out.println("Please select one of the available options: ");
                res = showMainMenu(special);
        }
        return res;
    }

    public static int showUpdateLocationMenu(){
        System.out.println("Change Location: ");
        try{
            return Integer.parseInt(scanner.nextLine());
        }catch(NumberFormatException e){
            System.out.println("Select a valid location!");
            return showUpdateLocationMenu();
        }

    }

    public static int showViewLocationMenu(){
        System.out.println("Choose Next Location: ");
        try{
            return Integer.parseInt(scanner.nextLine());
        }catch(NumberFormatException e){
            System.out.println("Select a valid location!");
            return showViewLocationMenu();
        }
    }

    public static int showEmptyLocationMenu(){
        System.out.println("Would you like to be notified when is empty?");
        System.out.println("1- Yes ");
        System.out.println("0- No ");
        int op = scanner.nextInt();
        flush();
        while(op!=1 && op != 0){
            System.out.println("Please select one available option! ");
            op = scanner.nextInt();
            flush();
        }
        return op;
    }


    public static int showReportPositiveMenu(){
        System.out.println("Would you like to report positive? ");
        System.out.println("1- Yes ");
        System.out.println("0- No ");
        int op = Integer.parseInt(scanner.nextLine());
        while(op!=1 && op != 0){
            System.out.println("Please select one available option! ");
            op = Integer.parseInt(scanner.nextLine());
        }
        return op;
    }

}
