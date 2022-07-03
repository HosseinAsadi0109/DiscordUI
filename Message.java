import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * this class creates message objects.
 * it includes 9 fields.
 * it creates when user wants to send a message.
 * it is abstract class and implements serializable interface.
 * it has 2 subclasses.
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public abstract class Message implements Serializable {
    // fields
    private String id;
    private String senderUsername;
    private String receiverUsername;
    private int likeNum;
    private int dislikeNum;
    private int laughEmojiNum;
    private boolean isPinned;
    private LocalDateTime date;
    private boolean isRead = false;
    // getters

    /**
     * it returns username of sender user.
     * @return
     */
    public String getSenderUsername() {
        return senderUsername;
    }

    /**
     * it returns count of likes
     * @return
     */
    public int getLikeNum(){
        return likeNum;
    }
    /**
     * it returns count of dislikes
     * @return
     */
    public int getDislikeNum() {
        return dislikeNum;
    }
    /**
     * it returns time of sending message.
     * @return
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * it returns username of receiver user
     * @return
     */
    public String getReceiverUsername() {
        return receiverUsername;
    }

    /**
     * it returns count of laugh emoji
     * @return
     */
    public int getLaughEmojiNum() {
        return laughEmojiNum;
    }
    // constructor
    public Message(String id, int likeNum, int dislikeNum, int laughEmojiNum, boolean isPinned, LocalDateTime date,
                   String senderUsername, String receiverUsername) {
        this.id = id;
        this.likeNum = likeNum;
        this.dislikeNum = dislikeNum;
        this.isPinned = isPinned;
        this.laughEmojiNum = laughEmojiNum;
        this.date = date;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
    }

    /**
     * when user watches a message it is switch to a seen message.
     */
    private void switchRead(){
        isRead = !isRead;
    }

    /**
     * it is abstract method and each class that extends this class must be implemented this method.
     * it has no parameter and return type
     */
    public abstract void printMessage();

}
