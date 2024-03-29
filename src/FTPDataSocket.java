import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

/**
 * Author: Sven Ordelman
 * Date created: 19-1-2017
 */
class FTPDataSocket {

    private OutputStream to_client_data;
//    private InputStream from_client_data;

    FTPDataSocket(Socket s) throws IOException {
//        from_client_data = s.getInputStream();
        to_client_data = s.getOutputStream();
    }

    //File to bytes
    private static byte[] loadFile(File file) throws IOException {

        long length = file.length();
//        if (length > Integer.MAX_VALUE) {
//            // File is too large
//        }
        InputStream is = new FileInputStream(file);

        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    void sendFile(String filePath) throws IOException {
        // send file back to client
        sendResponse("-rwxrwxrwx 1 Jeroen Jeroen             0 Jan  1  1970 " + filePath + "\n");
    }

    void sendFolder(String path) throws IOException {
        // Send folder back to client
        sendResponse("drwxrwxrwx 1 Jeroen Jeroen             0 Jan  1  1970 " + path + "\n");
    }

    void sendBackFile(String file, FileDirectory directory, CommandProcessor processor, ClientThread clientThread) throws IOException {
        // Send folder back to client
        if (processor.getType() == CommandProcessor.TransferType.I) {
            File file1 = new File(Server.USERS_FILEPATH + directory.getPath() + "/" + file);
            String content = new Scanner(file1).useDelimiter("\\Z").next();
            sendResponse(content);
        } else if (processor.getType() == CommandProcessor.TransferType.A) {
            File file1 = new File(Server.USERS_FILEPATH + directory.getPath() + "/" + file);
            byte[] bytes = loadFile(file1);
            sendResponse(Base64.getEncoder().encodeToString(bytes));
        }
        processor.closeSockets(clientThread);
    }

    private void sendResponse(String string) throws IOException {
        to_client_data.write(string.getBytes());
        to_client_data.flush();
    }

    void saveFile(String file, FileDirectory directory, CommandProcessor processor, ClientThread clientThread) throws IOException {

        /*File f = new File(Server.USERS_FILEPATH + directory.getPath() + "/" + file);

        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(from_client_data));
        String clientData = "";

        clientData = reader.readLine();
        FileWriter fooWriter = null; // true to append
        try {
            fooWriter = new FileWriter(f);
            while (clientData != null) {
                clientData = reader.readLine();
                System.out.println(clientData);
            }
            fooWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
