package Client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import Client.ClientConnection.Message;
import com.jakewharton.fliptables.FlipTableConverters;

public class Notifier implements Runnable {
    private Socket socket;
    /* client connection to receive message */
    private ClientConnection conn;
    private String username;


    public Notifier(String username) throws IOException {
        this.socket = new Socket(InetAddress.getLocalHost(), 12346);
        this.conn = new ClientConnection(socket);
        this.username = username;
    }

    @Override
    public void run() {
        Message message;
        boolean exit = false;
        try {
            this.conn.send("not;" + this.username);
            while(!exit) {
                message = conn.receiveMessage();
                System.out.println(message.tag);
                switch(message.tag) {
                    //todo pretty notifications
                    case 0: //positive contact
                        System.out.println("Risk Contact Detected : " + new String(message.data) + " ! Please pay attention for the next days!");
                        break;
                    case 1: //empty place
                        System.out.println("Location " + new String(message.data) + " is now empty! ");
                        break;
                    case 2: //download
                        downloadMap(message.data);
                        System.out.println("Map downloaded with success!");
                        break;
                    default:
                        exit = true;
                        break;
                }

            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("shutting down notifier connection ");
                conn.send("exit");
                conn.close();
                System.out.println("notifier connection down");
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Cria um caminho para a base de dados conforme o sistema operativo
     * @param path : caminho base de dados
     * @return : caminho absoluto da base de dados
     */
    public static String makePath(String path){
        String[] os = System.getProperty("os.name").split(" ");
        String fullPath = null;
        switch (os[0]){
            case "Windows" : fullPath = path + "\\src\\DataBase\\";
                break;
            case "Mac"     : fullPath = path + "/src/DataBase/";
                break;
        }
        return fullPath;
    }


    public void downloadMap(byte[] map_info) {
        File file = new File(makePath(Paths.get("").toAbsolutePath().toString()) + username + ".txt");
        try (FileWriter fileWriter = new FileWriter(file, false)){
            String[] headers = {"Location", "Address", "Nr users", "Nr sick users"};

            String[] info = new String(map_info).split(";");
            int N = Integer.parseInt(info[0]);
            Object[][] data = new Object[N * N][4];
            data[0][0] = "0";
            data[0][1] = "Home";
            data[0][2] = "---";
            data[0][3] = "---";


            for(int i = 1; i < N * N; i++) {
                String[] node_info = info[i].split(",");
                data[i][0] = Integer.parseInt(node_info[0]);
                data[i][1] = node_info[1];
                data[i][2] = Integer.parseInt(node_info[2]);
                data[i][3] = Integer.parseInt(node_info[3]);
            }

            String table = FlipTableConverters.fromObjects(headers, data);
            fileWriter.write(table);
        } catch(IOException ignored) { }
    }
}
