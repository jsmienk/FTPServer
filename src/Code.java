/**
 * Author: Jeroen
 * Date created: 08-12-16
 */
class Code {

    // end of line codes
    static final String CR = "<CR>";
    static final String LF = "<LF>";
    static final String NL = "<NL>";

    // space
    static final String SP = "<SP>";

    // 200+
    static final int ACTION_SUCCESSFUL = 200;
    static final int NAME_SYSTEM_TYPE = 215;
    static final int SERVICE_READY_FOR_NEW_USER = 220;
    static final int ENTERING_PASSIVE_MODE = 227;
    static final int USER_LOGGED_IN = 230;
    static final int REQUESTED_FILE_ACTION_OKAY = 250;
    static final int PATHNAME_CREATED = 257;

    // 300+
    static final int NEED_ACCOUNT_FOR_LOGIN = 332;

    // 500+
    static final int SYNTAX_ERROR_IN_PARAMETERS = 501;
    static final int CODE_NOT_IMPLEMENTED = 502;
    static final int CODE_NOT_IMPLEMENTED_FOR_PARAMETER = 504;
    static final int NOT_LOGGED_IN = 530;
}
