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

                // AUTH
                if (commands.length > 0 && commands[0].equals(Command.AUTH)) {
                    if (commands.length > 1 && commands[1].equals(Command.TLS)) {
                        send(Code.CODE_NOT_IMPLEMENTED_FOR_PARAMETER + Code.CR);
                        continue;
                    }

                    if (commands.length > 1 && commands[1].equals(Command.SSL)) {
                        send(Code.CODE_NOT_IMPLEMENTED_FOR_PARAMETER + Code.CR);
                        continue;
                    }

                    send(Code.SYNTAX_ERROR_IN_PARAMETERS + Code.CR);
                    continue;
                }

                // USER
                if (commands.length > 0 && commands[0].equals(Command.USER)) {
                    if (commands.length > 1) {
                        System.out.println("\tUsername: " + commands[1]);
                        send(Code.NEED_ACCOUNT_FOR_LOGIN + Code.CR);
                        continue;
                    }

                    send(Code.SYNTAX_ERROR_IN_PARAMETERS + Code.CR);
                    continue;
                }

                // USER
                if (commands.length > 0 && commands[0].equals(Command.PASS)) {
                    if (commands.length > 1) {
                        System.out.println("\tPassword: " + commands[1]);
                        send(Code.USER_LOGGED_IN + Code.CR);
                        continue;
                    }

                    send(Code.SYNTAX_ERROR_IN_PARAMETERS + Code.CR);
                    continue;
                }

                // TODO: PWD
                if (commands.length > 0 && commands[0].equals(Command.PWD)) {
                    send(Code.PATHNAME_CREATED + " / " + Code.CR);
                    continue;
                }

                // TODO: CWD
                if (commands.length > 0 && commands[0].equals(Command.CWD)) {
                    send(Code.PATHNAME_CREATED + " / " + Code.CR);
                    continue;
                }

                // TODO: RWD
                if (commands.length > 0 && commands[0].equals(Command.RWD)) {
                    send(Code.PATHNAME_CREATED + " / " + Code.CR);
                    continue;
                }

                // TODO: SYST
                if (commands.length > 0 && commands[0].equals(Command.SYST)) {
                    send(Code.NAME_SYSTEM_TYPE + " UNIX " + Code.CR);
                    continue;
                }

                send(Code.CODE_NOT_IMPLEMENTED + Code.CR);
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