import java.io.ObjectInputStream;
import java.net.Socket;
/**
 * this class creates thread for receiving message in chats.
 * it extends Thread class.
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public class UserMessageReceiverThread extends Thread{
    // fields
    Medium medium;
    ObjectInputStream objectInputStream;
    // constructor
    public UserMessageReceiverThread(Medium medium){
        this.medium = medium;
        objectInputStream = medium.getObjectInputStream();
    }

    /**
     * this method must be implemented because this class extends Thread class
     * this method receives and prints message until interruption.
     */
    @Override
    public void run() {
        boolean userIsInChatPage = true;
        while (userIsInChatPage){
            try{
                Message message = (Message) objectInputStream.readObject();
                System.out.println("USER RECEIVED A MESSAGE!!!");
                message.printMessage();
            }catch (Exception e){
                userIsInChatPage = false;
            }
        }
    }
}
