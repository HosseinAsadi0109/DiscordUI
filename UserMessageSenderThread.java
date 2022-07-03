import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;
/**
 * this class creates thread for sending message in chats.
 * it extends Thread class.
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public class UserMessageSenderThread extends Thread{
    // fields
    Medium medium;
    String sender;
    String receiver;
    ObjectOutputStream objectOutputStream;
    // constructor
    public UserMessageSenderThread(Medium medium, String username, String friendUsername){
        this.sender = username;
        this.receiver = friendUsername;
        this.medium = medium;
        objectOutputStream = medium.getObjectOutputStream();
    }
    /**
     * this method must be implemented because this class extends Thread class
     * this method sends message until interruption.
     */
    @Override
    public void run() {
        boolean userIsInChatPage = true;
        try {
            objectOutputStream.writeObject(sender);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Please enter a message: ");
        while (userIsInChatPage){
            try {
                String messageText = new Scanner(System.in).nextLine();
                if (!messageText.startsWith("/back")){
                    objectOutputStream.writeObject(new TextMessage(sender, receiver, messageText));
                    objectOutputStream.flush();
                    System.out.println("MESSAGE SENT TO SERVER!!!");
                }else {
                    objectOutputStream.writeObject(new TextMessage(sender, null, "/back"));
                    userIsInChatPage = false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
