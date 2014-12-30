import java.applet.Applet;
import java.net.URL;    //char
import java.net.MalformedURLException;
import java.awt.Graphics;
import java.awt.Color;

// -- main applet, creates a thread tha sleeps for 30 secs
// -- and a database object that performs SQL Inserts
public class pulse extends Applet {

    private final static boolean bInDebugMode = false;

    private final static long INTERVAL = 60000;

    private UConnect dbObj = null;
    private periodJolt thrdJolt = null;

    private Applet apObj = null;

    private String m_szUsername = null;
    private String m_szPuzzleDataset = null;
    private String m_szPuzzleType = null;
    private String m_szPuzzleID = null;
    private String m_szBackGndClr = null;
    private Color m_clrBackGndClr = null;

    private static final char SNG_QT = '\u0027';

    // -- constructor
    public pulse() {
        if (bInDebugMode) System.out.println("pulse constructor");
    }

    public void init() {
        super.init();

        if (bInDebugMode) System.out.println("pulse init()");
        if (dbObj != null)
            FailedSoExit(1);

        m_szUsername = getParameter("szUserName");
        m_szPuzzleDataset = getParameter("szDataset");
        if (m_szPuzzleDataset.length() >= 8) {
            m_szPuzzleType = m_szPuzzleDataset.substring(0,2);
            m_szPuzzleID = m_szPuzzleDataset.substring(2,8);
        }

        m_szBackGndClr = getParameter("background");

        if (m_szBackGndClr.equals("0"))
            m_clrBackGndClr = Color.white;
        else if (m_szBackGndClr.equals("1"))
            m_clrBackGndClr = Color.gray;
        else if (m_szBackGndClr.equals("2"))  //future
            m_clrBackGndClr = Color.red;

        setForeground(m_clrBackGndClr);
        setBackground(m_clrBackGndClr);

        dbObj = new UConnect(this);

        if (thrdJolt != null)
            FailedSoExit(3);

        thrdJolt = new periodJolt(this, INTERVAL);
        thrdJolt.setPriority(Thread.MIN_PRIORITY);
        thrdJolt.start();
    }

    public void start() {
        //super.start();

        if (bInDebugMode) System.out.println("pulse start()");
    }

    /*
    public void paint(Graphics g) {
        System.out.println("pulse paint()");
        g.setColor(bkclr);
        g.fillRect(0,0,this.bounds().width,this.bounds().height);
    }
    */

