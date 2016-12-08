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

    ClientThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        writer = new PrintWriter(clientSocket.getOutputStream());
    }

    @Override
    public void run() {

        // send a welcome message
        send("220<CR>");

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

                // AUTH
                if (commands.length > 0 && commands[0].equals("AUTH")) {
                    if (commands.length > 1 && commands[1].equals("TLS")) {
                        send("332<CR>");
                        continue;
                    }

                    send("501<CR>");
                    continue;
                }

                send("502<CR>");
            }

            clientSocket.close();
            System.out.println("Client disconnected.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void send(final String s) {
        writer.println(s);
        writer.flush();
    }
}