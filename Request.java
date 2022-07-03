import java.io.Serializable;
/**
 * this Class includes two fields , first fields contains a field of RequestType enum
 * second field includes Object for sending different and special data for user
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public class Request implements Serializable {
    // fields
    private RequestType type;
    private Object data;
    // constructor
    public Request(RequestType type, Object data) {
        this.type = type;
        this.data = data;
    }
    // getters

    /**
     * it returns type of request
     * @return
     */
    public RequestType getType() {
        return type;
    }

    /**
     * it returns type of request.
     * @return
     */
    public Object getData() {
        return data;
    }
}