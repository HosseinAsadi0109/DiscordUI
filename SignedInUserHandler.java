import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * This Class is the user interface of the Discord program in the console and includes a main menu
 * and different menus to handle different parts of the Discord program.
 * it includes 4 fields and implements Runnable interface.
 * it is a thread and starts in SignInSignUp class.
 *
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public class SignedInUserHandler implements Runnable {
    // scanner object for scan from user and 6 strings for colorful printing.
    Scanner scanner = new Scanner(System.in);
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    // fields
    String username;
    private Socket userSocket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    // constructor
    /**
     * here class connects to server and sends and receives information.
     * @param userSocket  this thread connects to server with socket.
     * @param username  username is username of user that work with app.
     */
    public SignedInUserHandler(Socket userSocket, String username) {
        this.username = username;
        this.userSocket = userSocket;
        try {
            objectInputStream = new ObjectInputStream(userSocket.getInputStream());
            System.out.println("OIS SET FOR SIGNED IN USER HANDLER");
            objectOutputStream = new ObjectOutputStream(userSocket.getOutputStream());
            objectOutputStream.flush();
            System.out.println("OOS SET FOR SIGNED IN USER HANDLER");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * this method must be implements because we implemented Runnable interface.
     * it works until we log out from application.
     * it has switch case , each case handles one part of program for example case 1 handles private chats .at all we have 9 cases.
     * no parameter
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int choice;
            boolean inMenu = true;
            printMainMenu();
            while (inMenu) {
                try {
                    System.out.print(">> ");
                    choice = Integer.parseInt(scanner.nextLine());
                    scanner.nextLine();
                    switch (choice) {
                        case 1:
                            privateChats();
                            break;
                        case 2:
                            friendsStuff();
                            break;
                        case 3:
                            serversStuff();
                            break;
                        case 4:
                            manageFriendshipRequests();
                            break;
                        case 5:
                            globalSearch();
                            break;
                        case 6:
                            blockedUsers();
                            break;
                        case 7:
                            setting();
                            break;
                        case 8:
                            inMenu = false;
                            try {
                                objectOutputStream.writeObject(new Request(RequestType.LOGOUT, null));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Thread.currentThread().interrupt();
                            break;
                        default:
                            throw new Exception();
                    }
                }catch (Exception e ){
                    System.out.println("Invalid value!!!");
                }
            }
        }
    }

    /**
     * this method prints 8 lines that shows part of program.
     * 1- private chats : goes to the PVP chats part.
     * 2- friends stuff : goes to the friends stuff.
     * 3- servers stuff : goes to server stuff and manage it.
     * 4- manage friendship requests : sending and receiving requests.
     * 5- global search : searching users.
     * 6- blocked users : list of blocking users.
     * 7- settings : setting about users.
     * 8- log out: exiting from Discord.
     * no @param
     */

    private void printMainMenu() {
        System.out.println("1) Private chats");
        System.out.println("2) Friends stuff");
        System.out.println("3) Servers stuff");
        System.out.println("4) Manage friendship requests");
        System.out.println("5) Global search");
        System.out.println("6) Blocked users");
        System.out.println("7) Settings");
        System.out.println("8) Log out");
    }

    /**
     * is responsible for previewing and managing user private chats.
     * no parameter
     */
    public void privateChats() {
        while (true) {
            try {
                // at first we receive a number of unread chats
                objectOutputStream.writeObject(new Request(RequestType.NUMBEROFUNREADCHATS,null));
                int numberOfUnreadChats = (Integer) objectInputStream.readObject();
                System.out.println("1) Show private chats list ("+ numberOfUnreadChats+ ")");
                System.out.println("2) start a new chat");
                System.out.print("3) Back\n>> ");
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 1) {
                    ArrayList<String> friendsListInPrivateChat = new ArrayList<>();
                    ArrayList<Integer> numberOfUnreadMessagesOfEachChat = new ArrayList<>();
                    // getting friends and number of messages of each private chat
                    objectOutputStream.writeObject(new Request(RequestType.LISTOFFRIENDSFORPRIVATECHAT,null));
                    friendsListInPrivateChat  = (ArrayList<String>) objectInputStream.readObject();
                    objectOutputStream.writeObject(new Request(RequestType.NUMBEROFUNREADMESSAGESOFEACHCHAT,null));
                    numberOfUnreadMessagesOfEachChat =  (ArrayList<Integer>) objectInputStream.readObject();
                    int i = 1;
                    for(String x : friendsListInPrivateChat){
                        String showIndexAndName = i + ") "+ x;
                        System.out.print(showIndexAndName);
                        for(int j = 1;j<= showIndexAndName.length();j++)
                            System.out.print(" ");
                        System.out.println("("+numberOfUnreadMessagesOfEachChat.get(i-1)+") ");
                        i++;
                    }
                    System.out.println(i+ ") Back");
                    int choice1;
                    while (true) {
                        try {
                            System.out.print("please choose your index :\n>> ");
                            choice1 = Integer.parseInt(scanner.nextLine());
                            if(choice1 == i){
                                break;
                            }else if(choice1 <i && choice1>=1){
                                String friendUsername = friendsListInPrivateChat.get(i-1);
                                // print messages
                                objectOutputStream.writeObject(new Request(RequestType.MESSAGESLIST,friendUsername));
                                ArrayList<Message> messages = (ArrayList<Message>) objectInputStream.readObject();
                                for(Message x : messages){
//
                                }

                                // start a new chat...
                                System.out.println("write messages and type /back for exit from a chat :");
                                // create a thread...
                                while (true){
                                    // receive a chat
                                    String message = scanner.nextLine();
                                    // end of chat
                                    if(message.compareTo("/back")==0) {
                                        objectOutputStream.writeObject(new Request(RequestType.ENDOFCHAT, null));
                                        break;
                                    } else {
                                        // send a chat
                                        objectOutputStream.writeObject(new Request(RequestType.SENDAMESSAGE,null));

                                        // code.......



                                    }
                                }

                                break;
                            }else
                                throw new Exception();
                        } catch (Exception e) {
                            System.out.println("Invalid input");
                        }
                    }
                    if(choice1 == i) {
                        continue;
                    }
                    break;
                } else if (choice == 2) {
                    objectOutputStream.writeObject(new Request(RequestType.LISTOFNEWFRIENDSFORSTARTANEWCHAT, null));
                    ArrayList<String> newFriends = (ArrayList<String>) objectInputStream.readObject();
                    int i = 1;
                    for (String x : newFriends) {
                        System.out.println(i + ") " + x);
                        i++;
                    }
                    System.out.println(i + ") back");
                    int choice1;
                    while (true) {
                        try {
                            System.out.print("Please choose your index :\n>> ");
                            choice1 = Integer.parseInt(scanner.nextLine());
                            if(choice1 == i){
                                break;
                            }else if(choice1 <i && choice1>=1){
                                // start a new chat...
                                String friendUsername = newFriends.get(i-1);
                                System.out.println("write messages and type /back for exit from a chat :");
                                while (true){
                                    String message = scanner.nextLine();
                                    if(message.compareTo("/back")==0)
                                        break;
                                    else {
                                        objectOutputStream.writeObject(new Request(RequestType.SENDAMESSAGE,null));
                                    }
                                }
                                objectOutputStream.writeObject(new Request(RequestType.ENDOFCHAT, null));
                                break;
                            }else
                                throw new Exception();
                        } catch (Exception e) {
                            System.out.println("Invalid input");
                        }
                    }
                    if(choice1 == i) {
                        continue;
                    }
                    break;
                } else if (choice == 3) {
                    break;
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                System.out.println("Invalid input!!!");
            }
        }
    }

    /**
     * is responsible for previewing and managing friends stuff .
     * at first , user chooses a friend and has some options to call, chat, block and unfriend another users.
     * no parameter
     */
    private void friendsStuff() {
        // at first we get allfriends list...
        try {
            objectOutputStream.writeObject(new Request(RequestType.LISTOFFRIENDS, null));
            ArrayList<String> friendsStuff = (ArrayList<String>) objectInputStream.readObject();
            int i = 1;
            for(String x : friendsStuff){
                System.out.println(i +") "+ x);
                i++;
            }
            System.out.println(i+") Back");
            // now we choose friend
            int choice ;
            while (true){
                try {
                    System.out.print("Please choose friend :\n>>");
                    choice = Integer.parseInt(scanner.nextLine());
                    String friendUsername = friendsStuff.get(i-1);
                    if (choice == i) {
                        return;
                    } else if (choice >= 1 && choice < i) {
                        while (true){
                            try {
                                System.out.println("1) Unfriend");
                                System.out.println("2) Block & Unfriend");
                                System.out.println("3) Start a new chat");
                                System.out.println("4) Call");
                                System.out.println("5) Video call");
                                System.out.println("6) Back");
                                System.out.print("Please choose your index :\n>> ");
                                int choice1 = Integer.parseInt(scanner.nextLine());
                                if(choice1 == 1){
                                    objectOutputStream.writeObject(new Request(RequestType.UNFRIENDUSER,friendUsername));
                                    break;
                                }else if(choice1 == 2){
                                    objectOutputStream.writeObject(new Request(RequestType.BLOCKANDUNFRIENDUSER,friendUsername));
                                    break;
                                }else if(choice1 == 3){
                                    objectOutputStream.writeObject(new Request(RequestType.MESSAGESLIST,friendUsername));
                                    ArrayList<String> messages = (ArrayList<String>) objectInputStream.readObject();

                                    // code .....


                                    // start a new chat...
                                    System.out.println("write messages and type /back for exit from a chat :");
                                    while (true){
                                        // receive a chat
                                        String message = scanner.nextLine();
                                        // end of chat
                                        if(message.compareTo("/back")==0) {
                                            objectOutputStream.writeObject(new Request(RequestType.ENDOFCHAT, null));
                                            break;
                                        }
                                        else {
                                            // send a chat
                                            objectOutputStream.writeObject(new Request(RequestType.SENDAMESSAGE,null));


                                            // code.......



                                        }
                                    }
                                    break;
                                }else if(choice1 == 4){
                                    // coming soon...
                                    break;
                                }else if(choice1 == 5){
                                    // coming soon..
                                    break;
                                }else if(choice1 == 6){
                                    break;
                                }else {
                                    throw new Exception();
                                }
                            }catch (Exception e){
                                System.out.println("Invalid value!!!");
                            }
                        }
                        break;
                    } else {
                        throw new Exception();
                    }
                }catch (Exception e){
                    System.out.println("Invalid Input!!!");
                }
            }
        }catch (Exception e){
            System.out.println("Invalid value!!!");
        }
    }

    /**
     * is responsible for previewing and managing friends stuff .
     * at first, it shows menu that create a new server,manage all servers that user joined in and starting chat.
     */
    public void serversStuff(){
        System.out.println("1) Show servers");
        System.out.println("2) Create a server");
        System.out.println("3) Manage my servers");
        System.out.print("4) Back\n>> ");
        while (true){
            try{
                int choice = Integer.parseInt(scanner.nextLine());
                if(choice == 1){
                    objectOutputStream.writeObject(new Request(RequestType.LISTOFSERVERS,null));
                    ArrayList<String> myServers = (ArrayList<String>) objectInputStream.readObject();
                    int i = 1;
                    for(String x : myServers){
                        System.out.println(i +") "+ x);
                        i++;
                    }
                    System.out.println(i + ") Back");
                    System.out.println("Please choose your Server :");
                    while (true){
                        try{
                            int choice1 = Integer.parseInt(scanner.nextLine());
                            if(choice1 == i){
                                break;
                            }else if(choice1 >=1 && choice1<i){
                                objectOutputStream.writeObject(new Request(RequestType.LISTOFCHANNELSOFSERVER,myServers.get(i-1)));
                                ArrayList<String> channelsOfServer = (ArrayList<String>) objectInputStream.readObject();
                                i = 1;
                                for(String x : channelsOfServer){
                                    System.out.println(i +") "+ x);
                                    i++;
                                }
                                System.out.println(i + ") Back");
                                System.out.println("Please choose your Channel :");
                                while (true){
                                    try{
                                        int choice2 = Integer.parseInt(scanner.nextLine());
                                        if(choice2 == i){
                                            break;
                                        }else if(choice2 >=1 && choice2<i){


                                            // code...
                                            // chatroom or voice channel


                                            break;
                                        }else
                                            throw new Exception();
                                    }catch (Exception e){
                                        System.out.println("Invalid value!!!");
                                    }
                                }
                                break;
                            }else
                                throw new Exception();
                        }catch (Exception e){
                            System.out.println("Invalid value!!!");
                        }
                    }
                    break;
                }else  if(choice == 2){
                    try {
                        System.out.println("Please enter Server name :");
                        String serverName = scanner.nextLine();
                        objectOutputStream.writeObject(new Request(RequestType.ISOKSERVERNAME,serverName));
                        boolean isOk = (boolean) objectInputStream.readObject();
                        if(isOk) {
                            System.out.println("Server created successfully.");
                        }
                        else
                            System.out.println("Invalid name!!!try again");
                        while (true) {
                            try {
                                System.out.println("Do you want to add a user to your General channel ?");
                                System.out.println("1- " + ANSI_GREEN + "Yes" + ANSI_RESET + "          2- " + ANSI_RED + "NO " + ANSI_RESET);
                                int choice1 = Integer.parseInt(scanner.nextLine());
                                if(choice1 == 1){
                                    objectOutputStream.writeObject(new Request(RequestType.LISTOFFRIENDS,null));
                                    ArrayList<String> allFriends = (ArrayList<String>) objectInputStream.readObject();
                                    int i = 1;
                                    for(String x : allFriends){
                                        System.out.println(i +") "+ x);
                                        i++;
                                    }
                                    System.out.println(i + ") Back");
                                    do {
                                        try {
                                            System.out.println("Please choose your friend :");
                                            int choice3 = Integer.parseInt(scanner.nextLine());
                                            if (choice3 == i) {
                                                break;
                                            } else if (choice3 > 0 && choice3 < i) {
                                                String[] information = new String[2];
                                                information[0] = serverName;
                                                information[1] = allFriends.get(i - 1);
                                                objectOutputStream.writeObject(new Request(RequestType.ADDUSERTOGENERALCHANNEL, information));
                                                boolean isOk1 = (boolean) objectInputStream.readObject();
                                                if (isOk1) {
                                                    System.out.println("Successfully added to general channel");
                                                } else
                                                    System.out.println("Can not add to channel");
                                            } else {
                                                throw new Exception();
                                            }
                                            System.out.println("Do you want to continue ?");
                                            System.out.println("1- " + ANSI_GREEN + "Yes" + ANSI_RESET + "          2- " + ANSI_RED + "NO " + ANSI_RESET);
                                            choice3 = Integer.parseInt(scanner.nextLine());
                                            if (choice3 == 1) {
                                                continue;
                                            } else if (choice3 == 2) {
                                                break;
                                            } else
                                                throw new Exception();
                                        }catch (Exception e){
                                            System.out.println("Invalid input!!!");
                                        }
                                    }while (true);

                                }else if(choice1 == 2){
                                    break;
                                }else
                                    throw new Exception();
                            } catch (Exception e) {
                                System.out.println("Invalid value");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Unable to send server name to server");
                    }
                    break;
                }else  if(choice == 3){
                    objectOutputStream.writeObject(new Request(RequestType.LISTOFSERVERS,null));
                    ArrayList<String> myServers = (ArrayList<String>) objectInputStream.readObject();
                    int i = 1;
                    for(String x : myServers){
                        System.out.println(i +") "+ x);
                        i++;
                    }
                    System.out.println(i + ") Back");
                    System.out.println("Please choose your Server :");
                    while(true){
                        try{
                            int choice1 = Integer.parseInt(scanner.nextLine());
                            if(choice1 == i){
                                break;
                            }else if(choice1 >= 1 && choice1 < i){
                                objectOutputStream.writeObject(new Request(RequestType.GETROLEOFUSERINTHISSERVER,myServers.get(i-1)));
                                String userRole = (String) objectInputStream.readObject();
                                if(userRole.equals("GeneralAdmin")){
                                    generalAdminHandlerMenu(myServers.get(i-1));
                                }else if(userRole.equals("ChannelAdmin")){
                                    objectOutputStream.writeObject(new Request(RequestType.GETADMINACCESS,myServers.get(i-1)));
                                    String adminAccess = (String) objectInputStream.readObject();
                                    adminMenuHandler(myServers.get(i-1),adminAccess);
                                }else if(userRole.equals("User")){
                                    userMenuHandler(myServers.get(i-1));
                                }
                            }else
                                throw new Exception();
                        }catch (Exception e){
                            System.out.println("Invalid value!!!");
                        }
                    }
                    break;
                }else if(choice == 4){
                    break;
                } else {
                    throw  new Exception();
                }
            }catch (Exception e){
                System.out.println("Invalid input!!!");
            }
        }

    }
    private void userMenuHandler(String serverName){
        System.out.println("1) Leave Server");
        System.out.println("2) Leave Channel");
        System.out.println("3) back");
        while (true){
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if(choice == 1){
                    objectOutputStream.writeObject(new Request(RequestType.LEAVEFROMSERVER,serverName));
                    System.out.println("Successfully Left");
                    break;
                }else if(choice == 2){
                    try{
                        objectOutputStream.writeObject(new Request(RequestType.LISTOFCHANNELSOFSERVERTHATUSERJOINEDIN,serverName));
                        ArrayList<String> myChannels = (ArrayList<String>) objectInputStream.readObject();
                        int i = 1;
                        for(String x : myChannels){
                            System.out.println(i +") "+ x);
                            i++;
                        }
                        System.out.println(i+") Back");
                        while (true){
                            try {
                                System.out.println("Please choose your index : ");
                                int choice1 = Integer.parseInt(scanner.nextLine());
                                if (choice1 == i) {
                                    break;
                                } else if (choice1 >= 1 && choice1 < i) {
                                    String[] information = new String[2];
                                    information[0] = serverName;
                                    information[1] = myChannels.get(i-1);
                                    objectOutputStream.writeObject(new Request(RequestType.LEFTCHANNEL,information));
                                    System.out.println(myChannels.get(i-1)+" Successfully Removed.");
                                    break;
                                } else
                                    throw new Exception();
                            }catch (Exception e){
                                System.out.println("Invalid value!!!");
                            }
                        }
                    }catch (Exception e){
                        System.out.println("Connection Lost...");
                    }
                    break;
                }else if(choice == 3){
                    return;
                }else
                    throw new Exception();
            }catch (Exception e){
                System.out.println("Invalid input!!!");
            }
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * @param serverName : name of server
     */
    private void addChannel(String serverName){
        try {
            while (true){
                try{
                    System.out.println("1) Text Channel");
                    System.out.println("2) Voice Channel");
                    System.out.println("3) Back");
                    int choice = Integer.parseInt(scanner.nextLine());
                    if(choice == 1){
                        System.out.println("Please enter Channel name : ");
                        String channelName = scanner.nextLine();
                        String[] information = new String[2];
                        information[0] = serverName;
                        information[1] = channelName;
                        objectOutputStream.writeObject(new Request(RequestType.ISOKTOCREATETEXTCHANNEL, information));
                        boolean isOk = (boolean) objectInputStream.readObject();
                        if (isOk) {
                            System.out.println("Channel Created Successfully");
                        } else
                            System.out.println("This Channel already existed!!!");
                        break;
                    }else if(choice == 2){
                        System.out.println("Please enter Channel name : ");
                        String channelName = scanner.nextLine();
                        String[] information = new String[2];
                        information[0] = serverName;
                        information[1] = channelName;
                        objectOutputStream.writeObject(new Request(RequestType.ISOKTOCREATEVOICECHANNEL, information));
                        boolean isOk = (boolean) objectInputStream.readObject();
                        if (isOk) {
                            System.out.println("Channel Created Successfully");
                        } else
                            System.out.println("This Channel already existed!!!");
                        break;
                    }else if(choice == 3){
                        return;
                    }else
                        throw new Exception();
                }catch (Exception e){
                    System.out.println("Invalid Input!!!");
                }
            }
        }catch (Exception e){
            System.out.println("Connection Lost...");
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can remove channel from a server.
     * at first, it receives a name of channel and removes it.
     * @param serverName : name of server
     */
    private void removeChannel (String serverName){
        try{
            objectOutputStream.writeObject(new Request(RequestType.LISTOFCHANNELSOFSERVER,serverName));
            ArrayList<String> myChannels = (ArrayList<String>) objectInputStream.readObject();
            int i = 1;
            for(String x : myChannels){
                System.out.println(i +") "+ x);
                i++;
            }
            System.out.println(i+") Back");
            while (true){
                try {
                    System.out.println("Please choose your index : ");
                    int choice1 = Integer.parseInt(scanner.nextLine());
                    if (choice1 == i) {
                        break;
                    } else if (choice1 >= 1 && choice1 < i) {
                        String[] information = new String[2];
                        information[0] = serverName;
                        information[1] = myChannels.get(i-1);
                        objectOutputStream.writeObject(new Request(RequestType.DELETECHANNEL,information));
                        System.out.println(myChannels.get(i-1)+" Successfully Removed.");
                        break;
                    } else
                        throw new Exception();
                }catch (Exception e){
                    System.out.println("Invalid value!!!");
                }
            }
        }catch (Exception e){
            System.out.println("Connection Lost...");
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add User to a server.
     * at first, it receives a name of server and add user to server.
     * then it handles events that user chooses it.
     * @param serverName : name of server
     */
    private void addUserToServer(String serverName){
        try{
            objectOutputStream.writeObject(new Request(RequestType.LISTOFFRIENDS,null));
            ArrayList<String> myFriends = (ArrayList<String>) objectInputStream.readObject();
            int i = 1;
            for(String x : myFriends){
                System.out.println(i +") "+ x);
                i++;
            }
            System.out.println(i+") Back");
            while (true){
                try {
                    System.out.println("Please choose your index : ");
                    int choice1 = Integer.parseInt(scanner.nextLine());
                    if (choice1 == i) {
                        break;
                    } else if (choice1 >= 1 && choice1 < i) {
                        String[] information = new String[2];
                        information[0] = serverName;
                        information[1] = myFriends.get(i-1);
                        objectOutputStream.writeObject(new Request(RequestType.ADDUSERTOSERVER,information));
                        boolean isSuccessful = (boolean) objectInputStream.readObject();
                        if(isSuccessful)
                        System.out.println(myFriends.get(i-1)+" Successfully Added.");
                        else
                            System.out.println("he has already been in channel");
                        break;
                    } else
                        throw new Exception();
                }catch (Exception e){
                    System.out.println("Invalid value!!!");
                }
            }
        }catch(Exception e){
            System.out.println("Connection Lost...");
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can remove user from a server.
     * at first, it receives a name of server and remove user from a server.
     * then it handles events that user chooses it.
     * @param serverName : name of server
     */
    private void removeUserFromServer(String serverName){
        try {
            objectOutputStream.writeObject(new Request(RequestType.LISTOFSERVERUSERS,serverName));
            ArrayList<String> myUsers = (ArrayList<String>) objectInputStream.readObject();
            int i = 1;
            for(String x : myUsers){
                System.out.println(i +") "+ x);
                i++;
            }
            System.out.println(i+") Back");
            while (true){
                try {
                    System.out.println("Please choose your index : ");
                    int choice1 = Integer.parseInt(scanner.nextLine());
                    if (choice1 == i) {
                        break;
                    } else if (choice1 >= 1 && choice1 < i) {
                        String[] information = new String[2];
                        information[0] = serverName;
                        information[1] = myUsers.get(i-1);
                        objectOutputStream.writeObject(new Request(RequestType.DELETEUSERFROMSERVER,myUsers.get(i-1)));
                        System.out.println(myUsers.get(i-1)+" Successfully Removed.");
                        break;
                    } else
                        throw new Exception();
                }catch (Exception e){
                    System.out.println("Invalid value!!!");
                }
            }
        }catch (Exception e){
            System.out.println("Connection Lost...");
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can change name of server.
     * at first, it receives a name of channel & add friends to channel.
     * @param serverName : name of server
     */
    private void changeServerName(String serverName){
        try {
            String serverNewName;
            while (true) {
                try {
                    System.out.println("Please enter new name or type /back to get back:");
                    serverNewName = scanner.nextLine();
                    if(serverNewName.equals("/back")){
                        break;
                    }else {
                        String[] information = new String[2];
                        information[0] = serverName;
                        information[1] = serverNewName;
                        objectOutputStream.writeObject(new Request(RequestType.SETNEWNAMEFORSERVER, information));
                        boolean isSuccessful = (boolean) objectInputStream.readObject();
                        if(isSuccessful)
                        System.out.println("Server name changed successfully!!!");
                        else
                            System.out.println("Invalid name try again");
                        break;
                    }
                }catch (Exception e){
                    System.out.println("Invalid Input!!!");
                }
            }
        }catch (Exception e){
            System.out.println("Connection Lost...");
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can see history of channel.
     * at first, it receives a name of channel & show date of first chat , count of messages , count of subscribers.
     * then it handles events that user chooses it.
     * @param serverName : name of server
     */
    private void seeHistoryOfChannel(String serverName){
        try{
            objectOutputStream.writeObject(new Request(RequestType.LISTOFCHANNELSOFSERVER,serverName));
            ArrayList<String> myChannels = (ArrayList<String>) objectInputStream.readObject();
            int i = 1;
            for(String x : myChannels){
                System.out.println(i +") "+ x);
                i++;
            }
            System.out.println(i+") Back");
            while (true){
                try {
                    System.out.println("Please choose your index : ");
                    int choice1 = Integer.parseInt(scanner.nextLine());
                    if (choice1 == i) {
                        break;
                    } else if (choice1 >= 1 && choice1 < i) {
                        String[] information = new String[2];
                        information[0] = serverName;
                        information[1] = myChannels.get(i-1);
                        objectOutputStream.writeObject(new Request(RequestType.HISTORYOFCHANNEL,information));
                        ArrayList<String> information1 = (ArrayList<String>) objectInputStream.readObject();
                        for(String x : information1){
                            System.out.println(x);
                        }
                        break;
                    } else
                        throw new Exception();
                }catch (Exception e){
                    System.out.println("Invalid value!!!");
                }
            }
        }catch (Exception e){
            System.out.println("Connection Lost...");
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can ban user from a server.
     * at first, it receives a name of channel & user then user can not watch and type messages.
     * then it handles events that user chooses it.
     * @param serverName : name of server
     */
    private void banUserFromServer(String serverName){
        try{
            objectOutputStream.writeObject(new Request(RequestType.LISTOFSERVERUSERS,serverName));
            ArrayList<String> allUsers = (ArrayList<String>) objectInputStream.readObject();
            int i = 1;
            for(String x : allUsers){
                System.out.println(i +") "+ x);
                i++;
            }
            System.out.println(i+") Back");
            while (true){
                try {
                    System.out.println("Please choose your index : ");
                    int choice1 = Integer.parseInt(scanner.nextLine());
                    if (choice1 == i) {
                        break;
                    } else if (choice1 >= 1 && choice1 < i) {
                        String[] information = new String[2];
                        information[0] = serverName;
                        information[1] = allUsers.get(i-1);
                        objectOutputStream.writeObject(new Request(RequestType.LISTOFCHANNELSTHATUSERJOINEDINASERVER,information));
                        ArrayList<String> channelsThatUserJoined = (ArrayList<String>) objectInputStream.readObject();
                        int j = 1;
                        for(String x : channelsThatUserJoined){
                            System.out.println(j +") "+ x);
                            j++;
                        }
                        System.out.println(j+") Back");
                        while (true){
                            try {
                                System.out.println("Please choose your index : ");
                                int choice2 = Integer.parseInt(scanner.nextLine());
                                if (choice2 == i) {
                                    break;
                                } else if (choice2 >= 1 && choice2 < i) {
                                    System.out.println("1) Ban User");
                                    System.out.println("2) UnBan User");
                                    System.out.println("3) Back");
                                    try{
                                        int choice3 = Integer.parseInt(scanner.nextLine());
                                        if(choice3 == 1){
                                            String[] information1 = new String[3];
                                            information1[0] = serverName;
                                            information1[1] = channelsThatUserJoined.get(j-1);
                                            information1[2] = allUsers.get(i-1);
                                            objectOutputStream.writeObject(new Request(RequestType.BANUSERFROMCHANNEL,information1));
                                            boolean isSuccessful = (boolean) objectInputStream.readObject();
                                            if(isSuccessful){
                                                System.out.println("Successfully banned from "+ information1[1] +".");
                                            }else {
                                                System.out.println(information1[2]+" has already been banned.");
                                            }
                                        }else if(choice3 == 2){
                                            String[] information1 = new String[3];
                                            information1[0] = serverName;
                                            information1[1] = channelsThatUserJoined.get(j-1);
                                            information1[2] = allUsers.get(i-1);
                                            objectOutputStream.writeObject(new Request(RequestType.UNBANUSERFROMCHANNEL,information1));
                                            boolean isSuccessful = (boolean) objectInputStream.readObject();
                                            if(isSuccessful){
                                                System.out.println("Successfully Unbanned from "+ information1[1] +".");
                                            }else {
                                                System.out.println(information1[2]+" has already been Unbanned.");
                                            }
                                        }else if(choice3 == 3){
                                            break;
                                        }else
                                            throw new Exception();
                                    }catch (Exception e){
                                        System.out.println("Invalid Input!!!");
                                    }
                                    break;
                                } else
                                    throw new Exception();
                            }catch (Exception e){
                                System.out.println("Invalid value!!!");
                            }
                        }
                        break;
                    } else
                        throw new Exception();
                }catch (Exception e){
                    System.out.println("Invalid value!!!");
                }
            }
        }catch (Exception e){
            System.out.println("Connection Lost...");
        }
    }
    // *****
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * @param serverName : name of server
     */
    private void limitUserFromChannel(String serverName){
        try{
            objectOutputStream.writeObject(new Request(RequestType.LISTOFSERVERUSERS,serverName));
            ArrayList<String> allUsers = (ArrayList<String>) objectInputStream.readObject();
            int i = 1;
            for(String x : allUsers){
                System.out.println(i +") "+ x);
                i++;
            }
            System.out.println(i+") Back");
            while (true){
                try {
                    System.out.println("Please choose your index : ");
                    int choice1 = Integer.parseInt(scanner.nextLine());
                    if (choice1 == i) {
                        break;
                    } else if (choice1 >= 1 && choice1 < i) {
                        String[] information = new String[2];
                        information[0] = serverName;
                        information[1] = allUsers.get(i-1);
                        objectOutputStream.writeObject(new Request(RequestType.LISTOFCHANNELSTHATUSERJOINEDINASERVER,information));
                        ArrayList<String> channelsThatUserJoined = (ArrayList<String>) objectInputStream.readObject();
                        int j = 1;
                        for(String x : channelsThatUserJoined){
                            System.out.println(j +") "+ x);
                            j++;
                        }
                        System.out.println(j+") Back");
                        while (true){
                            try {
                                System.out.println("Please choose your index : ");
                                int choice2 = Integer.parseInt(scanner.nextLine());
                                if (choice2 == i) {
                                    break;
                                } else if (choice2 >= 1 && choice2 < i) {
                                    System.out.println("1) Restrict User from Channel");
                                    System.out.println("2) Unrestricted User from Channel ");
                                    System.out.println("3) Back");
                                    try{
                                        int choice3 = Integer.parseInt(scanner.nextLine());
                                        if(choice3 == 1){
                                            String[] information1 = new String[3];
                                            information1[0] = serverName;
                                            information1[1] = channelsThatUserJoined.get(j-1);
                                            information1[2] = allUsers.get(i-1);
                                            objectOutputStream.writeObject(new Request(RequestType.RESTRICTEDUSERFROMCHANNEL,information1));
                                            boolean isSuccessful = (boolean) objectInputStream.readObject();
                                            if(isSuccessful){
                                                System.out.println("Successfully restrict from "+ information1[1] +".");
                                            }else {
                                                System.out.println(information1[2]+" has already been restricted.");
                                            }
                                        }else if(choice3 == 2){
                                            String[] information1 = new String[3];
                                            information1[0] = serverName;
                                            information1[1] = channelsThatUserJoined.get(j-1);
                                            information1[2] = allUsers.get(i-1);
                                            objectOutputStream.writeObject(new Request(RequestType.UNRESTRICTEDUSERFROMCHANNEL,information1));
                                            boolean isSuccessful = (boolean) objectInputStream.readObject();
                                            if(isSuccessful){
                                                System.out.println("Successfully Unrestricted from "+ information1[1] +".");
                                            }else {
                                                System.out.println(information1[2]+" has already been Unrestricted.");
                                            }
                                        }else if(choice3 == 3){
                                            break;
                                        }else
                                            throw new Exception();
                                    }catch (Exception e){
                                        System.out.println("Invalid Input!!!");
                                    }
                                    break;
                                } else
                                    throw new Exception();
                            }catch (Exception e){
                                System.out.println("Invalid value!!!");
                            }
                        }
                        break;
                    } else
                        throw new Exception();
                }catch (Exception e){
                    System.out.println("Invalid value!!!");
                }
            }
        }catch (Exception e){
            System.out.println("Connection Lost...");
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * @param serverName : name of server
     */
    private void pinnedMessage(String serverName){
        System.out.println("1) Pinning Message");
        System.out.println("2) Unpinning Message");
        System.out.println("3) Back");
        while (true){
            try{
                int choice = Integer.parseInt(scanner.nextLine());
                if(choice == 1){
                    System.out.println("Please enter the ID of message :");
                    String id = scanner.nextLine();
                    String[] information = new String[2];
                    information[0] = serverName;
                    information[1] = id;
                    objectOutputStream.writeObject(new Request(RequestType.PINNEDMESSAGE,information));
                    boolean isOk = (boolean) objectInputStream.readObject();
                    if(isOk){
                        System.out.println("Successfully Pinned");
                    }else{
                        System.out.println("Invalid Id!!!");
                    }
                    break;
                } else if(choice == 2){
                    System.out.println("Please enter the ID of message :");
                    String id = scanner.nextLine();
                    String[] information = new String[2];
                    information[0] = serverName;
                    information[1] = id;
                    objectOutputStream.writeObject(new Request(RequestType.UNPINNEDMESSAGE,id));
                    boolean isOk = (boolean) objectInputStream.readObject();
                    if(isOk){
                        System.out.println("Successfully UnPinned");
                    }else{
                        System.out.println("Invalid Id!!!");
                    }
                   break;
                } else if(choice == 3){
                   break;
                }else
                    throw new Exception();
            }catch (Exception e){
                System.out.println("Invalid value!!!");
            }
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * @param serverName : name of server
     */
    private void chooseAdmin(String serverName){
        try{
            objectOutputStream.writeObject(new Request(RequestType.LISTOFSERVERUSERS,serverName));
            ArrayList<String> allUsers = (ArrayList<String>) objectInputStream.readObject();
            int i = 1;
            for(String x : allUsers){
                System.out.println(i +") "+ x);
                i++;
            }
            System.out.println(i+") Back");
            while (true){
                try {
                    System.out.println("Please choose your index : ");
                    int choice1 = Integer.parseInt(scanner.nextLine());
                    if (choice1 == i) {
                        break;
                    } else if (choice1 >= 1 && choice1 < i) {
                        System.out.println("Admin Items :");
                        System.out.println("1) Adding Channel");
                        System.out.println("2) Removing Channel");
                        System.out.println("3) Removing User from Channel");
                        System.out.println("4) Baning User from Server");
                        System.out.println("5) Limiting User from channel");
                        System.out.println("6) Changing server name");
                        System.out.println("7) See History of chat");
                        System.out.println("8) Pinning Message");
                        System.out.println("choose this items or type /back to get back :");
                        String[] information = new String[2];
                        information[0] = serverName;
                        information[1] = allUsers.get(i-1);
                        while (true){
                            try {
                                String input = scanner.nextLine();
                                if (input.equals("/back")) {
                                    break;
                                } else if (input.equals("1")) {
                                    objectOutputStream.writeObject(new Request(RequestType.SETADMINACESSFORADDINGCHANNEL,information));
                                    boolean isSuccessfull = (boolean) objectInputStream.readObject();
                                    if(isSuccessfull){
                                        System.out.println("Successfully Done!");
                                    }else
                                        System.out.println("This item has already been set.");
                                    continue;
                                } else if (input.equals("2")) {
                                    objectOutputStream.writeObject(new Request(RequestType.SETADMINACESSFORREMOVINGCHANNEL,information));
                                    boolean isSuccessfull = (boolean) objectInputStream.readObject();
                                    if(isSuccessfull){
                                        System.out.println("Successfully Done!");
                                    }else
                                        System.out.println("This item has already been set.");
                                    continue;
                                } else if (input.equals("3")) {
                                    objectOutputStream.writeObject(new Request(RequestType.SETADMINACESSFORREMOVINGUSERFROMCHANNEL,information));
                                    boolean isSuccessfull = (boolean) objectInputStream.readObject();
                                    if(isSuccessfull){
                                        System.out.println("Successfully Done!");
                                    }else
                                        System.out.println("This item has already been set.");
                                    continue;
                                } else if (input.equals("4")) {
                                    objectOutputStream.writeObject(new Request(RequestType.SETADMINACESSFORBANNINGUSERFROMSERVER,information));
                                    boolean isSuccessfull = (boolean) objectInputStream.readObject();
                                    if(isSuccessfull){
                                        System.out.println("Successfully Done!");
                                    }else
                                        System.out.println("This item has already been set.");
                                    continue;
                                } else if (input.equals("5")) {
                                    objectOutputStream.writeObject(new Request(RequestType.SETADMINACESSFORLIMMITINGUSERFROMCHANNEL,information));
                                    boolean isSuccessfull = (boolean) objectInputStream.readObject();
                                    if(isSuccessfull){
                                        System.out.println("Successfully Done!");
                                    }else
                                        System.out.println("This item has already been set.");
                                    continue;
                                } else if (input.equals("6")) {
                                    objectOutputStream.writeObject(new Request(RequestType.SETADMINACESSFORCHANGINGSERVERNAME,information));
                                    boolean isSuccessfull = (boolean) objectInputStream.readObject();
                                    if(isSuccessfull){
                                        System.out.println("Successfully Done!");
                                    }else
                                        System.out.println("This item has already been set.");
                                    continue;
                                } else if (input.equals("7")) {
                                    objectOutputStream.writeObject(new Request(RequestType.SETADMINACESSFORSEEHISTORYOFCHAT,information));
                                    boolean isSuccessfull = (boolean) objectInputStream.readObject();
                                    if(isSuccessfull){
                                        System.out.println("Successfully Done!");
                                    }else
                                        System.out.println("This item has already been set.");
                                    continue;
                                } else if (input.equals("8")) {
                                    objectOutputStream.writeObject(new Request(RequestType.SETADMINACESSFORPINNINGMESSAGE,information));
                                    boolean isSuccessfull = (boolean) objectInputStream.readObject();
                                    if(isSuccessfull){
                                        System.out.println("Successfully Done!");
                                    }else
                                        System.out.println("This item has already been set.");
                                    continue;
                                } else
                                    throw new Exception();
                            }catch (Exception e){
                                System.out.println("Invalid input!!!");
                            }
                        }
                    } else
                        throw new Exception();
                }catch (Exception e){
                    System.out.println("Invalid value!!!");
                }
            }
        }catch (Exception e){
            System.out.println("Connection Lost...");
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * @param serverName : name of server
     */
    private void generalAdminHandlerMenu(String serverName){
        System.out.println("1) Add Channel"); // done successfully
        System.out.println("2) Remove Channel");  // done successfully
        System.out.println("3) Remove User from Channel"); // done successfully
        System.out.println("4) Add user to Channel"); // done successfully
        System.out.println("5) Ban User from Server");  // done successfully
        System.out.println("6) Limit User from channel"); // done successfully
        System.out.println("7) Change server name");   // done 90% just rejex
        System.out.println("8) See History of chat"); // tedad payam , time ijad channel daryaft va print shavad
        System.out.println("9) Pinned Message");  // done successfully
        System.out.println("10) Choose Admin"); // done successfully
        System.out.println("11) Back");
        while (true){
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 1) {
                    addChannel(serverName);
                    break;
                }else if(choice == 2){
                    removeChannel(serverName);
                    break;
                }else if(choice == 3){
                    removeUserFromServer(serverName);
                    break;
                }else if(choice == 4){
                    addUserToServer(serverName);
                    break;
                }else if(choice == 5){
                     banUserFromServer(serverName);
                }else if(choice == 6){
                      limitUserFromChannel(serverName);
                }else if(choice == 7){
                    changeServerName(serverName);
                    break;
                }else if(choice == 8){
                    seeHistoryOfChannel(serverName);
                    break;
                }else if(choice == 9){
                    pinnedMessage(serverName);
                     break;
                }else if(choice == 10){
                    chooseAdmin(serverName);
                }else if(choice == 11){
                    break;
                }else
                    throw new Exception();
            }catch (Exception e){
                System.out.println("Invalid Value!!!");
            }
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * @param serverName : name of server
     */
    private void adminMenuHandler(String serverName,String adminId){
        System.out.println("1) Add Channel"); // done successfully
        System.out.println("2) Remove Channel");  // done successfully
        System.out.println("3) Remove User from Channel"); // done successfully
        System.out.println("4) Ban User from Server");  // done successfully
        System.out.println("5) Limit User from channel");  // Done successfully
        System.out.println("6) Change server name");   // done 90% just rejex
        System.out.println("7) See History of chat"); // tedad payam , time ijad channel daryaft va print shavad // done successfully
        System.out.println("8) Pinned Message");   // dastan inke chetor pin shavad moshakhas shavad
        System.out.println("9) Back");
        while (true){
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 1) {
                    if(adminId.charAt(0) == '1') {
                        addChannel(serverName);
                    }else
                        System.out.println("You cannot change this part.");
                    break;
                }else if(choice == 2){
                    if(adminId.charAt(1) == '1') {
                        removeChannel(serverName);
                    }else
                        System.out.println("You cannot change this part.");
                    break;
                }else if(choice == 3){
                    if(adminId.charAt(2) == '1') {
                        removeUserFromServer(serverName);
                    }else
                        System.out.println("You cannot change this part.");
                    break;
                }else if(choice == 4){
                    if(adminId.charAt(3) == '1') {
                        banUserFromServer(serverName);
                    }else
                        System.out.println("You cannot change this part.");
                    break;
                }else if(choice == 5){
                    if(adminId.charAt(4) == '1') {
                        limitUserFromChannel(serverName);
                    }else
                        System.out.println("You cannot change this part.");
                }else if(choice == 6){
                    if(adminId.charAt(5) == '1') {
                        changeServerName(serverName);
                    }else
                        System.out.println("You cannot change this part.");
                }else if(choice == 7){
                    if(adminId.charAt(6) == '1') {
                        seeHistoryOfChannel(serverName);
                    }else
                        System.out.println("You cannot change this part.");
                    break;
                }else if(choice == 8){
                    if(adminId.charAt(7) == '1') {
                    pinnedMessage(serverName);
                    }else
                        System.out.println("You cannot change this part.");
                    break;
                }else if(choice == 9){
                    break;
                }else
                    throw new Exception();
            }catch (Exception e){
                System.out.println("Invalid Value!!!");
            }
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * it has not parameter
     */
    private void manageFriendshipRequests() {
        boolean inMenu = true;
        while (inMenu) {
            String[] status = null;
            try {
                objectOutputStream.writeObject(new Request(RequestType.NUMBEROFSENTANDRECEIVEDFRIENDSHIPREQUESTS, null));
                String response = (String) objectInputStream.readObject();
                status = response.split("\\+");
            } catch (Exception e) {
                System.out.println("UNABLE TO SEND OR RECEIVE FRIENDSHIP REQUESTS NUMBER");
            }
            printManageFriendShipRequestsMenu(status);
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    if (status[0].equals("0")) {
                        System.out.println("NO RECEIVED FRIENDSHIP REQUEST!!!");
                    }else {
                        ArrayList<String> pendingReceivedRequests = null;
                        try {
                            objectOutputStream.writeObject(new Request(RequestType.LISTOFRECEIVEDFRIENDREQUESTS, null));
                            pendingReceivedRequests = (ArrayList<String>) objectInputStream.readObject();
                            System.out.println();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        int i = 1;
                        for (String pendingReceivedRequest : pendingReceivedRequests) {
                            System.out.println(i + ") Remove request: " + pendingReceivedRequest);
                            i++;
                        }
                        System.out.println("Enter a number or type /back to get back");
                        String input1 = scanner.nextLine();
                        //This method input must be rechecked
                        if (!input1.equals("/back") && Integer.parseInt(input1)<pendingReceivedRequests.size() && Integer.parseInt(input1)>0) {
                            String targetUsername = pendingReceivedRequests.get(Integer.parseInt(input1) - 1);
                            System.out.println("Do you want accept or reject " + targetUsername + " ?");
                            System.out.println("1- " + ANSI_GREEN + "Accept\t" + ANSI_RESET + "2-" + ANSI_RED + "Reject\t" + ANSI_RESET + "3- " + ANSI_BLUE + "Back" + ANSI_RESET);
                            int input2 = Integer.parseInt(scanner.nextLine());
                            try {
                                if (input2 == 1) {
                                    objectOutputStream.writeObject(new Request(RequestType.ACCEPTFRIENDSHIPREQUEST,targetUsername));
                                    System.out.println(targetUsername+" Successfully accepted");
                                } else if (input2 == 2) {
                                    objectOutputStream.writeObject(new Request(RequestType.REJECTFRIENDSHIPREQUEST,targetUsername));
                                    System.out.println(targetUsername+" Successfully rejected");
                                }else{
                                    throw new Exception();
                                }
                            } catch (Exception e) {
                                System.out.println("Invalid Input!!!");
                            }
                        }
                    }
                    break;

                case "2":
                    if (status[1].equals("0")) {
                        System.out.println("NO SENT FRIENDSHIP REQUEST!!!");
                    } else {
                        ArrayList<String> pendingSentRequests;
                        try {
                            objectOutputStream.writeObject(new Request(RequestType.LISTOFSENTFRIENDREQUESTS, null)); // new
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            pendingSentRequests = (ArrayList<String>) objectInputStream.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        int j = 1;
                        for (String pendingSentRequest : pendingSentRequests) {
                            System.out.println(j + ") " + pendingSentRequest);
                            j++;
                        }

                        System.out.println("Enter a username number to remove friendship request or type /back to get back");
                        String input = scanner.nextLine();
                        if (!input.equals("/back") && Integer.parseInt(input)<=pendingSentRequests.size() && Integer.parseInt(input) > 0) {
                            String targetUsername = pendingSentRequests.get(Integer.parseInt(input) - 1);
                            try {
                                objectOutputStream.writeObject(new Request(RequestType.REMOVESENTFRIENDSHIPREQUEST, targetUsername));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("Request removed successfully!");
                        }else {
                            System.out.println("INVALID INPUT");
                        }
                    }
                    break;
                case "3":
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * it has not parameter
     */
    private void printManageFriendShipRequestsMenu(String[] status) {
        System.out.println("1) Received friendship request (" + status[0] + ")");
        System.out.println("2) Sent friendship requests (" + status[1] + ")");
        System.out.println("3) Back");
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * it has not parameter
     */
    public void globalSearch() {
        System.out.println("in global search");
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("1) Search for a user");
            System.out.println("2) Back");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    ArrayList<String> foundUsers = new ArrayList<>();
                    System.out.println("Please enter a username to search for:");
                    scanner.nextLine();
                    String searchKey = scanner.nextLine();
                    if (!searchKey.equals(username)){
                        try {
                            objectOutputStream.writeObject(new Request(RequestType.GLOBALSEARCHFORAUSERNAME, searchKey));
                            System.out.println("Search key sent to server");
                            foundUsers = (ArrayList<String>) objectInputStream.readObject();
                        } catch (Exception e) {
                            System.out.println("UNABLE TO SEND SEARCH FOR USER REQUEST TO SERVER OR UNABLE TO READ FROM SERVER");
                        }
                        if (foundUsers.size() == 0) {
                            System.out.println("No user is named \" " + searchKey + " \"");
                        } else {
                            System.out.println("Users:");
                            int i = 1;
                            for (String foundUser : foundUsers) {
                                System.out.println("\t" + i + ". " + foundUser);
                                i++;
                            }
                            System.out.println("Enter a username number or type /back to get back");
                            String input = scanner.nextLine();
                            if (!input.equals("/back")) {
                                String targetUsername = foundUsers.get(Integer.parseInt(input) - 1);
                                System.out.println("The chosen username is " + targetUsername);
                                innerMenuOfGlobalSearch(targetUsername);
                            }
                        }
                    }else {
                        System.out.println("IT IS YOU :)");
                    }
                    break;
                case 2:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * it has not parameter
     */
    private void innerMenuOfGlobalSearch(String targetUsername) {
        boolean inMenu = true;
        while (inMenu) {
            try {
                objectOutputStream.writeObject(new Request(RequestType.TODOWITHAUSEROFRESULTOFGLOBALSEARCH, targetUsername));
            } catch (IOException e) {
                System.out.println("UNABLE TO SEND A USERNAME FROM FOUND USERS TO SERVER");
            }
            String responseOfFriendshipCheckAndAlreadyRequestedOrNot = null;
            try {
                responseOfFriendshipCheckAndAlreadyRequestedOrNot = (String) objectInputStream.readObject();
            } catch (Exception e) {
                System.out.println("UNABLE TO RECEIVE THE ANSWER OF FRIENDSHIP AND REQUEST STATUS1");
            }
            System.out.println("*** " + targetUsername + " ***");
            if (responseOfFriendshipCheckAndAlreadyRequestedOrNot.charAt(0) == '1') {
                System.out.println("1) Unfriend");
            } else {
                char isFriendshipRequested = responseOfFriendshipCheckAndAlreadyRequestedOrNot.charAt(1);
                if (isFriendshipRequested == '1') {
                    System.out.println("1) Remove friendship request");
                } else {
                    System.out.println("1) Request for friendship");
                }
            }
            System.out.println("2) Start a chat");
            System.out.println("3) Block");
            System.out.println("4) Block and Unfriend");
            System.out.println("5) Back");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    if (responseOfFriendshipCheckAndAlreadyRequestedOrNot.charAt(0) == '1') {
                        try {
                            objectOutputStream.writeObject(new Request(RequestType.UNFRIEND, targetUsername));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Friend successfully unfriended");
                    } else {
                        if (responseOfFriendshipCheckAndAlreadyRequestedOrNot.charAt(1) == '1') {
                            try {
                                objectOutputStream.writeObject(new Request(RequestType.REMOVESENTFRIENDSHIPREQUEST, targetUsername));
                                System.out.println("Request successfully removed!");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            try {
                                objectOutputStream.writeObject(new Request(RequestType.SENDFRIENDSHIPREQUEST, targetUsername));
                                System.out.println("Request successfully sent!");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    break;
                case "2":
                    try {
                        // INJA RO MAN ZADAM
                        objectOutputStream.writeObject(new Request(RequestType.MESSAGESLIST, targetUsername));
                        ArrayList<Message> messages = (ArrayList<Message>) objectInputStream.readObject();
                        for (Message x : messages) {
//
                        }

                        // start a new chat...
                        System.out.println("write messages and type /back for exit from a chat :");
                        // create a thread...
                        while (true) {
                            // receive a chat
                            String message = scanner.nextLine();
                            // end of chat
                            if (message.compareTo("/back") == 0) {
                                objectOutputStream.writeObject(new Request(RequestType.ENDOFCHAT, null));
                                break;
                            } else {
                                // send a chat
                                objectOutputStream.writeObject(new Request(RequestType.SENDAMESSAGE, null));

                                // code.......


                            }
                        }
                    }catch (Exception e){
                        System.out.println("Invalid Input!!!");
                    }


                    break;
                case "3":
                    // INJA RO MAN ZADAM
                    try {
                        objectOutputStream.writeObject(new Request(RequestType.JUSTBLOCKEDUSER,targetUsername));
                        System.out.println("Successfully blocked "+ targetUsername);
                    }catch (Exception e){
                        System.out.println("Operation failure!!!");
                    }
                    break;
                case "4" :
                    try {
                        objectOutputStream.writeObject(new Request(RequestType.BLOCKANDUNFRIENDUSER,targetUsername));
                        System.out.println("Successfully blocked and unfriended "+ targetUsername);
                    }catch (Exception e){
                        System.out.println("Operation failure!!!");
                    }
                    break;
                case "5":
                    inMenu = false;
                    break;
            }

        }

    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * it has not parameter
     */
    public void blockedUsers() {
        try {
            // recieve blockd users
            ArrayList<String> blockedUsers;
            objectOutputStream.writeObject(new Request(RequestType.LISTOFBLOCKEDUSERS, null));
            blockedUsers = (ArrayList<String>) objectInputStream.readObject();
            int i = 1;
            for (String x : blockedUsers) {
                System.out.println(i + ") " + x);
                i++;
            }
            System.out.println(i + ") Back");
            // now we choose friend
            int choice;
            while (true) {
                try {
                    System.out.print("Please choose index :\n>>");
                    choice = Integer.parseInt(scanner.nextLine());
                    String blockedUsername = blockedUsers.get(i - 1);
                    if (choice == i) {
                        return;
                    }else if(choice>= 1 && choice<i){
                        System.out.println("1) Unblock");
                        System.out.println("2) Unblock & Send friend request");
                        System.out.print("3) Back\n>>");
                        while (true) {
                            try {
                                int choice1 = Integer.parseInt(scanner.nextLine());
                                if(choice1 == 3){
                                    break;
                                }else if(choice1 == 2){
                                    objectOutputStream.writeObject(new Request(RequestType.UNBLOCKANDSENDFRIENDREQUESTTOUSER,blockedUsername));
                                    break;
                                }else if(choice1 == 1){
                                    objectOutputStream.writeObject(new Request(RequestType.UNBLOCKUSER,blockedUsername));
                                    break;
                                }else
                                    throw new Exception();
                            }catch (Exception e){
                                System.out.println("Invalid input!!!");
                            }
                        }
                        break;
                    }else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input!!!");
                }
            }
        }catch (Exception e){
            System.out.println("Invalid input");
        }

    }
    /**
     * is responsible for previewing and managing a part of managing servers.
     * admins or general admin can add channel to a server.
     * at first, it receives a name of channel & add friends to channel.
     * then it handles events that user chooses it.
     * it has not parameter
     */
    public void setting() {
        while (true) {
            try {
                objectOutputStream.writeObject(new Request(RequestType.GETCURRENTSTATUS, username));
                String currentStatus = (String) objectInputStream.readObject();
                objectOutputStream.writeObject(new Request(RequestType.GETCURRENTEMAIL, null));
                String currentEmail = (String) objectInputStream.readObject();
                objectOutputStream.writeObject(new Request(RequestType.GETCURRENTPHONENUMBER,null));
                String currentPhoneNumber = (String) objectInputStream.readObject();
                System.out.println("1) Set Status (your current status : "+ currentStatus + " )");
                System.out.println("2) Change password " );
                System.out.println("3) Change email (your current email : "+ currentEmail+" )");
                System.out.println("4) Change phone number (your current phone number :"+ currentPhoneNumber+" )");
                System.out.print("5) Back\n>> ");
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 1) {
                    while (true) {
                        int choice1;
                        System.out.println("Your current status : " + currentStatus);
                        System.out.println("Please choose your status");
                        System.out.println("1) Online");
                        System.out.println("2) Idle");
                        System.out.println("3) Invisible");
                        System.out.println("4) Do not Disturb");
                        System.out.println("5) Back");
                        try {
                            choice1 = Integer.parseInt(scanner.nextLine());
                            if (choice1 == 1) {
                                objectOutputStream.writeObject(new Request(RequestType.SETNEWSTATUS, UserStatus.ONLINE));
                                boolean isSuccessful = (boolean) objectInputStream.readObject();
                                if (isSuccessful) {
                                    System.out.println(">> Status changed successfully");
                                } else {
                                    System.out.println(">> Operation failed!!");
                                }
                                break;
                            } else if (choice1 == 2) {
                                objectOutputStream.writeObject(new Request(RequestType.SETNEWSTATUS, UserStatus.IDLE));
                                boolean isSuccessful = (boolean) objectInputStream.readObject();
                                if (isSuccessful) {
                                    System.out.println(">> Status changed successfully");
                                } else {
                                    System.out.println(">> Operation failed!!");
                                }
                                break;
                            } else if (choice1 == 3) {
                                objectOutputStream.writeObject(new Request(RequestType.SETNEWSTATUS, UserStatus.INVISIBLE));
                                boolean isSuccessful = (boolean) objectInputStream.readObject();
                                if (isSuccessful) {
                                    System.out.println(">> Status changed successfully");
                                } else {
                                    System.out.println(">> Operation failed!!");
                                }
                                break;
                            } else if (choice1 == 4) {
                                objectOutputStream.writeObject(new Request(RequestType.SETNEWSTATUS, UserStatus.DONOTDISTURB));
                                boolean isSuccessful = (boolean) objectInputStream.readObject();
                                if (isSuccessful) {
                                    System.out.println(">> Status changed successfully");
                                } else {
                                    System.out.println(">> Operation failed!!");
                                }
                                break;
                            } else if (choice1 == 5) {
                                break;
                            } else {
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid input!!! try again");
                        }
                    }
                } else if (choice == 2) {
                    String newPassword;
                    String currentPassword;
                    while (true) {
                        try {
                            System.out.println("please enter your current password :");
                            currentPassword = scanner.nextLine();
                            objectOutputStream.writeObject(new Request(RequestType.CHECKCURRENTPASSWORD, currentPassword));
                            boolean isValid = (boolean) objectInputStream.readObject();
                            if (isValid) {
                                System.out.println("please enter your password or type /back to get back : ");
                                System.out.print("[Caution]>> password must be at least 8 characters and contain [0-9] , [a-z] & [A-Z].\n>> ");
                                newPassword = scanner.nextLine();
                                if(newPassword.equals("/back")){
                                    break;
                                }else if (!(Pattern.matches("[a-z[A-z[0-9]]]", newPassword) || newPassword.length() >= 8)) { // maybe it is wrong
                                    throw new Exception();
                                } else {
                                    objectOutputStream.writeObject(new Request(RequestType.SETNEWPASSWORD, newPassword));
                                    boolean isSuccessful = (boolean) objectInputStream.readObject();
                                    if (isSuccessful) {
                                        System.out.println(">> Password changed successfully");
                                    } else {
                                        System.out.println(">> Operation failed!!");
                                    }
                                }
                            } else {
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            System.out.println("invalid input!!! try again");
                            continue;
                        }
                        break;
                    }
                } else if (choice == 3) {
                    String newEmail;
                    while (true) {
                        try {
                            System.out.println("please enter your new email or type /back to get back:");
                            System.out.print("[caution]>> please enter complete format [abcdef@newEmail.com]\n>> ");
                            newEmail = scanner.nextLine();
                            if(newEmail.equals("/back")){
                                break;
                            }else if (!(newEmail.contains("@gmail.com")) && !(newEmail.contains("@email.com"))) {
                                throw new Exception();
                            } else {
                                objectOutputStream.writeObject(new Request(RequestType.SETNEWEMAIL, newEmail));
                                boolean isSuccessful = (boolean) objectInputStream.readObject();
                                if (isSuccessful) {
                                    System.out.println(">> Email changed successfully");
                                } else {
                                    System.out.println(">> Operation failed!!");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("invalid input!!! try again");
                            continue;
                        }
                        break;
                    }
                } else if (choice == 4) {
                    String newPhoneNumber;
                    while (true) {
                        try {
                            System.out.print("please enter your new phone number or type /back to get back\n>> ");
                            newPhoneNumber = scanner.nextLine();
                            if(newPhoneNumber.equals("/back")){
                                break;
                            }else if (newPhoneNumber.compareTo("-") == 0 || newPhoneNumber.length() > 11) {    // CORRECT HERE
                                throw new Exception();
                            } else {
                                objectOutputStream.writeObject(new Request(RequestType.SETNEWPHONENUMBER, newPhoneNumber));
                                boolean isSuccessful = (boolean) objectInputStream.readObject();
                                if (isSuccessful) {
                                    System.out.println(">> Phone number changed successfully");
                                } else {
                                    System.out.println(">> Operation failed!!");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("invalid input!!! try again");
                            continue;
                        }
                        break;
                    }
                } else if (choice == 5) {
                    break;
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                System.out.println("Invalid input!!!");
            }
        }
    }

}
