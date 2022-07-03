/**
 * this class just creates signInSignUp threads and starts it.
 *
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public class DiscordUserInterface {
    public static void main(String[] args) {
        // creating and starting thread.
        SignInSignUp signInSignUp = new SignInSignUp();
        new Thread(signInSignUp).start();
    }
}
