import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * this class shows first menu for signing in or signing up.
 * it is thread that connect to a server.
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public class SignInSignUp implements Runnable{
    // fields
    private String serverIp = "localhost";
    Scanner scanner = new Scanner(System.in);
    ObjectOutputStream objectOutputStreamForSignup;
    ObjectInputStream objectInputStreamForSignup;
    ObjectOutputStream objectOutputStreamForSignin;
    ObjectInputStream objectInputStreamForSignin;
    Socket currentUserSocketForSignin = null;
    Socket currentUserSocketForSignup = null;

    /**
     * this method must be implemented because it implements Runnable interface.
     * it creates thread then handle menu until thread interruption
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            int choice = initializeChoiceFirstMenu();
            switch (choice){
                case 1:
                    signin();
                    break;
                case 2:
                    signup();
                    break;
                case 3:
                    Thread.currentThread().interrupt();
                    break;
            }
        }
    }

    /**
     * this method shows items then user selects and method returns choice.
     * @return
     */
    private int initializeChoiceFirstMenu(){
        int choice = 0;
        System.out.println("1) Sign in");
        System.out.println("2) Sign up");
        System.out.println("3) Exit");
        do {
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            }catch (InputMismatchException e){
                System.out.println("Please enter either 1 or 2 or 3");
            }
        }while (!(choice == 1 || choice == 2 || choice == 3));
        return choice;
    }

    /**
     * this method handles sign in option to sign in user.
     */
    private void signin(){
        if (currentUserSocketForSignin == null){
            try {
                currentUserSocketForSignin = new Socket(serverIp, 6000);
                System.out.println("Sign in socket to server successfully set");
                objectOutputStreamForSignin = new ObjectOutputStream(currentUserSocketForSignin.getOutputStream());
                System.out.println("OOS FOR SIGN IN SET!");
                objectInputStreamForSignin = new ObjectInputStream(currentUserSocketForSignin.getInputStream());
                System.out.println("OIS FOR SIGN IN SET!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String sentCredentialPacket;
        String receivedStringFromServer = "";
        String enteredUsername = null;
        while (!receivedStringFromServer.equals("Successfully logged in.")){
            System.out.println("Type /back to get back!");
            System.out.println("Please enter your username: ");
            enteredUsername = scanner.nextLine();
            if (!enteredUsername.equals("/back")){
                System.out.println("Please enter your password: ");
                String enteredPassword = scanner.nextLine();
                if (!enteredPassword.equals("/back")){
                    sentCredentialPacket = enteredUsername + "+" + enteredPassword;
                    try {
                        objectOutputStreamForSignin.writeObject(sentCredentialPacket);
                        objectOutputStreamForSignin.flush();
                        receivedStringFromServer = (String) objectInputStreamForSignin.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Credential successfully sent to server");
                    System.out.println(receivedStringFromServer);
                }else {
                    return;
                }
            }else {
                return;
            }
        }
        Thread signedInUserHandler = new Thread(new SignedInUserHandler(currentUserSocketForSignin, enteredUsername));
        signedInUserHandler.start();

        Thread.currentThread().interrupt();
    }

    /**
     * this method handles sign in option to sign in user.
     */
    private void signup() {
        try {
            currentUserSocketForSignup = new Socket(serverIp, 6100);
            System.out.println("Sign up socket to server successfully set");
            objectOutputStreamForSignup = new ObjectOutputStream(currentUserSocketForSignup.getOutputStream());
            System.out.println("OOS FOR SIGN UP SET!");
            objectInputStreamForSignup = new ObjectInputStream(currentUserSocketForSignup.getInputStream());
            System.out.println("OIS FOR SIGN UP SET!");
        } catch (Exception e) {
            System.out.println("Thread of sign in signup interrupted");
            e.printStackTrace();
        }

        String username = getUsername();
        String password = null;
        String email = null;
        if (!username.equals("/back")){
            password = getPass();
            if (!password.equals("/back")){
                email = getEmail();
            }
            if (!email.equals("/back")){
                finalizeSignup(username, password, email);
            }
        }
    }

    /**
     * this method checks entered username and returns it
     */
    private String getUsername() {
        System.out.println("Please choose a username: ");
        System.out.println("[CAUTION] >> Username must be a combination of at least 6 characters long. Alphabet letters and numbers and _ and - and . is accepted.");
        System.out.println("Type /back to get back!");
        String desiredEnteredUsername = "";
        boolean isEnteredUsernameOK = false;
        while (!isEnteredUsernameOK) {
            desiredEnteredUsername = scanner.nextLine();
            switch (desiredEnteredUsername) {
                case "/back":
                    return "/back";
                default:
                    try {
                        objectOutputStreamForSignup.writeObject(new Request(RequestType.CHECKIFUSERNAMEISOKATALL, desiredEnteredUsername));
                        objectOutputStreamForSignup.flush();
                        System.out.println("USERNAME SENT TO SERVER FOR CHECK!");
                        String serverResponse = (String) objectInputStreamForSignup.readObject();
                        switch (serverResponse) {
                            case "REGEXERROR":
                                System.out.println("PLEASE USE A PROPER USERNAME FORMAT AT LEAST 6 CHARS. ONLY LETTERS AND NUMBERS ARE ACCEPTED! TRY AGAIN!");
                                break;
                            case "AVAILABILITYERROR":
                                System.out.println("THIS USERNAME IS ALREADY TAKEN. CHOOSE A NEW ONE!");
                                break;
                            case "OK":
                                System.out.println("OK FOR USERNAME FROM SERVER RECEIVED");
                                return desiredEnteredUsername;
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR IN SIGN UP USERNAME SECTION");
                    }
            }
        }
        return "/back";
    }

    /**
     * this method checks entered password and returns it
     */
    private String getPass(){
        System.out.println("PLEASE CHOOSE A PASSWORD AT LEAST 8 CHARS LONG CONTAINING CAPITAL, SMALL LETTER AND NUMBER AND SPECIAL CHAR");
        boolean isEnteredPasswordOk = false;
        while (!isEnteredPasswordOk){
            String desiredEnteredPassword = scanner.nextLine();
            switch (desiredEnteredPassword){
                case "/back":
                    return "/back";
                default:
                    if (isPasswordRegexOK(desiredEnteredPassword)){
                        return desiredEnteredPassword;
                    }else {
                        System.out.println("PLEASE CONSIDER STATED FORMAT!");
                    }
            }
        }
        return null;
    }

    /**
     * this method checks entered email and returns it
     */
    private String getEmail() {
        System.out.println("into getting email");
        String desiredEnteredEmail = "";
        String serverResponse = "REGEXERROR";
        System.out.println("PLEASE ENTER YOUR EMAIL:");
        while (!serverResponse.equals("YES")){
            desiredEnteredEmail = scanner.nextLine();
            switch (desiredEnteredEmail){
                case "/back":
                    return "/back";
                default:
                    try {
                        objectOutputStreamForSignup.writeObject(new Request(RequestType.CHECKIFEMAILISOKATALL, desiredEnteredEmail));
                        System.out.println("entered email sent to server");

                        serverResponse = (String) objectInputStreamForSignup.readObject();
                        switch (serverResponse){
                            case "REGEXERROR":
                                System.out.println("PLEASE USE A PROPER EMAIL FORMAT aaaa@bbb.com");
                                break;
                            case "AVAILABILITYERROR":
                                System.out.println("THIS EMAIL IS ALREADY REGISTERED. YOU CAN USE SIGN IN MENU WITH TYPING /back.");
                                break;
                            case "OK":
                                return desiredEnteredEmail;
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR IN SIGN UP EMAIL SECTION");;
                    }
            }

        }
        return "/back";
    }

    /**
     * it does last step of signing up
     * @param username : username of user
     * @param password : password of user
     * @param email : email of user
     */
    private void finalizeSignup(String username, String password, String email) {
        try {
            objectOutputStreamForSignup.writeObject(new Request(RequestType.SIGNUPUSER, username+"+"+password+"+"+email));
            System.out.println("SUCCESSFULLY REGISTERED!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("USE SIGN IN MENU TO SIGN IN");
    }

    /**
     * it just returns that is it ok for setting password or not.
     * @param password
     * @return if it is valid it returns true else it rey=turns false
     */
    private boolean isPasswordRegexOK (String password){
        return password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,30}");
    }
}
