import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Author: Jeroen
 * Date created: 08-12-16
 */
public class Server {

    static final String USERS_FILEPATH = "users/";

    private static final int SERVER_PORT = 2100;

    private static final String USERS_FILE_PATH = "users/users";

    private final Map<String, User> users;

    private Server() {
        this.users = new HashMap<>();

        // try to read the users file
        try {
            // open a scanner
            final Scanner scanner = new Scanner(new File(USERS_FILE_PATH));
            while (scanner.hasNextLine()) {
                // get a single line
                final String line = scanner.nextLine().trim();
                // if it is a comment, check next line
                if (line.length() == 0 || line.charAt(0) == '#') continue;

                // split the data
                final String[] user = line.split(";");

                // if a username and password was found on the line
                if (user.length > 1) {
                    final String username = user[0];
                    final String password = user[1];
                    users.put(username.toLowerCase(), new User(username, password,
                            (user.length > 2 ? user[3] : "/" + username)));
                }
            }

            // close it
            scanner.close();
        } catch (FileNotFoundException fnfe) {
            System.err.println("Users file was not found.");

            // try to create an empty users file
            try {
                final PrintWriter writer = new PrintWriter(new File(USERS_FILE_PATH));
                writer.println("# -- users file --");
                writer.println("# comments are preceded by a pound sign");
                writer.println("# username and password are divided by a semi colon");
                writer.println("# a single user is on a single line");

                // close it
                writer.close();
                System.err.println("Empty users file created.");
            } catch (FileNotFoundException ignored) {
            }
        }
    }

    /**
     * Inloggen,
     * browsen door directories,
     * down- en uploaden van files via passive mode.
     */
    public static void main(String[] args) {
        new Server().run();
    }

    User loginUser(String username, String password) {
        assert username != null : "null username";
        assert !username.isEmpty() : "empty username";
        assert password != null : "null password";
        assert !password.isEmpty() : "empty password";

        final User possibleUser = users.get(username.toLowerCase());
        if (possibleUser != null && possibleUser.isUser(password))
            return possibleUser;
        return null;
    }

    private void run() {

        try {
            // open the socket on the port
            final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            // success!
            System.out.println("FTP Server started on port: " + SERVER_PORT);

            //noinspection InfiniteLoopStatement
            while (true) {
                // wait for a client to connect
                final Socket clientSocket = serverSocket.accept();

                // create a new client thread and start it
                new ClientThread(clientSocket, this).start();
                System.out.println("Client connected.");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
