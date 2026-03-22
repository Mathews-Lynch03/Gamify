package Java.common;

public class Protocol {
    private Protocol() {}

    //Requests
    public static final String REQ_USER   = "USER";
    public static final String REQ_ORDER  = "ORDER";
    public static final String REQ_CANCEL = "CANCEL";
    public static final String REQ_VIEW   = "VIEW";
    public static final String REQ_END    = "END";

    // Responses
    public static final String RES_CONNECTED   = "CONNECTED";
    public static final String RES_MATCH       = "MATCH";
    public static final String RES_CANCELLED   = "CANCELLED";
    public static final String RES_NOT_FOUND   = "NOT_FOUND";
    public static final String RES_ENDED       = "ENDED";

    // Special multi-line block delimiter
    public static final String BOOK_START      = "ORDER_BOOK_START";
    public static final String BOOK_END        = "ORDER_BOOK_END";

    // Error
    public static final String RES_ERROR       = "ERROR";
}
