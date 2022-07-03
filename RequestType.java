/**
 * this class requests to server.
 * it has more than 80 requests and server handles this request.
 *
 * @author Mehdi Mohammadi
 * @author Hossein Asadi
 * @version 1.0
 * @since 7/2022
 */
public enum RequestType {
    GLOBALSEARCHFORAUSERNAME,
    SIGNUPUSER,
    LOGOUT,
    CHECKIFUSERNAMEISOKATALL,
    GLOBALSEARCHFORSERVERNAME,
    TODOWITHAUSEROFRESULTOFGLOBALSEARCH,
    NUMBEROFSENTANDRECEIVEDFRIENDSHIPREQUESTS,
    SENDFRIENDSHIPREQUEST,
    UNFRIEND,
    LISTOFSENTFRIENDREQUESTS,
    REMOVESENTFRIENDSHIPREQUEST,
    // additional part1
    SETNEWSTATUS, // server sets new status
    SETNEWPASSWORD, // server sets new password
    SETNEWEMAIL,  // server sets new email
    SETNEWPHONENUMBER, // server sets new phone number
    CHECKCURRENTPASSWORD, //server checks that password is ok
    GETCURRENTSTATUS, // user wants current status
    // part2
    LISTOFNEWFRIENDSFORSTARTANEWCHAT,  // list of friends who have not sent a message
    ENDOFCHAT,  // user wants to end chat
    SENDAMESSAGE,  // user wants to send a message
    NUMBEROFUNREADCHATS,  // number of chats that they are unread
    LISTOFBLOCKEDUSERS, // list of blocked users
    MESSAGESLIST,  // list of all messages that they are in a chat
    NUMBEROFUNREADMESSAGESOFEACHCHAT, // list of unread message that they are in a chat
    LISTOFFRIENDS, // list of all friends
    BLOCKANDUNFRIENDUSER, // user wants to unfriend and block a friend
    UNFRIENDUSER, // user wants to unfriend a friend
    LISTOFFRIENDSFORPRIVATECHAT,  // list of friends who they sent message and user did not see a messages
    CHECKIFEMAILISOKATALL, // server checks ,is email obeys the rules???
    GETCURRENTEMAIL, // user wants current email
    GETCURRENTPHONENUMBER, // user wants current phone number
    UNBLOCKUSER, // user wants to unblock another user
    UNBLOCKANDSENDFRIENDREQUESTTOUSER, // user wants to unblock and unfriend another user
    // PART3
    // server stuff
    ISOKSERVERNAME,  // check that is ok to create a server then return boolean
    GETROLEOFUSERINTHISSERVER,  // return role of user in this server 1- GeneralAdmin 2- ChannelAdmin 3- User
    LISTOFCHANNELSOFSERVER,   // List of channels that are in a server
    LISTOFSERVERS,  // List of all servers that we created or join these servers
    GETADMINACCESS, // return a string that has 8 characters include 1 & 0 for instance 11010001
    ISOKTOCREATETEXTCHANNEL, // check is ok to create a channel in server then return a boolean
    ISOKTOCREATEVOICECHANNEL,
    DELETECHANNEL, // delete channel from server
    ADDUSERTOSERVER, // add user to server
    ADDUSERTOGENERALCHANNEL,
    LISTOFSERVERUSERS ,// list of all ussers that are in a server
    DELETEUSERFROMSERVER,  // Delete user from server
    SETNEWNAMEFORSERVER, // set new name for server
    HISTORYOFCHANNEL, // return information about channel like count of messages , time of channel creation
    // ...
    REJECTFRIENDSHIPREQUEST,  // reject friendship request
    ACCEPTFRIENDSHIPREQUEST, // accept friendship request
    JUSTBLOCKEDUSER, // just block user and do not unfriend
    LISTOFRECEIVEDFRIENDREQUESTS,
    USERLEFTCHATPAGE,// list of all requests that received
    INTOCHAT,
    // new
    LEFTCHANNEL,
    PINNEDMESSAGE,
    UNPINNEDMESSAGE,
    LEAVEFROMSERVER,
    LISTOFCHANNELSOFSERVERTHATUSERJOINEDIN,
    SETADMINACESSFORADDINGCHANNEL,
    SETADMINACESSFORREMOVINGCHANNEL,
    SETADMINACESSFORREMOVINGUSERFROMCHANNEL,
    SETADMINACESSFORBANNINGUSERFROMSERVER,
    SETADMINACESSFORLIMMITINGUSERFROMCHANNEL,
    SETADMINACESSFORCHANGINGSERVERNAME,
    SETADMINACESSFORSEEHISTORYOFCHAT,
    SETADMINACESSFORPINNINGMESSAGE,
    LISTOFCHANNELSTHATUSERJOINEDINASERVER,
    RESTRICTEDUSERFROMCHANNEL,
    UNRESTRICTEDUSERFROMCHANNEL,
    UNBANUSERFROMCHANNEL,
    BANUSERFROMCHANNEL;
}