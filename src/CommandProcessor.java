import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * Author: Jeroen
 * Date created: 22-12-16
 */
class CommandProcessor {

    private final Server server;

    private FileDirectory directory;

    private State state;

    private String usernameToLogin;

    CommandProcessor(Server server) {
        this.server = server;
        state = State.NO_AUTH;
        usernameToLogin = null;
    }

    String processCommand(String[] commands) {

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
                // TODO: PWD
                if (commands.length > 0 && commands[0].equals(Command.PWD))
                    return (Code.PATHNAME_CREATED + " " + directory.getDirectoryList() + " is home directory " + Code.CR);


                // TODO: CWD
                if (commands.length > 0 && commands[0].equals(Command.CWD))
                    return (Code.REQUESTED_FILE_ACTION_OKAY + " " + directory.getDirectoryList() + " " + Code.CR);


                // TODO: RWD
                if (commands.length > 0 && commands[0].equals(Command.RWD))
                    return (Code.REQUESTED_FILE_ACTION_OKAY + " " + directory.getDirectoryList() + " " + Code.CR);


                // SYST
                if (commands.length > 0 && commands[0].equals(Command.SYST))
                    return (Code.NAME_SYSTEM_TYPE + " UNIX Type: L8 " + Code.CR);


                // TYPE I
                if (commands.length > 1 && commands[0].equals(Command.TYPE)) {
                    if (commands[1].equals("I"))
                        return (Code.ACTION_SUCCESSFUL + " Type set to I " + Code.CR);
                    return (Code.SYNTAX_ERROR_IN_PARAMETERS + Code.CR);
                }

                // EPSV
                if (commands.length > 0 && commands[0].equals(Command.EPSV))
                    return (Code.ENTERING_PASSIVE_MODE + " "
                            + "(127,0,0,1,2100,6969)"
                            + " " + Code.CR);

                // LIST
                if (commands.length > 0 && commands[0].equals(Command.LIST))
                    return Code.ACTION_SUCCESSFUL + " " + Code.NL + " " + directory.getList() + " " + Code.CR;

                return (Code.CODE_NOT_IMPLEMENTED + Code.CR);

            case TRANSFERRING:
                return (Code.CODE_NOT_IMPLEMENTED + Code.CR);
        }

        return (Code.CODE_NOT_IMPLEMENTED + Code.CR);
    }

    private enum State {
        NO_AUTH,
        AUTH,
        TRANSFERRING
    }

    private enum IPVersion {
        IPv4,
        IPv6
    }
}
