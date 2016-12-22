import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Author: Jeroen
 * Date created: 08-12-16
 */
class ClientThread extends Thread {

    private final Socket clientSocket;
    private final PrintWriter writer;

    private final CommandProcessor processor;

    ClientThread(Socket clientSocket, Server server) throws IOException {
        assert clientSocket != null : "null socket";
        assert server != null : "null server";

        this.clientSocket = clientSocket;
        writer = new PrintWriter(clientSocket.getOutputStream());
        processor = new CommandProcessor(server);
    }

    @Override
    public void run() {
        String usernameToLogin = null;

        // send a welcome message
        send(Code.SERVICE_READY_FOR_NEW_USER + " Sven & Jeroen's FTP Server " + Code.CR);

        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String clientData = "";
            int lineCount = -1;
            while (true) {
                lineCount++;

                // get a message from the client
                clientData = reader.readLine();
                if (clientData == null) break;

                // print the message
                System.out.println(lineCount + ": " + clientData);

                final String[] commands = clientData.split(" ");

                send(processor.processCommand(commands));
            }

            clientSocket.close();
            System.out.println("Client disconnected.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void send(final String s) {
        System.out.println("\t\t\t\t\t\t<--\t\t" + s);
        writer.println(s);
        writer.flush();
    }
}