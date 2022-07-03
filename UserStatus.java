/**
 * this class has 4 enums for showing situation of user.
 * user set this.
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public enum UserStatus {
    ONLINE("ONLINE"),
    IDLE("IDLE"),
    INVISIBLE("INVISIBLE"),
    DONOTDISTURB("DONOTDISTURB");
    // fields
    private String stringFormat;
    // constructor
    UserStatus(String stringFormat) {
        this.stringFormat = stringFormat;
    }

    /**
     * it returns situation of user
     * @return String : situation of user
     */
    @Override
    public String toString() {
        return stringFormat;
    }
}
