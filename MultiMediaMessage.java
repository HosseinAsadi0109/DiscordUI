import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
/**
 * this class creates multi media message.
 * this class extends message class.
 * it has 2 fields that includes file and caption
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public class MultiMediaMessage extends Message{
    // fields
    private File file;
    private String caption;
    // constructor
    public MultiMediaMessage(String id, int likeNum, int dislikeNum,int laughEmojiNum, boolean isPinned, LocalDateTime date, String caption, File file, String senderUsername, String receiverUsername) {
        super(id, likeNum, dislikeNum,laughEmojiNum, isPinned, date, senderUsername, receiverUsername);
        this.file = file;
        this.caption = caption;
    }

    /**
     * it prints some information of message object for instance : text, likes,dislikes , time and etc.
     */
    @Override
    public void printMessage(){
        System.out.println("["+ super.getSenderUsername()+"] : " + caption);
        System.out.println("["+super.getDate().getHour() + ":" + super.getDate().getMinute()+"  likes : "
                +super.getLikeNum()+" dislikes : "+ super.getDislikeNum()+ "Laughs : "+ super.getLaughEmojiNum()+ " ]");
    }
}
