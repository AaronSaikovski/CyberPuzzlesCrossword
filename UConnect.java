import java.applet.Applet;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.net.Socket;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;

// -- DataBase connectivity object
public class UConnect{

    private pulse ptrParent = null;
    private String m_szHost = null;
    private Socket m_sckt = null;
    private DataOutputStream m_out = null;
    private DataInputStream m_in = null;

    private final static int HTTP_PORT = 80;

    private static final String szGET = "GET ";
    private static final String szHTTPTAIL = " HTTP/1.0\r\n\r\n";

    // -- constructor
    public UConnect(pulse obj) {
        if (obj.getDebugMode()) System.out.println("UConnect constructor");
        ptrParent = obj;
    }

    // -- create db bound socket and associated streams
    public boolean dbInit() {
        if (ptrParent.getDebugMode()) System.out.println("UConnect dbInit()");
        if (!setHostAddress())
            return false;

        if (!createNewSocket()) {
            dbExit();
            return false;
        }

        if (!createNewStreams()) {
            dbExit();
            return false;
        }

        return true;
    }

    // -- save the host
    private boolean setHostAddress() {
        if (ptrParent.getDebugMode()) System.out.println("UConnect setHostAddress()");
        m_szHost = "";
        m_szHost = ptrParent.getCodeBase().getHost();
        if (m_szHost.length() == 0) {
            if (ptrParent.getDebugMode()) System.out.println("length = 0");
            return false;
        }
        return true;
    }

    // -- create pipe
    private boolean createNewSocket() {
        if (ptrParent.getDebugMode()) System.out.println("UConnect createNewSocket()");

        if (m_szHost == null)
            return false;

        if (m_sckt != null) {
            try {
                m_sckt.close();
            } catch(IOException ioe) {
                System.out.println("IOException closing socket: " + ioe);
            }
        }
        m_sckt = null;

        try {
            m_sckt = new Socket(m_szHost, HTTP_PORT);
        } catch(IOException ioe) {
            System.out.println("IOException creating socket: " + ioe);
        }

        if (m_sckt == null) {
            if (ptrParent.getDebugMode()) System.out.println("socket = null");
            return false;
        }

        return true;
    }

    // -- create end points
    private boolean createNewStreams() {
        if (ptrParent.getDebugMode()) System.out.println("UConnect createNewStreams()");

        if (m_sckt == null)
            return false;

        if (m_out != null) {
            try {
            m_out.close();
            } catch(IOException ioe) {
                System.out.println("IOException closing out stream: " + ioe);
            }
        }
        if (m_in != null) {
            try {
            m_in.close();
            } catch(IOException ioe) {
                System.out.println("IOException closing in stream: " + ioe);
            }
        }

        m_out = null;
        m_in = null;

        try {
            m_out = new DataOutputStream(m_sckt.getOutputStream());
            m_in = new DataInputStream(m_sckt.getInputStream());
        } catch(IOException ioe) {
            System.out.println("IOException creating input and output streams: " + ioe);
        }
        if ((m_out == null) || (m_in == null)) {
            if (ptrParent.getDebugMode()) System.out.println("streams = null");
            return false;
        }

        return true;
    }

    // -- destroy db bound socket and associated streams
    public boolean dbExit() {
        if (ptrParent.getDebugMode()) System.out.println("UConnect dbExit()");

        try {
            if (m_sckt != null) {
                m_sckt.close();
                m_sckt = null;
            }

        } catch(IOException ioe) {
            System.out.println("IOException closing http socket: " + ioe);
        }

        try {
            if (m_out != null) {
                m_out.close();
                m_out = null;
            }
        } catch(IOException ioe) {
            System.out.println("IOException closing http output stream: " + ioe);
        }

        try {
            if (m_in != null) {
                m_in.close();
                m_in = null;
            }
        } catch(IOException ioe) {
            System.out.println("IOException closing http input stream: " + ioe);
        }

        return true;
    }

    // public void doSQL_Select() { }

    // public String doSQL_Select() { }

    public boolean doSQL_Select() {
        return true;
    }

    public void doSQL_Update() {
    }

