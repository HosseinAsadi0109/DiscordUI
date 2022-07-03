import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
/**
 * this Class includes 3 fields for connection , reading and writing messages.
 *
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public class Medium {
    // fields
    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
    // constructor
    public Medium(Socket socket) {
        this.socket = socket;
        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * it returns objectInputStream
     * @return ObjectInputStream
     */
    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    /**
     * it returns objectOutputStream
     * @return ObjectOutputStream
     */
    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }
}
