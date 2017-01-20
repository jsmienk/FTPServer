/**
 * Author: Jeroen
 * Date created: 08-12-16
 */
class Command {

    // authentication
    final static String AUTH = "AUTH";
    final static String TLS = "TLS";
    final static String SSL = "SSL";

    // user related
    final static String USER = "USER";
    final static String PASS = "PASS";

    // working directories
    final static String PWD = "PWD"; // print
    final static String CWD = "CWD"; // change
    final static String RWD = "RWD"; // remove
    final static String LIST = "LIST";
    final static String MKD = "MKD";

    final static String SYST = "SYST";
    final static String TYPE = "TYPE";
    final static String PORT = "PORT";
    final static String STOR = "STOR";
    final static String RETR = "RETR";

    // transferring
    final static String PASV = "PASV";
}
