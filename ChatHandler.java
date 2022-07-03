import com.sun.jdi.ThreadReference;

import java.io.IOException;
import java.net.Socket;
/**
 * this class includes 4 fields and handles private chats.
 * this thread connects to server in port number 6300 and send and receive message.
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public class ChatHandler implements Runnable {
    // fields
    Socket socket;
    Medium medium;
    String username;
    String friendUsername;
    // constructor
    public ChatHandler(String username, String friendUsername) {
        this.username = username;
        this.friendUsername = friendUsername;
        try {
            this.socket = new Socket("localhost", 6300);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        medium = new Medium(socket);
    }

    /**
     * this method must be implements because this class implemented Runnable interface.
     * it starts two threads that sends and receives messages until they interrupted.
     * it has not return type and parameter.
     */
    @Override
    public void run() {
        Thread UserMessageSenderThread = new UserMessageSenderThread(medium, username, friendUsername);
        UserMessageSenderThread.start();
        new UserMessageReceiverThread(medium).start();
        try {
            UserMessageSenderThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
