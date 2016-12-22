/**
 * Author: Jeroen
 * Date created: 22-12-16
 */
class CommandProcessor {

    private final Server server;

    private User user;
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
                        user = server.loginUser(usernameToLogin, password);
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

                return (Code.CODE_NOT_IMPLEMENTED + Code.CR);

            case AUTH:
                // TODO: PWD
                if (commands.length > 0 && commands[0].equals(Command.PWD))
                    return (Code.PATHNAME_CREATED + " " + directory.getDirectoryList() + " " + Code.CR);

                // TODO: CWD
                if (commands.length > 0 && commands[0].equals(Command.CWD))
                    return (Code.PATHNAME_CREATED + " " + directory.getDirectoryList() + " " + Code.CR);

                // TODO: RWD
                if (commands.length > 0 && commands[0].equals(Command.RWD))
                    return (Code.PATHNAME_CREATED + " " + directory.getDirectoryList() + " " + Code.CR);

                // TODO: SYST
                if (commands.length > 0 && commands[0].equals(Command.SYST))
                    return (Code.NAME_SYSTEM_TYPE + " UNIX Type: L8 " + Code.CR);

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
}