    //
    // String nm, int prd
    //
    // "http://www.cyberpuzzles.aust.com/scripts/uconstruct.exe?proc=testproc7&company=cp&action=exec" +
    //                        "&@recidval=" + prd + "&@name=" + nm +
    //                        "&template=users2.txt"
    //
    // -- send SQL UPDATE and validate success
    public boolean doSQL_Update(String szSQLUpdate) {
        if (ptrParent.getDebugMode()) System.out.println("UConnect boolean doSQL_Update()");

        if (!dbInit()) {
            dbExit();
            return false;
        }

        if (!doSQLQuery(szSQLUpdate)) {
            dbExit();
            return false;
        }

        if (!handleSQLQueryResponse()) {
            dbExit();
            return false;
        }

        if (!dbExit())
            return false;

        return true;
    }

    public String doszSQL_Update(String szSQLUpdate) {
        if (ptrParent.getDebugMode()) System.out.println("UConnect boolean doszSQL_Update()");

        if (!dbInit()) {
            dbExit();
            return "init failed";
        }

        if (!doSQLQuery(szSQLUpdate)) {
            dbExit();
            return "query failed";
        }

        String szQueryResponse = null;
        if ((szQueryResponse = handleszSQLQueryResponse()).equals("")) {
            dbExit();
            return "null";
        }

        if (!dbExit())
            return "cleanup failed";

        return szQueryResponse;
    }

    private boolean doSQLQuery(String szQuery) {
        if (ptrParent.getDebugMode()) System.out.println("UConnect doSQLQuery()");

        if (m_out == null)
            return false;

        if (ptrParent.getDebugMode()) System.out.println("Requesting: " + szGET + szQuery + szHTTPTAIL);
        //System.out.println("Requesting: " + szGET + szQuery + " <minus tail>");   // + szHTTPTAIL);
        try {
            m_out.writeBytes(szGET + szQuery + szHTTPTAIL);
        } catch(IOException ioe) {
            System.out.println("IOException writing http to web server: " + ioe);
            return false;
        }

        return true;
    }

    // -- read all return data
    private boolean handleSQLQueryResponse() {
        if (ptrParent.getDebugMode()) System.out.println("UConnect handleSQLQueryResponse()");

        if (m_in == null)
            return false;

        boolean bDone = false;
        String szTemp = null;
        String szHTTPResponse = null;
        try {
            while (((szTemp = m_in.readLine()) != null) && (!bDone)) {
                szHTTPResponse = szHTTPResponse + szTemp;   //removed crlf
                if (ptrParent.getDebugMode()) System.out.println("szHTTPResponse: " + szHTTPResponse);
                //System.out.println("szHTTPResponse: " + szHTTPResponse);
                if (szHTTPResponse.indexOf("/ENDOFSCRIPT") != -1){
                    if (ptrParent.getDebugMode()) System.out.println("found end");
                    bDone = true;
                    //break;  //force exit of while loop
                }
            }
            if (ptrParent.getDebugMode()) System.out.println("szHTTPResponse: " + szHTTPResponse);
        } catch(IOException ioe) {
            System.out.println("IOException reading http web server response: " + ioe);
            return false;
        }

        //if (ptrParent.getDebugMode()) System.out.println("Query completed");
        if (ptrParent.getDebugMode()) System.out.println("Query completed");

        return true;
    }

    private String handleszSQLQueryResponse() {
        if (ptrParent.getDebugMode()) System.out.println("UConnect handleszSQLQueryResponse()");

        if (m_in == null)
            return "no input stream";

        boolean bDone = false;
        String szTemp = null;
        String szHTTPResponse = null;
        try {
            while (((szTemp = m_in.readLine()) != null) && (!bDone)) {
                szHTTPResponse = szHTTPResponse + szTemp;   //removed crlf
                if (ptrParent.getDebugMode()) System.out.println("szHTTPResponse: " + szHTTPResponse);
                //System.out.println("szHTTPResponse: " + szHTTPResponse);
                if (szHTTPResponse.indexOf("/ENDOFSCRIPT") != -1){
                    if (ptrParent.getDebugMode()) System.out.println("found end");
                    bDone = true;
                    //break;  //force exit of while loop
                }
            }
            if (ptrParent.getDebugMode()) System.out.println("szHTTPResponse: " + szHTTPResponse);
        } catch(IOException ioe) {
            System.out.println("IOException reading http web server response: " + ioe);
            return "exception getting" + szHTTPResponse;
        }
        //if (ptrParent.getDebugMode()) System.out.println("Query completed");
        if (ptrParent.getDebugMode()) System.out.println("Query completed");

        return szHTTPResponse;
    }
}
