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

    public static class mapViewer {
        private int n; //nr of nodes in map
        private Map<Integer, String> map = new HashMap<>();
        public static boolean loaded = false;

        public mapViewer(int N){

        }

        public void location_info(int node, String name){
            map.put(node, name);
        }

    }

    public static void waitEnter() {
        System.out.println("Press enter to continue");
        scanner.nextLine();
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
                showWelcomeMenu();
                //waitEnter();
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

    public static String showMainMenu(boolean special) {
        List<String> options = new ArrayList<>();
        options.add("1 - Update Location");
        options.add("2 - Number of people in a location");
        options.add("3 - Report Positive Case");
        if(special) options.add("4 - Download map");
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
            case "4" :
                res = "download";
                break;
            case "0":
                res = "logout";
                break;
            default:
                System.out.println("Please select one of the available options: ");
                //waitEnter();
                res = showMainMenu(special);
        }
        return res;
    }


    public static int showUpdateLocationMenu(){
        System.out.println("Change Location ");
        int op = scanner.nextInt();
        flush();
        return op;

    }


    //todo check if location is right
    public static int showViewLocationMenu(){
        System.out.println("Choose Next Location: ");
        int loc = scanner.nextInt();
        flush();
        return loc;
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


    /*
    *
    * Menu "Tens a mania que és special"
    *
    * */
    public static List<String> showSpecialMenu(){
        List<String> answers = new ArrayList<>();
        System.out.println("Do you have special permissions? ");
        System.out.println("1- Yes ");
        System.out.println("2- No ");
        answers.add(scanner.nextLine());

        return answers;
    }


}
