import java.time.LocalDateTime;
/**
 * this class creates text message.
 * this class extends message class.
 * it has 1 field that includes text of message.
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public class TextMessage extends Message{
    // includes
    private String text;
    // constructor
    public TextMessage(String id, int likeNum, int dislikeNum,int laughEmojiNum, boolean isPinned, LocalDateTime date, String text, String senderUsername, String receiverUsername) {
        super(id, likeNum, dislikeNum,laughEmojiNum, isPinned, date, senderUsername, receiverUsername);
        this.text = text;
    }
    // constructor
    public TextMessage(String sender, String receiver, String messageText) {
        super("0", 0, 0,0, false, LocalDateTime.now(), sender, receiver);
        this.text = messageText;
    }

    /**
     * it prints some information of message object for instance : text, likes,dislikes , time and etc.
     */
    @Override
    public void printMessage(){
        System.out.println("["+ super.getSenderUsername()+"] : " +text);
        System.out.println("["+super.getDate().getHour() + ":" + super.getDate().getMinute()+"  likes : "
                +super.getLikeNum()+" dislikes : "+ super.getDislikeNum()+ "Laughs : "+ super.getLaughEmojiNum()+ " ]");
    }

    /**
     * it returns text of message.
     * @return
     */
    public String getText() {
        return text;
    }
}
