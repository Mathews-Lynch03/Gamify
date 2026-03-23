package client;

public class GamifyServiceDetails {
    // Connection
    public static final int    LISTENING_PORT     = 7777;

    // Command keywords (sent to server)
    public static final String END_SESSION        = "END";
    public static final String REGISTER_USER      = "USER";
    public static final String ORDER              = "ORDER";
    public static final String CANCEL             = "CANCEL";
    public static final String VIEW               = "VIEW";

    // Order sides
    public static final String BUY_SIDE           = "B";
    public static final String SELL_SIDE          = "S";

    // Separator used to build command strings  e.g. "USER:Seb"
    public static final String COMMAND_SEPARATOR  = ":";

    // Separator used inside orders  e.g. "B,Minecraft,25.00"
    public static final String FIELD_SEPARATOR    = ",";

    // Responses from server
    public static final String CONNECTED          = "CONNECTED";
    public static final String SESSION_TERMINATED = "ENDED";
    public static final String MATCH              = "MATCH";
    public static final String CANCELLED          = "CANCELLED";
    public static final String NOT_FOUND          = "NOT_FOUND";
}
