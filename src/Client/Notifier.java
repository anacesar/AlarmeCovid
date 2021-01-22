package Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import Client.ClientConnection.Message;

public class Notifier implements Runnable{
    private Socket socket;
    /* client connection to receive message */
    private ClientConnection conn;
    private String username;


    public Notifier(String username) throws IOException {
        this.socket = new Socket(InetAddress.getLocalHost(),12346);
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
                switch(message.tag){
                    //todo pretty notifications
                    case 0: //positive contact
                        System.out.println("Risk Contact Detected : " + new String(message.data) + " ! Please pay attention for the next days!");
                        break;
                    case 1: //empty place

                        break;
                    case 2: //download

                        break;
                    default:
                        exit = true;
                        break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
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
}