    public synchronized void pullTrigger(int nPeriod, int bIsExiting /* fake boolean */) {     //, Thread aThread) {
        if (bInDebugMode) System.out.println("pulse pullTrigger()");
        if (dbObj == null)
            FailedSoExit(5);

        // -- tell DB the user has been around for an INTERVAL

        // -- ignore the puzzleid
        //if (!dbObj.doSQLPeriodUpdate("neilski", "", nPeriod)) {

        if (m_szUsername.length() == 0) {
            FailedSoExit(11);
        }

        // http://www.cyberpuzzles.aust.com/scripts/uconstruct.exe?proc=testproc7&company=cp&action=exec" +
        //                "&@recidval=" + prd + "&@name=" + nm +
        //                "&template=users2.txt
        //
        if (m_szPuzzleDataset.length() == 0) {
            //if (!dbObj.doSQL_Update("http://www.cyberpuzzles.aust.com/scripts/uconstruct.exe?proc=spjava_userusage&company=cp&action=exec" +
            if (!dbObj.doSQL_Update("http://203.7.178.131/scripts/uconstruct.exe?proc=spjava_userusage&company=cp&action=exec" +
                        "&@recidval=" + nPeriod + "&@name=" + m_szUsername +
                        "&template=userusage.txt")) {
                FailedSoExit(9);
            }
            /*
            if (!dbObj.doSQL_Update("http://www.cyberpuzzles.aust.com/scripts/uconstruct.exe?proc=testproc7&company=cp&action=exec" +
                        "&@recidval=" + nPeriod + "&@name=" + m_szUsername +
                        "&template=users2.txt")) {
                FailedSoExit(4);
            }
            */
        } else {
            //combine user and puzzle usage updates into 1 sql sp
            // -- on end of active table, ie puzzle_id_### -- set to '000000', just update userusage!
            if (m_szPuzzleID.equals("000000")) {
                //if (!dbObj.doSQL_Update("http://www.cyberpuzzles.aust.com/scripts/uconstruct.exe?proc=spjava_userusage&company=cp&action=exec" +
                if (!dbObj.doSQL_Update("http://203.7.178.131/scripts/uconstruct.exe?proc=spjava_userusage&company=cp&action=exec" +
                            "&@recidval=" + nPeriod + "&@name=" + m_szUsername +
                            "&template=userusage.txt")) {
                    FailedSoExit(91);
                }
            } else {
                //if (!dbObj.doSQL_Update("http://www.cyberpuzzles.aust.com/scripts/uconstruct.exe?proc=spjava_userpuzzleusage&company=cp&action=exec" +
                if (!dbObj.doSQL_Update("http://203.7.178.131/scripts/uconstruct.exe?proc=spjava_userpuzzleusage&company=cp&action=exec" +
                            "&@recidval=" + nPeriod + "&@name=" + m_szUsername +
                            "&@puzzle_type_in=" + m_szPuzzleType +
                            "&@puzzle_id_in=" + m_szPuzzleID +
                            "&@exited=" + bIsExiting +
                            "&@duration=" + thrdJolt.getDuration() +
                            "&template=userpuzzleusage.txt")) {
                    FailedSoExit(92);
                }
            }

/*
            if (!dbObj.doSQL_Update("http://www.cyberpuzzles.aust.com/scripts/uconstruct.exe?proc=testproc7&company=cp&action=exec" +
                        "&@recidval=" + nPeriod + "&@name=" + m_szUsername +
                        "&template=users2.txt")) {
                FailedSoExit(9);
            }
            if (!dbObj.doSQL_Update("/scripts/uconstruct.exe?proc=testproc8&company=cp&action=exec" +
                        "&@recidval=" + nPeriod + "&@puzzle_type_in=" + m_szPuzzleType +
                        "&@puzzle_id_in=" + m_szPuzzleID + "&template=users2.txt")) {
                FailedSoExit(12);
            }
*/
        }

        /*
        // -- success, so interupt threaded timeout
        aThread.interrupt();
        */
    }

    public synchronized void doTimeout() {
        if (bInDebugMode) System.out.println("pulse doTimeout()");
        FailedSoExit(6);
    }

