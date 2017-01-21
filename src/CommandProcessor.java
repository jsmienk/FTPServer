import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Author: Jeroen
 * Date created: 22-12-16
 */
class CommandProcessor {

    private static final int port1 = 125;
    private static final int port2 = 63;
    private final Server server;
    private FileDirectory directory;
    private State state;
    private String usernameToLogin;
    private String type;

    private Socket dataSocket = null;
    private FTPDataSocket fileDataSocket = null;
    private ServerSocket serverSocket = null;

    CommandProcessor(Server server) {
        this.server = server;
        state = State.NO_AUTH;
        usernameToLogin = null;
    }

    String processCommand(String[] commands, ClientThread clientThread) throws IOException, InterruptedException {
        // what can we do, depending on our state
        switch (state) {
            // when we are not authenticated
            case NO_AUTH:
                // AUTH
                if (commands.length > 0 && commands[0].equals(Command.AUTH)) {
                    if (commands.length > 1 && commands[1].equals(Command.TLS))
                        return (Code.CODE_NOT_IMPLEMENTED_FOR_PARAMETER + Code.CR);

                    if (commands.length > 1 && commands[1].equals(Command.SSL))
                        return (Code.CODE_NOT_IMPLEMENTED_FOR_PARAMETER + Code.CR);

                    return (Code.SYNTAX_ERROR_IN_PARAMETERS + Code.CR);
                }

                // USER
                if (commands.length > 0 && commands[0].equals(Command.USER)) {
                    if (commands.length > 1) {
                        usernameToLogin = commands[1];
                        System.out.println("\tUsername: " + usernameToLogin);
                        return (Code.NEED_ACCOUNT_FOR_LOGIN + Code.CR);
                    }

                    return (Code.SYNTAX_ERROR_IN_PARAMETERS + Code.CR);
                }

                // USER
                if (commands.length > 0 && commands[0].equals(Command.PASS)) {
                    if (commands.length > 1) {
                        // try to login the user
                        final String password = commands[1];
                        User user = server.loginUser(usernameToLogin, password);
                        System.out.println("\tPassword: " + password);

                        // failed to login
                        if (user == null) {
                            System.err.println(usernameToLogin + " failed to log in.");
                            return (Code.NOT_LOGGED_IN + Code.CR);
                        }

                        // logged in!
                        directory = new FileDirectory(user.getRootDirectory());
                        state = State.AUTH;
                        return (Code.USER_LOGGED_IN + Code.CR);
                    }

                    return (Code.SYNTAX_ERROR_IN_PARAMETERS + Code.CR);
                }

                // if it is another command, it is not allowed
                return (Code.NOT_LOGGED_IN + Code.CR);

            case AUTH:
                if (commands.length > 0 && commands[0].equals(Command.PWD)) {
                    if (directory.getStack().size() == 0)
                        directory.addDirectory(String.valueOf(directory.getRoot()));
                    return (Code.PATHNAME_CREATED + " " + directory.getPath());

                }


                if (commands.length > 0 && commands[0].equals(Command.CWD)) {
                    if (commands[1] != null) {
                        if (commands[1].equals("..")) {
                            if (directory.getStack().size() > 0) {
                                directory.goBackDirectory();
                            }
                            return (Code.REQUESTED_FILE_ACTION_OKAY + "/" + directory.getStack().peek() + Code.CR);
                        } else {
                            String[] string = commands[1].split("/");
                            if (!directory.getStack().get(0).equals("/" + string[string.length - 1])) {
                                directory.addDirectory("/" + string[string.length - 1]);
                            }
                            return (Code.REQUESTED_FILE_ACTION_OKAY + "/" + directory.getStack().peek() + Code.CR);
                        }
                    }
                    if (directory.getStack().size() > 0)
                        return (Code.REQUESTED_FILE_ACTION_OKAY + " " + directory.getPath() + " " + Code.CR);
                    else
                        return (Code.REQUESTED_FILE_ACTION_OKAY + " " + directory.getRoot() + " " + Code.CR);

                }


                // TODO: RWD
                if (commands.length > 0 && commands[0].equals(Command.RWD))
                    return (Code.REQUESTED_FILE_ACTION_OKAY + " " + directory.getDirectoryList() + " " + Code.CR);


                // SYST
                if (commands.length > 0 && commands[0].equals(Command.SYST))
                    return (Code.NAME_SYSTEM_TYPE + " UNIX Type: L8 " + Code.CR);


                // TYPE
                if (commands.length > 1 && commands[0].equals(Command.TYPE)) {
                    if (commands[1].equals("I")) {
                        type = "I";
                        return (Code.ACTION_SUCCESSFUL + " Type set to I " + Code.CR);
                    }
                    if (commands[1].equals("A")) {
                        type = "A";
                        return (Code.ACTION_SUCCESSFUL + " Type set to A " + Code.CR);
                    }
                    return (Code.SYNTAX_ERROR_IN_PARAMETERS + Code.CR);
                }

                // PASV
                if (commands.length > 0 && commands[0].equals(Command.PASV)) {
                    //Say that we entered passive mode on certain port.
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                    if (dataSocket != null) {
                        dataSocket.close();
                    }
                    clientThread.send((Code.ENTERING_PASSIVE_MODE + " "
                            + "(127,0,0,1," + port1 + "," + port2 + ")"
                            + " " + Code.CR));
                    //Create a server socket on that port
                    serverSocket = new ServerSocket(port1 * 256 + port2);
                    dataSocket = serverSocket.accept();
                    //Make our socket for sending the data to the client
                    fileDataSocket = new FTPDataSocket(dataSocket);
                    //Return
                    return "";
                }

                // LIST
                if (commands.length > 0 && commands[0].equals(Command.LIST)) {
                    //Say that we opened a BINARY mode data connection for sending the data
                    clientThread.send("150 Opening BINARY mode data connection.\\n");
                    //Send the filelist for the map whe wanted
                    File folder = new File(Server.USERS_FILEPATH + directory.getPath());
                    File[] listOfFiles = folder.listFiles();

                    if (listOfFiles != null) {
                        for (File listOfFile : listOfFiles) {
                            if (listOfFile.isFile()) {
                                System.out.println("File " + listOfFile.getName());
                                fileDataSocket.sendFile(listOfFile.getName());
                            } else if (listOfFile.isDirectory()) {
                                System.out.println("Directory " + listOfFile.getName());
                                fileDataSocket.sendFolder(listOfFile.getName());
                            }
                        }
                    }

                    //Sleep before we send anything
                    Thread.sleep(500);
                    //Close socket so client knows we're done
                    dataSocket.close();
                    serverSocket.close();
                    //Say that we completed the transfer
                    return ("260 Transfer complete.\n");
                }

                if (commands.length > 0 && commands[0].equals(Command.RETR)) {
                    if (commands[1] != null) {
                        fileDataSocket.sendBackFile(commands[1], directory, this, clientThread);
                    }
                    return "";
                }

                if (commands.length > 0 && commands[0].equals(Command.STOR)) {
                    if (commands[1] != null) {
                        fileDataSocket.saveFile(commands[1], directory, this, clientThread);
                    }
                    return "";
                }

                if (commands.length > 0 && commands[0].equals(Command.MKD)) {
                    if (commands[1] != null) {
                        directory.createDirectory(new File(Server.USERS_FILEPATH + directory.getPath() + "/" + commands[1]));
                    }
                    return Code.PATHNAME_CREATED + " " + commands[1];
                }


                return (Code.CODE_NOT_IMPLEMENTED + Code.CR);

            case TRANSFERRING:
                return (Code.CODE_NOT_IMPLEMENTED + Code.CR);
        }

        return (Code.CODE_NOT_IMPLEMENTED + Code.CR);
    }

    String getType() {
        return type;
    }

    void closeSockets(ClientThread thread) throws IOException {
        serverSocket.close();
        dataSocket.close();
        thread.send("260 Transfer complete.\n");
    }

    private enum State {
        NO_AUTH,
        AUTH,
        TRANSFERRING
    }
}
