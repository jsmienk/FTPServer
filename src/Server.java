import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Author: Jeroen
 * Date created: 08-12-16
 */
public class Server {

    private static final int SERVER_PORT = 2100;

    /*
     * Inloggen,
     * browsen door directories,
     * down- en uploaden van files via passive mode.
     */
    public static void main(String[] args) {
        new Server().run();
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
                new ClientThread(clientSocket).start();
                System.out.println("Client connected.");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