    public String getNextDataSetID(String szPuzzleType) {
        if (bInDebugMode) System.out.println("pulse getCurrentDataSetID()");
        if (dbObj == null)
            FailedSoExit(22);

        if (m_szUsername.length() == 0) {
            FailedSoExit(20);
        }


        String szNextPzlID = null;
        //if ((szNextPzlID = dbObj.doszSQL_Update("http://www.cyberpuzzles.aust.com/scripts/uconstruct.exe?" +
        if ((szNextPzlID = dbObj.doszSQL_Update("http://203.7.178.131/scripts/uconstruct.exe?" +
                    "proc=spjava_setgetuseractivepuzzle&" +
                    "company=cp&action=exec" +
                    "&@name=" + m_szUsername +
                    "&@puzzle_type=" + m_szPuzzleType +
                    "&template=setgetuseractivepuzzle.txt")).equals("")) {
            FailedSoExit(40);
        }

/*
        // set the next puzzleid
        if (!dbObj.doSQL_Update("http://www.cyberpuzzles.aust.com/scripts/uconstruct.exe?proc=spjava_setnextpuzzleid&" +
                    "company=cp&action=exec" +
                    "&@name=" + m_szUsername +
                    "&@puzzle_type_in=" + m_szPuzzleType +
                    "&template=setnextpuzzleid.txt")) {
            FailedSoExit(40);
        }

        //get the next puzzle id
        String szNextPzlID = null;
        if (m_szPuzzleType.equals("QX")) {
            if ((szNextPzlID = dbObj.doszSQL_Update("http://www.cyberpuzzles.aust.com/scripts/uconstruct.exe?company=cp&" +
                    "table=users&" +
                    "select=name=" + SNG_QT + m_szUsername + SNG_QT +
                    "&template=getnextpuzzleidqx.txt")).equals("")) {
                FailedSoExit(41);
            }
*/

        /*
        String szCurPuzzleID = null;
        dbObj.doSQLGetCurrentDataSetID(m_szUsername, szPuzzleType);
        if ((szCurPuzzleID.equals("")) || (szCurPuzzleID.length() != 6)) {
            if (bInDebugMode) System.out.println("pulse FailedSoExit(): " + szPuzzleType + " : " + szCurPuzzleID + " ::");
            FailedSoExit(21);
        }
        */

        //return szCurPuzzleID;
        //return "000003";

        if (bInDebugMode) System.out.println("returned string****" + szNextPzlID);
        //String szCompareString = m_szUsername + m_szPuzzleType;   text/html
        String szCompareString = "text/html";
        if (bInDebugMode) System.out.println("compare string: "+szCompareString);
        int nPtr = szNextPzlID.indexOf(szCompareString);
        if (bInDebugMode) System.out.println("ptr****" + nPtr);
        if ((nPtr) != -1) {
            String szResult = szNextPzlID.substring(nPtr + szCompareString.length(), nPtr + szCompareString.length() + 6);
            if (bInDebugMode) System.out.println("pulse success" + szResult);
            int n = 0;
            try{
                n = new Integer(szResult.trim()).intValue();
                m_szPuzzleID = szResult;
            } catch (NumberFormatException nfe) {
                System.out.println("NumberFormatException: " + nfe);
                m_szPuzzleID = "000000";
            }
            return szResult;
        } else {
            if (bInDebugMode) System.out.println("pulse failure" + szNextPzlID);
            //m_szPuzzleID = "999999";  //remains the same
            m_szPuzzleID = "000000";
            return "";
        }
    }

    // -- failed, so send user to the failed db pg
    private void FailedSoExit(int nPlace) {
        //if (bInDebugMode) System.out.println("pulse FailedSoExit()" + nPlace);
        if (bInDebugMode) System.out.println("pulse FailedSoExit()" + nPlace);

        if ((thrdJolt != null) && (thrdJolt.isAlive())) {
            thrdJolt.stop();
            thrdJolt = null;
        }

        if (dbObj != null) {
            //dbObj.dbExit();
            dbObj = null;
        }

        try {
            //getAppletContext().showDocument(new URL("http://www.objectcentric.com"));
            getAppletContext().showDocument(new URL(getCodeBase().getHost().toString() + "/index.html"));
            //http://www.cyberpuzzles.aust.com/dbdown.html
        } catch (MalformedURLException murle) {
            if (bInDebugMode) System.out.println("MalformedURLException showing dbdown.html:" + murle);
        }

    }

    // -- browser calls stop() on document exit
    public void stop() {
        if (bInDebugMode) System.out.println("pulse stop()");
        //super.stop();
        hide();

        pullTrigger(thrdJolt.getDeficitDiff(), 1);     //, this);

        if ((thrdJolt != null) && (thrdJolt.isAlive())) {
            if (bInDebugMode) System.out.println("calling thrdJolt stop()");
            thrdJolt.stop();
            thrdJolt = null;
        }

        if (dbObj != null) {
            //dbObj.dbExit();
            dbObj = null;
        }
        //dispose();
        //System.exit(1);
    }

    public boolean getDebugMode() {
        return bInDebugMode;
    }

    // -- destruction time, java has no destructor's
    // -- or over-ride finalize() for java objects derived from type Object
    public void destroy() {
        super.destroy();
        if (bInDebugMode) System.out.println("pulse destroy()");
    }
}