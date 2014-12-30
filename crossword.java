////////////////////////////////////////////////////////////////////////////
//                                                                        //
//      Module:     crossword.java                                        //
//      Author:     Aaron Saikovski                                       //
//      Date:       24/03/97                                              //
//      Purpose:    A crossword Applet based on the well known crossword. //
//                  Part of the OzEmail Cyber puzzles suite.              //
//                                                                        //
////////////////////////////////////////////////////////////////////////////

import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.applet.AudioClip;
import java.net.*;

                //************************************************************************//
                //*NOTE: ALL THREE VERSIONS OF THE CROSSWORD WILL BE CONTAINED IN HERE.  *//
                //*THE PUZZLE ID READ FROM THE DATASTREAM WILL CONTROL THE PUZZLE TYPE.  *//
                //************************************************************************//

////////////////////////////////////////////////////////////////////////////
//Naming conventions
//
//  Image == img
//  Font == fnt
//  FontMetrics == fm
//  Square == sq
//  boolean == b
//  colour == clr
//  int == n
//
////////////////////////////////////////////////////////////////////////////

public class crossword extends Applet implements Runnable {


    //Next button was pressed
    boolean bNextPressed = false;

    //Puzzle State machines
    public boolean bPuzzleFinished = false, bSetFinished = false;

    //Next Puzzle is currently unavailable flag
    private boolean bIsNextPuzzleReady = true;

    //Stores timestamp of mouseUpEvent
    long lLastHint = 0;

    //Monster boolean
    //boolean bPuzSetFinished = false, bAllImagesLoaded = false;
    boolean bAllImagesLoaded = false;

    //String for Puzzle ID of last puzzle in set of ten
    //String szLastPuzzleID = null;
    String szRelDir = null, szDataSet = null, szPuzData = null;

    //Default bubble settings....set in InitData()
    int nBubbleOut = -99, nCurrHintBubble = 0;

    //Repaint variables
    boolean bBufferDirty = false, bInitCrossword = false;

    Image imBackBuffer;
    Graphics gBackBuffer;
    boolean bNewBackFlush=false;

    //Listboxes
    List lstClueAcross = null;
    List lstClueDown = null;

    //Listbox position variables
    public final static int nHORIZ_LIST_GAP = 12;

    //Scoring label points
    Point ptQXPlayerScoreLabel = null;
    Point ptQXPlayerScore = null;
    Point ptTXPlayerScoreLabel = null;
    Point ptTXPlayerScore = null;
    Point ptJXPlayerScoreLabel = null;
    Point ptJXPlayerScore = null;

    //Listbox heading text positions
    Point ptTopAcross = null;
    Point ptTopDown = null;

     //QuickCrossword Polygon region declarations
    int[] nQXClearXpoints = {305, 347, 404, 350}, nQXClearYpoints = {356, 344, 365, 385};
    Polygon polyQXClear = new Polygon( nQXClearXpoints, nQXClearYpoints, 4);
    int[] nQXCheckXpoints = {257, 277, 290, 292, 279, 259, 245, 244}, nQXCheckYpoints = {346, 345, 355, 369, 382, 383, 374, 355};
    Polygon polyQXCheck = new Polygon(nQXCheckXpoints, nQXCheckYpoints, 8);
    int[] nQXNextXpoints = {407, 466, 503, 405}, nQXNextYpoints = {353, 351, 386, 386};
    Polygon polyQXNext = new Polygon(nQXNextXpoints, nQXNextYpoints, 4);
    int[] nQXCliffXpoints = {671,585,591,582,580,586,581,585,564,550,544,551,590,583,590,586,611,619,611,612,596,613,617,642,660,660,671,671,659,651,658,658,671},
    nQXCliffYpoints = {389,389,353,348,342,315,311,284,295,291,280,269,254,214,205,199,159,151,148,110,107,101,83,81,89,102,106,111,113,144,150,159,171};
    Polygon polyQXCliff = new Polygon(nQXCliffXpoints, nQXCliffYpoints,33);

    //TV Crossword Polygon region declarations
    int[] nTXClearXpoints = {442, 440, 386, 380}, nTXClearYpoints = {341, 380, 384, 352};
    Polygon polyTXClear = new Polygon( nTXClearXpoints, nTXClearYpoints, 4);
    int[] nTXCheckXpoints = {350,353,301,302,}, nTXCheckYpoints = {355, 383, 383, 358};
    Polygon polyTXCheck = new Polygon(nTXCheckXpoints, nTXCheckYpoints, 4);
    int[] nTXNextXpoints = {516,511,483,483,}, nTXNextYpoints = {336, 378, 379, 337};
    Polygon polyTXNext = new Polygon(nTXNextXpoints, nTXNextYpoints, 4);
    //Polygons changed to incorporate TV screen as polygon region
    //int[] nTXRayXpoints = {543,544,543,543,490,483,468,464,460,454,461,466,468,489,490,497,494,489,488,484,487,491,507,521},
    //nTXRayYpoints = {29, 47, 68, 90, 89, 89, 86, 84, 88, 88, 79, 79, 78, 78, 67, 55, 50, 47, 41, 36, 32, 31, 29, 29};
    //Polygon polyTXRay = new Polygon(nTXRayXpoints, nTXRayYpoints,24);
    int[] nTXRayXpoints = {556, 554, 446, 442}, nTXRayYpoints = {28, 89, 86, 34};
    Polygon polyTXRay = new Polygon(nTXRayXpoints, nTXRayYpoints,4);

    //Jnr Crossword Polygon region declarations
    int[] nJXClearXpoints = {482,477,461,443,438,458}, nJXClearYpoints = {298,358,363,357,298,293};
    Polygon polyJXClear = new Polygon( nJXClearXpoints, nJXClearYpoints, 6);
    //int[] nJXCheckXpoints = {474, 463, 463, 456, 449, 449, 424, 424, 416, 409, 409, 400, 398, 379, 372, 375, 378, 383, 393, 416, 422, 439, 453, 465},
    //nJXCheckYpoints = {309, 335, 348, 353, 349, 336, 336, 347, 352, 348, 325, 322, 315, 317, 300, 295, 308, 309, 294, 291, 295, 296, 291, 295};
    //Polygon polyJXCheck = new Polygon(nJXCheckXpoints, nJXCheckYpoints, 24);
    int[] nJXNextJXpoints = {575,575,562,561,550,535,534,517,512,512,506,488,482,485,501,505,514,530,530,552,572},
    nJXNextYpoints = {320,339,358,382,385,384,366,361,347,334,337,335,326,318,304,299,294,304,326,328,321};
    Polygon polyJXNext = new Polygon(nJXNextJXpoints, nJXNextYpoints, 21);
    int[] nJXBilbyXpoints = {173, 139, 141, 157, 161, 148, 114, 110, 91, 76, 89, 82, 58, 57, 41, 39, 50, 63, 63, 74, 82, 79, 86, 89, 75, 85, 39, 20, 52, 42, 76, 96, 113, 124},
    nJXBilbyYpoints = {289, 293, 314, 319, 328, 335, 331, 356, 368, 357, 337, 319, 299, 240, 224, 209, 209, 254, 294, 312, 309, 296, 273, 229, 217, 198, 165, 158, 150, 142, 146, 181, 183, 206};
    Polygon polyJXBilby = new Polygon(nJXBilbyXpoints, nJXBilbyYpoints,34);

    //Parser class
    crosswordparser MrParser;

    //Data set variables
    String szPuzzleType = null;
    public int nNumCols = 0, nNumRows = 0, nNumAcross = 0, nNumDown = 0, nPuzzleID = 0;
    int[] nColRef, nRowRef, nQuesNum;
    boolean[] bDataIsAcross;
    String[] szClues = null, szAnswers = null;
    int[] nCosts = {0,0,0,0,0,0};
    String szGetLetters = null, szTmpGetLetters, szBlurb = null;
    int nNumQuestions;

    //Data set instance variable
    nDatasetUDT[] udtDataSet;

    Image imgBackBuffer;
    Font fntnumFont, fntFont, fntScore, fntListhead ;
    FontMetrics fntmFont;

    //Square instance variable
    Square[][] sqPuzzleSquares = null;

    //ClueAnswer Instance variable
    ClueAnswer[] caPuzzleClueAnswers = null;

    //Highlight Constants
    public final static int nCURRENT_LETTER = 1;
    public final static int nCURRENT_WORD = 2;
    public final static int nCURRENT_NONE = 3;

    //Offset constants
    public final static int nCROSS_BORDER_WIDTH = 3;
    int nCrossOffsetX = 5;
    int nCrossOffsetY = 5;

    //Crossword dimension constants
    public final static int nMAX_CROSS_WIDTH = 291;
    public final static int nMAX_CROSS_HEIGHT = 291;

    String strGuesses[][] = null;
    Square sqCurrentSquare = null;
    boolean bUpdatedActiveArea = false;
    boolean bChangedActiveArea = false;

    //Square width and height constants
    public static int nSquareWidth = 0;
    public static int nSquareHeight = 0;

    //X and Y Offsets for the square's answer number.
    public final static int nXNUM_OFFSET = 2, nYNUM_OFFSET = 9;

    //Applet control key constants
    public final static int nTAB_KEY = 9;           //Tab key
    public final static int nDELETE_KEY = 127;      //Delete key
    public final static int nBACKSPACE_KEY = 8;     //Delete key
    public final static int nUP_ARROW = 1004;       //Up Arrow
    public final static int nDOWN_ARROW = 1005;     //Down Arrow
    public final static int nLEFT_ARROW = 1006;     //Left Arrow
    public final static int nRIGHT_ARROW = 1007;    //Right Arrow
    public final static int nSPACEBAR = 32;         //Spacebar

    //Crossword Width and Height variables
    int nCrosswordWidth=0, nCrosswordHeight=0, nCrosswordOffset=6;

    //Status of row/column orientation (Across or Down)
    public boolean bIsAcross = true;

    //Form control constants
    final static int nBUTTONWIDTH = 105;
    final static int nBUTTONHEIGHT = 26;
    final static int nLISTWIDTH_LG = 400;
    final static int nLISTWIDTH_SM = 200;
    final static int nLISTHEIGHT = 90;

    //Form Fonts
    Font fntSmFormFont = null;
    Font fntLgFormFont = null;
    Font fntListFont = null;

    //Mouse Coords
    int nMouseX = 0;
    int nMouseY = 0;

    //Images to use for Crossword squares
    Image imgHighliteSquare = null;
    Image imgSquareWord = null;
    Image imgNormalSquare = null;

    //Background Image to use
    Image imgBackground = null;

    //Tab key variable
    int nTabPress = 0;

    //Scoring variable
    public int nScore = 0;

    //Rectangle variable
    Rectangle rectCrossWord = null;

    //Component focus variable
    int nFocusState = 0;

    //Has the crossword finished
    //public boolean bIsFinished = false;

    //Audio to use.
    audioThread authIntro = null, authHint1 = null, authHint2 = null, authHint3 = null, authFinish = null, authEndPuz = null;

    //mouseMove String
    String szPuzzleTitle = null;

    //Number of times Hint has been accessed by the user
    int nUserHintPress = 0;

    //Thread and thread flag declarations
    Thread thMainThread = null;
    imageThread imthHiLiteSqu = null, imthSquWord = null, imthNormSqu = null, imthBackground = null;

    //Data thread
    dataThread dtCrosswordDataThread = null;

    //Bubble image variables
    imageThread imthBubble1 = null;
    imageThread imthBubble2 = null;
    imageThread imthBubble3 = null;
    imageThread imthBubble4 = null;
    imageThread imthBubble5 = null;
    imageThread imthBubble6 = null;

    //Next puzzle unavailable
    imageThread imthBubble7 = null;


    //Background Image loaded flag
    boolean bBackgroundImageLoaded = false;

    //Position of Back to puzzle land button
    //
    //Quick
    static final Point pt_QXBACK_BUTTON = new Point(395, 90);
    static final Rectangle rectQXBACK_BUTTON = new Rectangle( pt_QXBACK_BUTTON.x, pt_QXBACK_BUTTON.y, 182, 94);

    //TV
    static final Point pt_TXBACK_BUTTON = new Point(315, 150);
    static final Rectangle rectTXBACK_BUTTON = new Rectangle( pt_TXBACK_BUTTON.x, pt_TXBACK_BUTTON.y, 182, 94);

    //Junior
    static final Point pt_JXBACK_BUTTON = new Point(250, 130);
    static final Rectangle rectJXBACK_BUTTON = new Rectangle( pt_JXBACK_BUTTON.x, pt_JXBACK_BUTTON.y, 182, 94);

    //Back to puzzleland image
    imageThread imthPuzzleBack = null;

    //Flag back button
    boolean bBackButtonOn = false;

    //More puzzles in set boolean flag
    boolean bMorePuzzles = false;

    //QuickSolve cheating mechanism
    int[] nXCheatXpoints = {672, 672, 664, 664}, nXCheatYpoints = {0, 8, 8, 0};
    Polygon polyQuickSolve = new Polygon(nXCheatXpoints, nXCheatYpoints, 4);

    //Thread Monitor activation region
    //Rectangle rectThreadMonitor = new Rectangle(0, 382, 8, 8);
    //ThreadMon ThrMon = null;
    //boolean bIsThrOn = false;

    /*---------------------------------------------------------------*/

    //Applet Initialisation
    public void init() {
        this.showStatus("Crossword Initialising");
        super.init();
        setLayout(null);
        this.setBackground(Color.white);
    }

    /*---------------------------------------------------------------*/

    //Old Code
    //Gets the next puzzle ID from the pulse applet.
    /*public boolean getNextPuzzleData(){

        int nPuzzleIDTmp = nPuzzleID;
        String szPuzzleIDTmp = "";

        String szNewPuzzleDataSetFileName = "";
        try {
            Applet ap = getAppletContext().getApplet("cp pulse");

            if (ap != null) {
                if (ap instanceof pulse) {
                    System.out.println("IAC in progress...");

                    //Record stats
                    //case 2 - User request for next puzzle

                    //username not required as pulse has that
                    System.out.println("getNextDataSetID request with: " + String.valueOf(nPuzzleID));

                    //
                    //Make an Applet call to the pulse applet to obtain the next puzzle id
                    //szPuzzleIDTmp = ((pulse)ap).getNextDataSetID(szPuzzleType);
                    szPuzzleIDTmp = ((pulse)ap).getNextDataSetID2((new String("00000").substring(String.valueOf(nPuzzleID).length() - 1))
                                        + String.valueOf(nPuzzleID), nScore, bPuzzleFinished);

                    if (szPuzzleIDTmp.equals("")) {
                        //nPuzzleId remains unchanged and cancel getting next puzzle
                        szNewPuzzleDataSetFileName = (new String("00000").substring(String.valueOf(nPuzzleID).length() - 1)) + String.valueOf(nPuzzleID);
                    } else {
                        nPuzzleIDTmp = new Integer(szPuzzleIDTmp.trim()).intValue();
                        //szNewPuzzleDataSetFileName = String.valueOf(nPuzzleIDTmp);
                        szNewPuzzleDataSetFileName = (new String("00000").substring(String.valueOf(nPuzzleIDTmp).length() - 1)) + String.valueOf(nPuzzleIDTmp);
                    }
                    System.out.println("getCurrentDataSetID response with: " + szNewPuzzleDataSetFileName);

                } else {
                    System.out.println("Applet is not <pulse.class>!");
                    //nPuzzleId remains unchanged and cancel getting next puzzle
                    szNewPuzzleDataSetFileName = (new String("00000").substring(String.valueOf(nPuzzleID).length() - 1)) + String.valueOf(nPuzzleID);
                }
            } else {
                System.out.println("Unable to find <pulse.class>!");
                //nPuzzleID remains unchanged and cancel getting next puzzle
                szNewPuzzleDataSetFileName = (new String("00000").substring(String.valueOf(nPuzzleID).length() - 1)) + String.valueOf(nPuzzleID);
            }
        } catch (Exception e) {
            System.out.println("Exception in getNextPuzzleData: " + e);
            //nPuzzleID remains unchanged and cancel getting next puzzle
            szNewPuzzleDataSetFileName = (new String("00000").substring(String.valueOf(nPuzzleID).length() - 1)) + String.valueOf(nPuzzleID);
            e.printStackTrace();
        }

        if (nPuzzleIDTmp == nPuzzleID)  //failed
            return true;
        else {
            nPuzzleID = nPuzzleIDTmp;
            if(Integer.parseInt(szNewPuzzleDataSetFileName) > 0){
                szNewPuzzleDataSetFileName = szPuzzleType + szNewPuzzleDataSetFileName + ".txt";
                dtCrosswordDataThread.newDataSet(szNewPuzzleDataSetFileName);
                System.out.println("succeeded getting next id: " + szNewPuzzleDataSetFileName);
                return true;
            } else {
                System.out.println("Found end of puzzle set: " + szNewPuzzleDataSetFileName);
                return false;
            }
        }
    }*/

     //Gets the next puzzle ID from the pulse applet.
    public boolean getNextPuzzleData(){

        int nPuzzleIDTmp = nPuzzleID;
        String szPuzzleIDTmp = "";

        String szNewPuzzleDataSetFileName = "";
        try {
            Applet ap = getAppletContext().getApplet("cp pulse");

            if (ap != null) {
                if (ap instanceof pulse) {
                    System.out.println("IAC in progress...");

                    System.out.println("getNextDataSetID request with: " + String.valueOf(nPuzzleID));

                    //Make an Applet call to the pulse applet to obtain the next puzzle id
                    szPuzzleIDTmp = ((pulse)ap).getNextDataSetID2((new String("00000").substring(String.valueOf(nPuzzleID).length() - 1)) + String.valueOf(nPuzzleID),
                                        nScore, bPuzzleFinished);

                    if (szPuzzleIDTmp.equals("")) {
                        //nPuzzleId remains unchanged and cancel getting next puzzle
                        szNewPuzzleDataSetFileName = (new String("00000").substring(String.valueOf(nPuzzleID).length() - 1)) + String.valueOf(nPuzzleID);
                    } else {
                        nPuzzleIDTmp = new Integer(szPuzzleIDTmp.trim()).intValue();
                        szNewPuzzleDataSetFileName = (new String("00000").substring(String.valueOf(nPuzzleIDTmp).length() - 1)) + String.valueOf(nPuzzleIDTmp);
                    }
                    System.out.println("getCurrentDataSetID response with: " + szNewPuzzleDataSetFileName);
                } else {
                    System.out.println("Applet is not <pulse.class>!");
                    //nPuzzleId remains unchanged and cancel getting next puzzle
                    szNewPuzzleDataSetFileName = (new String("00000").substring(String.valueOf(nPuzzleID).length() - 1)) + String.valueOf(nPuzzleID);
                }
            } else {
                System.out.println("Unable to find <pulse.class>!");
                //nPuzzleId remains unchanged and cancel getting next puzzle
                szNewPuzzleDataSetFileName = (new String("00000").substring(String.valueOf(nPuzzleID).length() - 1)) + String.valueOf(nPuzzleID);
            }
        } catch (Exception e) {
            System.out.println("Exception in getNextPuzzleData: " + e);
            //nPuzzleId remains unchanged and cancel getting next puzzle
            szNewPuzzleDataSetFileName = (new String("00000").substring(String.valueOf(nPuzzleID).length() - 1)) + String.valueOf(nPuzzleID);
            e.printStackTrace();
        }

        if (nPuzzleIDTmp == nPuzzleID)  //failed
            return true;
        else {
            nPuzzleID = nPuzzleIDTmp;
            if(Integer.parseInt(szNewPuzzleDataSetFileName) > 0){
                szNewPuzzleDataSetFileName = szPuzzleType + szNewPuzzleDataSetFileName + ".txt";
                dtCrosswordDataThread.newDataSet(szNewPuzzleDataSetFileName);
                System.out.println("succeeded getting next id: " + szNewPuzzleDataSetFileName);
                return true;
            } else {
                System.out.println("Found end of puzzle set: " + szNewPuzzleDataSetFileName);
                return false;
            }
        }
    }

    /*---------------------------------------------------------------*/

    //Get the next puzzle.
    public void getNextPuzzle() {

        //Only allow this when the puzzle sets have not been done.
        if (!bSetFinished) {

            int nPuzzleIdTmp = nPuzzleID;

            //Get next puzzle ID
            bMorePuzzles = getNextPuzzleData();

            if (nPuzzleIdTmp == nPuzzleID) {  //failed
                System.out.println("The next puzzle is currently unavailable, please try again in two minutes, Thankyou.");
                this.showStatus("The next puzzle is currently unavailable, please try again in two minutes, Thankyou.");

                //Show a status bubble if the DB is not ready
                bIsNextPuzzleReady = false;
                nBubbleOut = -88;
                bBufferDirty = true;
                buildBackBuffer();
                paint(getGraphics());
                return;
            }
            else {
                bIsNextPuzzleReady = true;
                nBubbleOut = -99;
                bBufferDirty = true;
                buildBackBuffer();
                paint(getGraphics());
            }


            bNextPressed = false;
            bPuzzleFinished = false;
            ptQXPlayerScoreLabel = null;
            ptQXPlayerScore = null;
            ptTXPlayerScoreLabel = null;
            ptTXPlayerScore = null;
            ptJXPlayerScoreLabel = null;
            ptJXPlayerScore = null;

            nNumQuestions = 0;
            nScore = 0;
            rectCrossWord = null;
            lstClueAcross.clear();
            lstClueDown.clear();
            lstClueAcross.hide();
            lstClueDown.hide();
            lstClueAcross = null;
            lstClueDown = null;
            ptTopAcross = null;
            ptTopDown = null;
            nColRef = null;
            nRowRef = null;
            nQuesNum = null;
            bDataIsAcross = null;
            szClues = null;
            szAnswers = null;
            udtDataSet = null;
            sqPuzzleSquares = null;
            caPuzzleClueAnswers = null;
            strGuesses = null;
            sqCurrentSquare = null;

            bBufferDirty = true;
            bInitCrossword = false;
            initPuzzleData();
            initData();
            initControls();
            buildCrossword();
            bNewBackFlush = true;

            sqCurrentSquare = caPuzzleClueAnswers[0].getSquare();
            bIsAcross = caPuzzleClueAnswers[0].bIsAcross;
            caPuzzleClueAnswers[0].HighlightSquares(sqCurrentSquare, true);
            lstClueAcross.select(0);

            buildBackBuffer();
            updateBackBuffer();
            paint(getGraphics());
            lstClueAcross.show();
            lstClueDown.show();
        }
    }

    /*---------------------------------------------------------------*/

    //Sets the puzzle stats for a puzzle
    public boolean setWebStats(){

        boolean bSuccess = false;

        try {
            Applet ap = getAppletContext().getApplet("cp pulse");

            if (ap != null) {
                if (ap instanceof pulse) {
                    System.out.println("IAC in progress...");
                    System.out.println("getNextDataSetID request with: " + String.valueOf(nPuzzleID));
                    System.out.println("setWebStats() nScore is " + nScore);

                    bSuccess = ((pulse)ap).setWebStats((new String("00000").substring(String.valueOf(nPuzzleID).length() - 1))
                                    + String.valueOf(nPuzzleID), nScore, bPuzzleFinished, bNextPressed);

                    //if (szPuzzleIDTmp.equals("")) {
                    System.out.println("succeeded setting webstats: " + bSuccess);
                    return bSuccess;
                } else { System.out.println("Applet is not <pulse.class>!"); }
            } else {
                System.out.println("Unable to find <pulse.class> in setWebStats()!");
            }
        } catch (Exception e) {
            System.out.println("Exception in setWebStats(): " + e);
            e.printStackTrace();
        }

        return false;
    }

    /*---------------------------------------------------------------*/

    //Applet start method - Kicks off threads.
    public void start(){

        //Get applet params from the HTML
        this.szRelDir = getParameter("szRelDir");
        this.szDataSet = getParameter("szDataSet");

        //Kick off the datathread
        if(dtCrosswordDataThread == null){
            dtCrosswordDataThread = new dataThread(szDataSet, szRelDir, this);
            dtCrosswordDataThread.setPriority(Thread.MAX_PRIORITY);
            dtCrosswordDataThread.start();
        }

        //Init the puzzle data
        initPuzzleData();

        //Assign last puzzle ID
        /*if (szPuzzleType.equals("QX"))
            szLastPuzzleID =  "QX000015";
        else if (szPuzzleType.equals("TX"))
            szLastPuzzleID = "TX000002";
        else if (szPuzzleType.equals("JX"))
            szLastPuzzleID = "JX000010";
        */

        //Instantiate the background image
        String szBackgroundImage = "";
        if (szPuzzleType.equals("QX"))
            szBackgroundImage = "wordypuz.gif";
        else if (szPuzzleType.equals("TX"))
            szBackgroundImage = "tvmovpuz.gif";
        else if (szPuzzleType.equals("JX"))
            //szBackgroundImage = "kidspuzz.gif"; //Old image with obsolete check words button
            szBackgroundImage = "kidspuzz.gif";

        if(imthBackground == null){
            imthBackground = new imageThread(szBackgroundImage, this);
            imthBackground.setPriority(Thread.MAX_PRIORITY);
            imthBackground.start();
        }


        //Intantiate image threads but do not start
        //
        //Highlighted square
        if(imthHiLiteSqu == null){
            imthHiLiteSqu = new imageThread("sqlite.gif", this);
            imthHiLiteSqu.setPriority(Thread.MAX_PRIORITY);
        }

        //Highlighted word
        if(imthSquWord == null){
            imthSquWord = new imageThread("wordlite.gif", this);
            imthSquWord.setPriority(Thread.MAX_PRIORITY);
        }

        //Normal Square
        if(imthNormSqu == null){
            imthNormSqu = new imageThread("normsq.gif", this);
            imthNormSqu.setPriority(Thread.MAX_PRIORITY);
        }

        //Back to puzzleland navigation
        if(imthPuzzleBack == null){
            imthPuzzleBack = new imageThread("backtosi.gif", this);
            imthPuzzleBack.setPriority(Thread.MIN_PRIORITY);
        }

        //Instantiate and start main thread
        if (thMainThread == null){
            thMainThread = new Thread(this);
            thMainThread.setPriority(Thread.MAX_PRIORITY);
            thMainThread.start();
        }


        ////////////////////////////////////////////////////////
        //
        //Bubble Threads
        if(true){

            ////////////////////////////////////////////////////////
            //Quick crossword
            if (szPuzzleType.equals("QX")) {

                if (imthBubble1 == null) {
                    imthBubble1 = new imageThread("word1.gif", this);
                    imthBubble1.setPriority(Thread.MAX_PRIORITY);
                }

                if (imthBubble2 == null) {
                    imthBubble2 = new imageThread("word2.gif", this);
                    imthBubble2.setPriority(Thread.MAX_PRIORITY);
                }

                if (imthBubble3 == null){
                    imthBubble3 = new imageThread("word3.gif", this);
                    imthBubble3.setPriority(Thread.MAX_PRIORITY);
                }

                if (imthBubble4 == null) {
                    imthBubble4 = new imageThread("word4.gif", this);
                    imthBubble4.setPriority(Thread.NORM_PRIORITY);
                }

                if (imthBubble5 == null) {
                    imthBubble5 = new imageThread("word5.gif", this);
                    imthBubble5.setPriority(Thread.NORM_PRIORITY);
                }

                if (imthBubble6 == null) {
                    imthBubble6 = new imageThread("word6.gif", this);
                    imthBubble6.setPriority(Thread.NORM_PRIORITY);
                }

                if (imthBubble7 == null) {
                    imthBubble7 = new imageThread("word_next.gif", this);
                    imthBubble7.setPriority(Thread.NORM_PRIORITY);
                }

            }

            ////////////////////////////////////////////////////////
            //TV crossword
            else if (szPuzzleType.equals("TX")) {

                if (imthBubble1 == null) {
                    imthBubble1 = new imageThread("tv1.gif", this);
                    imthBubble1.setPriority(Thread.MAX_PRIORITY);
                }

                if (imthBubble2 == null) {
                    imthBubble2 = new imageThread("tv2.gif", this);
                    imthBubble2.setPriority(Thread.MAX_PRIORITY);
                }

                if (imthBubble3 == null) {
                    imthBubble3 = new imageThread("tv3.gif", this);
                    imthBubble3.setPriority(Thread.NORM_PRIORITY);
                }

                if (imthBubble4 == null) {
                    imthBubble4 = new imageThread("tv4.gif", this);
                    imthBubble4.setPriority(Thread.NORM_PRIORITY);
                }

                if (imthBubble5 == null) {
                    imthBubble5 = new imageThread("tv5.gif", this);
                    imthBubble5.setPriority(Thread.NORM_PRIORITY);
                }

                if (imthBubble7 == null) {
                    imthBubble7 = new imageThread("tv_next.gif", this);
                    imthBubble7.setPriority(Thread.NORM_PRIORITY);
                }

            }

            ////////////////////////////////////////////////////////
            //Junior crossword
            else if (szPuzzleType.equals("JX")) {

                if (imthBubble1 == null) {
                    imthBubble1 = new imageThread("jnr1.gif", this);
                    imthBubble1.setPriority(Thread.MAX_PRIORITY);
                }

                if (imthBubble2 == null) {
                    imthBubble2 = new imageThread("jnr2.gif", this);
                    imthBubble2.setPriority(Thread.MAX_PRIORITY);
                }

                if (imthBubble3 == null) {
                    imthBubble3 = new imageThread("jnr3.gif", this);
                    imthBubble3.setPriority(Thread.NORM_PRIORITY);
                }

                if (imthBubble4 == null) {
                    imthBubble4 = new imageThread("jnr4.gif", this);
                    imthBubble4.setPriority(Thread.NORM_PRIORITY);
                }

                if (imthBubble5 == null) {
                    imthBubble5 = new imageThread("jnr5.gif", this);
                    imthBubble5.setPriority(Thread.NORM_PRIORITY);
                }

                if (imthBubble7 == null) {
                    imthBubble7 = new imageThread("jnr_next.gif", this);
                    imthBubble7.setPriority(Thread.NORM_PRIORITY);
                }
            }
        }


        ////////////////////////////////////////////////////////
        //
        //Audio threads
        //
        //Quick crossword
        if (szPuzzleType.equals("QX")) {
            if(authIntro == null){
                authIntro = new audioThread("det2.au", this);
                authIntro.setPriority(Thread.MAX_PRIORITY);
                authIntro.start();
            }

            if(authHint1 == null){
                authHint1 = new audioThread("det3.au", this);
                authHint1.setPriority(Thread.MAX_PRIORITY);
            }

            if(authHint2 == null){
                authHint2 = new audioThread("det4.au", this);
                authHint2.setPriority(Thread.MIN_PRIORITY);
            }

            if(authHint3 == null){
                authHint3 = new audioThread("det5.au", this);
                authHint3.setPriority(Thread.MIN_PRIORITY);
            }

            if(authFinish == null){
                authFinish = new audioThread("det6.au", this);
                authFinish.setPriority(Thread.MIN_PRIORITY);
            }

        }

        ////////////////////////////////////////////////////////
        //TV crossword
        if (szPuzzleType.equals("TX")) {
            if(authIntro == null){
                authIntro = new audioThread("goss2.au", this);
                authIntro.setPriority(Thread.MAX_PRIORITY);
                authIntro.start();
            }

            if(authHint1 == null){
                authHint1 = new audioThread("goss3.au", this);
                authHint1.setPriority(Thread.MAX_PRIORITY);
            }

            if(authHint2 == null){
                authHint2 = new audioThread("goss4.au", this);
                authHint2.setPriority(Thread.MIN_PRIORITY);
            }

            if(authHint3 == null){
                authHint3 = new audioThread("goss5.au", this);
                authHint3.setPriority(Thread.MIN_PRIORITY);
            }

            if(authFinish == null){
                authFinish = new audioThread("goss6.au", this);
                authFinish.setPriority(Thread.MIN_PRIORITY);
            }

        }

        ////////////////////////////////////////////////////////
        //Junior crossword
        if (szPuzzleType.equals("JX")) {
            if(authIntro == null){
                authIntro = new audioThread("bilby2p.au", this);
                authIntro.setPriority(Thread.MAX_PRIORITY);
                authIntro.start();
            }

            if(authHint1 == null){
                authHint1 = new audioThread("bilby3p.au", this);
                authHint1.setPriority(Thread.MAX_PRIORITY);
            }

            if(authHint2 == null){
                authHint2 = new audioThread("bilby4p.au", this);
                authHint2.setPriority(Thread.MIN_PRIORITY);
            }

            if(authHint3 == null){
                authHint3 = new audioThread("bilby5p.au", this);
                authHint3.setPriority(Thread.MIN_PRIORITY);
            }

            if(authFinish == null){
                authFinish = new audioThread("bilby6p.au", this);
                authFinish.setPriority(Thread.MIN_PRIORITY);
            }
        }

        //All crossword type utilise this .au file
        if(authEndPuz == null){
                authEndPuz = new audioThread("endpuz.au", this);
                authEndPuz.setPriority(Thread.NORM_PRIORITY);
        }

    }

    /*---------------------------------------------------------------*/

    //Thread run method
    public void run(){

        Image imTmpAppletImage = createImage(this.bounds().width, this.bounds().height);
        Graphics gTmpAppletGraphics = imTmpAppletImage.getGraphics();

        //Force thread to wait for the background image
        while(!imthBackground.bLoaded){
            try{
                thMainThread.sleep(100);
            }catch(InterruptedException ie){
                System.out.println("Sleep state for image retrieval thread threw Interrupted Exception");}
        }
        //Paint image threads background image and force repaint
        gTmpAppletGraphics.drawImage(imthBackground.imImage, 0, 0, this.bounds().width, this.bounds().height, this);
        getGraphics().drawImage(imthBackground.imImage, 0, 0, this.bounds().width, this.bounds().height, this);
        paint(getGraphics());
        try{
            thMainThread.sleep(100);
        }catch(InterruptedException ie){System.out.println("Sleep state for image retrieval thread threw Interrupted Exception");}

        //Assign image to instance variable and set boolean flag
        imgBackground = imthBackground.imImage;
        bBackgroundImageLoaded = true;

        //Create BackBuffer Image instance and Graphics to same
        imBackBuffer = createImage(this.bounds().width, this.bounds().height);
        gBackBuffer = imBackBuffer.getGraphics();
        buildBackBuffer();

        //Start image bubble retrieval threads
        imthHiLiteSqu.start();
        imthSquWord.start();
        imthNormSqu.start();

        //Start the back to puzzleland navigation image thread
        imthPuzzleBack.start();

        //Init the data
        initData();

        //Init the controls
        initControls();

        //Sleep while images load
        while((!imthHiLiteSqu.bLoaded)||(!imthSquWord.bLoaded)||(!imthNormSqu.bLoaded));

        //Assign crossword Squares images and cleanup threads
        gTmpAppletGraphics.drawImage(imthHiLiteSqu.imImage, 0, 0, this.bounds().width, this.bounds().height, this);
        gTmpAppletGraphics.drawImage(imthSquWord.imImage, 0, 0, this.bounds().width, this.bounds().height, this);
        gTmpAppletGraphics.drawImage(imthNormSqu.imImage, 0, 0, this.bounds().width, this.bounds().height, this);

		//Highlighted squares
		imgHighliteSquare = imthHiLiteSqu.imImage;

		//Highlighted word
		imgSquareWord = imthSquWord.imImage;

		//Normal Squares
        imgNormalSquare = imthNormSqu.imImage;

        try{
            thMainThread.sleep(200);
        }
            catch(InterruptedException ie){
            }

        //build the crossword data
        buildCrossword();
        bNewBackFlush = true;
        updateBackBuffer();
        paint(getGraphics());

        //Show the lists
        lstClueAcross.show();
        lstClueDown.show();

        //Set the initial active square
        sqCurrentSquare = caPuzzleClueAnswers[0].getSquare();

        //Return the orientation
        bIsAcross = caPuzzleClueAnswers[0].bIsAcross;

        //Highlight the default square...if allowed
        caPuzzleClueAnswers[0].HighlightSquares(sqCurrentSquare, true);

        //Set the default across list item to be the first item in the list
        lstClueAcross.select(0);

        //Forces dirty squares
        for(int i=0; i<nNumRows; i++){              //down
            for(int j=0; j<nNumCols; j++){
                    sqPuzzleSquares[i][j].bIsDirty = true;
            }
        }

        //Request a repaint
        bBufferDirty = true;
        repaint();

        //Start bubble threads
        imthBubble1.start();
        if (szPuzzleType.equals("QX"))
            imthBubble2.start();

		//Wait until the first bubble has loaded
        while(!imthBubble1.bLoaded);

        //Set index to bubble out
        nBubbleOut = 1;
        bBufferDirty = true;
        bNewBackFlush = true;
        updateBackBuffer();
        paint(getGraphics());

        //Play the intro sound
        authIntro.playIt();

        if(szPuzzleType.equals("QX")){
            try{
                thMainThread.sleep(4000);
            }catch(InterruptedException ie){}
        }
        else if(szPuzzleType.equals("TX")){
            try{
                thMainThread.sleep(4500);
            }catch(InterruptedException ie){}
        }
        else if(szPuzzleType.equals("JX")){
            try{
                thMainThread.sleep(4200);
            }catch(InterruptedException ie){}
        }


        //Start the au sound threads
        authHint1.start();
        authHint2.start();
        authHint3.start();
        authEndPuz.start();
        authFinish.start();

        //QX Load the second bubble..slowly
        if (szPuzzleType.equals("QX")) {
            while(!imthBubble2.bLoaded){
                try{
                    thMainThread.sleep(100);
                }catch(InterruptedException ie){
                    System.out.println("Sleep state for image retrieval thread threw Interrupted Exception");}
            }

            buildBackBuffer();

            nBubbleOut = 2;
            bBufferDirty = true;
            paint(getGraphics());
            try{
                thMainThread.sleep(5500);
            }catch(InterruptedException ie){}

            nBubbleOut = -99;
            bBufferDirty = true;
            buildBackBuffer();
            paint(getGraphics());

            try{
                thMainThread.sleep(5500);
            }catch(InterruptedException ie){}
        }

        nBubbleOut = -99;
        bBufferDirty = true;
        buildBackBuffer();
        repaint();

        //Start the remaining bubble threads
        if (szPuzzleType.equals("JX")||szPuzzleType.equals("TX"))
            imthBubble2.start();
        imthBubble3.start();
        imthBubble4.start();
        imthBubble5.start();
        if(szPuzzleType.equals("QX")) {
            imthBubble6.start();
        }


        //Next puzzle unavailable balloon
        imthBubble7.start();

        //Statusbar info
        if(szPuzzleType.equals("TX"))  //TV
            szPuzzleTitle = "Welcome to TV and Movie Mad Crossword!";
        else if (szPuzzleType.equals("JX")) //Junior
            szPuzzleTitle = "Welcome to Kids Korner Crossword!";
        else if (szPuzzleType.equals("QX")) //Quick
            szPuzzleTitle = "Welcome to Wordzone Crossword!";
        this.showStatus(szPuzzleTitle);


        //Destroy temporary Image and Graphics to such
        imTmpAppletImage = null;
        gTmpAppletGraphics = null;
        System.gc();

        //Give the Applet the focus
        this.requestFocus();

        while((!imthBubble3.bLoaded)||(!imthBubble4.bLoaded)||(!imthBubble5.bLoaded)){
                try{
                    thMainThread.sleep(100);
                }catch(InterruptedException ie){
                    System.out.println("Sleep state for image retrieval thread threw Interrupted Exception");}
            }


        //Wait until th eimages have loaded
        if(szPuzzleType.equals("QX")){
            while(!imthBubble6.bLoaded && !imthBubble7.bLoaded){
                try{
                    thMainThread.sleep(100);
                }catch(InterruptedException ie){
                    System.out.println("Sleep state for image retrieval thread threw Interrupted Exception");}
            }

        }

        //Get next puzzle ID
        bMorePuzzles = getNextPuzzleData();

        //Flag that all images hav loaded
        bAllImagesLoaded = true;
    }

    /*---------------------------------------------------------------*/

    //Applet paint method
    public void paint(Graphics g){
        if(bBufferDirty)
            updateBackBuffer();
        if(bBackgroundImageLoaded)
            g.drawImage(imBackBuffer, 0, 0, this.bounds().width, this.bounds().height, this);
    }

    /*---------------------------------------------------------------*/

    //Builds the crossword data references
    public void buildCrossword(){

        //Init squares
        sqPuzzleSquares = new Square[nNumRows][nNumCols];

        //Initialise the arrays
        for(int i=0; i<nNumRows; i++) {
            for(int j=0; j<nNumCols; j++) {
                sqPuzzleSquares[i][j] = new Square();
                sqPuzzleSquares[i][j].CreateSquare(nCrossOffsetX + i*nSquareWidth, nCrossOffsetY + j*nSquareHeight);

    		}
        }

        //Init ClueAnswers
        caPuzzleClueAnswers = new ClueAnswer[nNumQuestions]; //Need to work out dimensions

        for (int i=0; i<nNumQuestions; i++){

            //Need to build a temp object of sqAnswerSquares[]
            Square sqAnswerSquares[] = new Square[udtDataSet[i].szAnswer.length()];
            for (int j=0; j<udtDataSet[i].szAnswer.length(); j++){ //Need to work out number
                //Build the Clue/Answer sets
                if (udtDataSet[i].IsAcross){
                    sqAnswerSquares[j] = sqPuzzleSquares[udtDataSet[i].nCoordDown + j][udtDataSet[i].nCoordAcross];
                    if (j == 0) lstClueAcross.addItem(udtDataSet[i].nQuestionNum + ". " + udtDataSet[i].szClue);
                }
                else{
                    sqAnswerSquares[j] = sqPuzzleSquares[udtDataSet[i].nCoordDown][udtDataSet[i].nCoordAcross + j];
                    if (j == 0) lstClueDown.addItem(udtDataSet[i].nQuestionNum + ". " + udtDataSet[i].szClue);
                }
            }

            //Build the Clue/Answer references
            caPuzzleClueAnswers[i] = new ClueAnswer();
            caPuzzleClueAnswers[i].setObjectRef(udtDataSet[i].szAnswer,
                            udtDataSet[i].szClue, udtDataSet[i].nQuestionNum,
                            udtDataSet[i].IsAcross, sqAnswerSquares);
        }
        bInitCrossword = true;
    }

    /*---------------------------------------------------------------*/

    //Draws the background image to the backbuffer
    public void buildBackBuffer(){
       gBackBuffer.drawImage(imgBackground, 0, 0, this.bounds().width, this.bounds().height, this);

       //We wish to flush the background image buffer
       bNewBackFlush = true;
    }

    /*---------------------------------------------------------------*/

    //Build the Background buffer
    synchronized public void updateBackBuffer(){

        //If we are finished and puzzle is Junior
        //Do not rebuild crossword
        if (bSetFinished && szPuzzleType.equals("JX")) {
            //lstClueAcross.hide();
            //lstClueDown.hide();
        }

        //If repaint required
		else {
		//if (true) {
    		    if(bNewBackFlush) {

    		    //Show the crossword score
    		    drawCrosswordScore(gBackBuffer);

                //Draw some rectangles
                gBackBuffer.setColor(Color.lightGray);
                gBackBuffer.drawRect(nCrossOffsetX-3,nCrossOffsetY-3,nCrosswordWidth+5, nCrosswordHeight+5);
                gBackBuffer.setColor(Color.gray);
                gBackBuffer.drawRect(nCrossOffsetX-2,nCrossOffsetY-2,nCrosswordWidth+3, nCrosswordHeight+3);
                gBackBuffer.drawRect(nCrossOffsetX-1,nCrossOffsetY-1,nCrosswordWidth+1, nCrosswordHeight+1);

                //Draw some lines to give a 3D effect
                if((szPuzzleType.equals("QX")) || (szPuzzleType.equals("TX"))){
                    gBackBuffer.setColor(Color.black);
                    gBackBuffer.drawLine(nCrossOffsetX-1, nCrossOffsetY-1, nCrossOffsetX-1,nCrossOffsetX+nCrosswordHeight-1);
                    gBackBuffer.drawLine(nCrossOffsetX-1, nCrossOffsetY-1, nCrossOffsetX+nCrosswordWidth-1, nCrossOffsetY-1);
                    gBackBuffer.setColor(Color.lightGray);
                    gBackBuffer.drawLine(nCrossOffsetX + nCrosswordWidth+1, nCrossOffsetY-1,nCrossOffsetX + nCrosswordWidth+1,nCrossOffsetY + nCrosswordHeight);
                    gBackBuffer.drawLine(nCrossOffsetX -1, nCrossOffsetY + nCrosswordHeight + 1 ,nCrossOffsetX + nCrosswordWidth+1,nCrossOffsetY + nCrosswordHeight+1);
                }
                else if(szPuzzleType.equals("JX")) {
                    gBackBuffer.setColor(Color.black);
                    gBackBuffer.drawLine(nCrossOffsetX-1, nCrossOffsetY-1, nCrossOffsetX-1, nCrossOffsetY+nCrosswordHeight-1);
                    gBackBuffer.drawLine(nCrossOffsetX-1, nCrossOffsetY-1, nCrossOffsetX+nCrosswordWidth-1, nCrossOffsetY-1);
                    gBackBuffer.setColor(Color.lightGray);
                    gBackBuffer.drawLine(nCrossOffsetX + nCrosswordWidth+1, nCrossOffsetY-1,nCrossOffsetX + nCrosswordWidth+1,nCrossOffsetY + nCrosswordHeight);
                    gBackBuffer.drawLine(nCrossOffsetX -1, nCrossOffsetY + nCrosswordHeight + 1 ,nCrossOffsetX + nCrosswordWidth+1,nCrossOffsetY + nCrosswordHeight+1);

                }

                if(!bSetFinished)
                    drawListHeaders(gBackBuffer);
                else{
                    lstClueAcross.hide();
                    lstClueDown.hide();
                }

                if(bInitCrossword)
                    for(int i=0; i<nNumRows; i++)              //down
                        for(int j=0; j<nNumCols; j++)          //across
                            sqPuzzleSquares[i][j].bIsDirty = true;
              }
        }

		///////////////////////////////////////////////////
        //
		//Draw the bubbles
        if(nBubbleOut != -99){
            if (szPuzzleType.equals("QX")) { //Quick
                switch (nBubbleOut){
                    case 1: gBackBuffer.drawImage(imthBubble1.imImage, 487, 70, this);
                            break;

                    case 2: gBackBuffer.drawImage(imthBubble2.imImage, 487, 70, this);
                            break;

                    case 3: gBackBuffer.drawImage(imthBubble3.imImage, 490, 70, this);
                            authHint1.playIt();
                            break;

                    case 4: gBackBuffer.drawImage(imthBubble4.imImage, 490, 70, this);
                            authHint2.playIt();
                            break;

                    case 5: gBackBuffer.drawImage(imthBubble5.imImage, 490, 70, this);
                            authHint3.playIt();
                            break;

                    case 6: gBackBuffer.drawImage(imthBubble6.imImage, 357, 57, this);
                            authFinish.playIt();
                            break;

                    case -88: gBackBuffer.drawImage(imthBubble7.imImage, 490, 70, this);
                              break;
                }
            }

			///////////////////////////////////////////////////
			//
            else if (szPuzzleType.equals("TX")) { //TV
                switch (nBubbleOut){
                    case 1: gBackBuffer.drawImage(imthBubble1.imImage, 316, 10, this);
                            break;

                    case 2: gBackBuffer.drawImage(imthBubble2.imImage, 316, 10, this);
                            authHint1.playIt();
                            break;

                    case 3: gBackBuffer.drawImage(imthBubble3.imImage, 316, 10, this);
                            authHint2.playIt();
                            break;

                    case 4: gBackBuffer.drawImage(imthBubble4.imImage, 316, 10, this);
                            authHint3.playIt();
                            break;

                    case 5: gBackBuffer.drawImage(imthBubble5.imImage, 288, 54, this);
                            authFinish.playIt();
                            break;

                    case -88: gBackBuffer.drawImage(imthBubble7.imImage, 316, 10, this);
                              break;
                }

            }

			///////////////////////////////////////////////////
			//
			else if (szPuzzleType.equals("JX")) { //Junior
                switch (nBubbleOut){
                    case 1: gBackBuffer.drawImage(imthBubble1.imImage, 73, 103, this);
                            break;

                    case 2:  //If both the puzzle is finished and the puzzle set is finished..disable the hints
                             if (!bPuzzleFinished && !bSetFinished) {
                                gBackBuffer.drawImage(imthBubble2.imImage, 73, 103, this);
                                authHint1.playIt();
                             }
                            break;

                    case 3: //If both the puzzle is finished and the puzzle set is finished..disable the hints
                            if (!bPuzzleFinished && !bSetFinished) {
                                gBackBuffer.drawImage(imthBubble3.imImage, 73, 103, this);
                                authHint2.playIt();
                            }
                            break;

                    case 4: //If both the puzzle is finished and the puzzle set is finished..disable the hints
                            if (!bPuzzleFinished && !bSetFinished) {
                                gBackBuffer.drawImage(imthBubble4.imImage, 73, 103, this);
                                authHint3.playIt();
                            }
                            break;

                    case 5: gBackBuffer.drawImage(imthBubble5.imImage, 152, 75, this);
                            authFinish.playIt();
                            break;

                    case -88: gBackBuffer.drawImage(imthBubble7.imImage, 73, 103, this);
                              break;
                }

            }

        }


        ///////////////////////////////////////////////////
        //

        //If we are finished and puzzle is Junior
        //Do not redraw crossword
        if (bSetFinished && szPuzzleType.equals("JX")) {
            //Do nothing
        }
        else {
		//if(true){

        //Draw the crossword.
        for(int i=0; i<nNumRows; i++){              //down
            for(int j=0; j<nNumCols; j++){          //across

				//Check to see if a char is allowed
                if(sqPuzzleSquares[i][j].bIsCharAllowed){

					//Check to see if a repaint is required
                    if(sqPuzzleSquares[i][j].bIsDirty) {

						//Reset boolean dirty flag
                        sqPuzzleSquares[i][j].bIsDirty = false;

						//if the puzzle has finished draw blank squares
                        if (bPuzzleFinished) {
                            gBackBuffer.drawImage(imgNormalSquare, sqPuzzleSquares[i][j].nXCoord, sqPuzzleSquares[i][j].nYCoord,nSquareWidth,nSquareHeight, this);
                        }
                        else { //draw the images
                            if (sqPuzzleSquares[i][j].clBackColour == Color.white)
                                gBackBuffer.drawImage(imgNormalSquare, sqPuzzleSquares[i][j].nXCoord, sqPuzzleSquares[i][j].nYCoord,nSquareWidth,nSquareHeight, this);

                            if (sqPuzzleSquares[i][j].clBackColour == Color.yellow)
                                gBackBuffer.drawImage(imgSquareWord, sqPuzzleSquares[i][j].nXCoord, sqPuzzleSquares[i][j].nYCoord,nSquareWidth,nSquareHeight, this);

                            if (sqPuzzleSquares[i][j].clBackColour == Color.cyan)
                                gBackBuffer.drawImage(imgHighliteSquare, sqPuzzleSquares[i][j].nXCoord, sqPuzzleSquares[i][j].nYCoord,nSquareWidth,nSquareHeight, this);
                        }

                        gBackBuffer.setColor(Color.black);

                        //small number font
                        //hack walking across object boundaries
                        gBackBuffer.setFont(fntnumFont);
                        if (sqPuzzleSquares[i][j].clAcross != null){
                            if (sqPuzzleSquares[i][j].clAcross.sqAnswerSquares[0] == sqPuzzleSquares[i][j]){
                                gBackBuffer.drawString(String.valueOf(sqPuzzleSquares[i][j].clAcross.nQuestionNumber),nCrossOffsetX + i*nSquareWidth+1, nCrossOffsetY + j*nSquareHeight+getFontMetrics(fntnumFont).getHeight() - getFontMetrics(fntnumFont).getDescent());
                            }
                        }
                        if (sqPuzzleSquares[i][j].clDown != null){
                            if (sqPuzzleSquares[i][j].clDown.sqAnswerSquares[0] == sqPuzzleSquares[i][j])
                                gBackBuffer.drawString(String.valueOf(sqPuzzleSquares[i][j].clDown.nQuestionNumber),nCrossOffsetX + i*nSquareWidth+1, nCrossOffsetY + j*nSquareHeight+getFontMetrics(fntnumFont).getHeight() - getFontMetrics(fntnumFont).getDescent());
                        }

                        //Char entered by user.
                        gBackBuffer.setFont(fntFont);
                        gBackBuffer.setColor(sqPuzzleSquares[i][j].clForeColour);
                        gBackBuffer.drawString((String.valueOf(sqPuzzleSquares[i][j].chLetter).toUpperCase()),nCrossOffsetX + i*nSquareWidth + (nSquareWidth/2) - (getFontMetrics(fntFont).charWidth(sqPuzzleSquares[i][j].chLetter)) / 2, nCrossOffsetY + j*nSquareHeight + (nSquareHeight/2) + (getFontMetrics(fntFont).getHeight()/2)-getFontMetrics(fntFont).getDescent());

                    }
                }
                else{
                    gBackBuffer.setColor(Color.black);
                    gBackBuffer.fillRect(sqPuzzleSquares[i][j].nXCoord, sqPuzzleSquares[i][j].nYCoord,nSquareWidth,nSquareHeight);

                }
            }
         }
      }

      //Draw the backto puzzle land button
        if(bBackButtonOn){
            if (szPuzzleType.equals("QX")) //Quick
                gBackBuffer.drawImage(imthPuzzleBack.imImage, pt_QXBACK_BUTTON.x, pt_QXBACK_BUTTON.y, this);
            if (szPuzzleType.equals("TX")) //TV
                gBackBuffer.drawImage(imthPuzzleBack.imImage, pt_TXBACK_BUTTON.x, pt_TXBACK_BUTTON.y, this);
            if (szPuzzleType.equals("JX")) //Junior
                gBackBuffer.drawImage(imthPuzzleBack.imImage, pt_JXBACK_BUTTON.x, pt_JXBACK_BUTTON.y, this);
        }

        bNewBackFlush = false;
    }


    /*---------------------------------------------------------------*/

    //Overload the update method - make it a critical process
    public synchronized void update(Graphics g){
        paint(g);
    }

    /*---------------------------------------------------------------*/

    //Generic Applet Event handler
    public boolean handleEvent(Event event) {
        if (event.id == Event.LIST_SELECT && event.target == lstClueDown) {
                selChangeLstClueDown(event);
                return true;
        }
        else
        if (event.id == Event.LIST_SELECT && event.target == lstClueAcross) {
                selChangeLstClueAcross(event);
                return true;
        }
        if (event.id == Event.MOUSE_MOVE && event.target == this) {
                mouseMoveThis(event);
                return true;
        }

        return super.handleEvent(event);
    }

    /*---------------------------------------------------------------*/

    //Mouse down event
    public boolean mouseDown(Event evt, int x, int y) {
        nMouseX = x-nCrossOffsetX;
        nMouseY = y-nCrossOffsetY;
        return true;
    }

    /*---------------------------------------------------------------*/

    //Mouseup event
    public boolean mouseUp(Event evt, int x, int y) {
        bBufferDirty = true;

        //Thread monitor stuff for debugging threads
        //Use Control and rectangle region to toggle on/off
        /*if ((evt.modifiers & Event.CTRL_MASK) != 0 && rectThreadMonitor.inside(evt.x, evt.y)) {
            System.out.println("User fired off Threadmon");
            //bIsThrOn = !bIsThrOn;
            bIsThrOn = true;
            startThreadMonitor();
        }*/

         //Aarons Cheating mechanism...kewl huh??
         //Now uses the Control key...
         if (bAllImagesLoaded) {
             if (!bPuzzleFinished && !bSetFinished) {
                if ((evt.modifiers & Event.CTRL_MASK) != 0 && (evt.modifiers & Event.SHIFT_MASK) != 0 &&
                 polyQuickSolve.inside(evt.x, evt.y)) {
                    quickSolver();
                }
             }
         }


        //Back to puzzle land button
        if ((bBackButtonOn) && (rectQXBACK_BUTTON.inside(evt.x, evt.y) || rectTXBACK_BUTTON.inside(evt.x, evt.y) || rectJXBACK_BUTTON.inside(evt.x, evt.y))) {
            returnToPuzzleLand();
	        return true;
        }

        //if puzzle is finished...eat the event
        if (!bSetFinished) {

            //Check that the mouse event occurred within our specified rectangle
            if(rectCrossWord.inside(x, y)) {

                //If the individual puzzle has finished...eat the event
                if (!bPuzzleFinished) {

                    //Exception handling added as an ArrayIndexOutOfBoundException occurs
                    Square sqSelSquare = sqPuzzleSquares[(x - nCrossOffsetX)/nSquareWidth][(y - nCrossOffsetY)/nSquareHeight];
                    try {
                        if (sqSelSquare.bIsCharAllowed){

                            //clear current highlights
                            sqCurrentSquare.getClueAnswerRef(bIsAcross).HighlightSquares(sqCurrentSquare, false);

                            //Deselect the listbox based on direction
                            if(!bIsAcross)
                                lstClueDown.deselect(lstClueDown.getSelectedIndex());
                            else
                                lstClueAcross.deselect(lstClueAcross.getSelectedIndex());

                            //test if same sq and flip if possible
                            if (sqSelSquare == sqCurrentSquare){
                                if (sqSelSquare.CanFlipDirection(bIsAcross))
                                    bIsAcross = !bIsAcross;
                            }
                            else
                                if ((bIsAcross) && (sqSelSquare.clAcross == null))
                                    bIsAcross = !bIsAcross;
                                else if ((!bIsAcross) && (sqSelSquare.clDown == null))
                                    bIsAcross = !bIsAcross;

                            //set new current sq & highlight them
                            sqCurrentSquare = sqSelSquare;
                            sqCurrentSquare.getClueAnswerRef(bIsAcross).HighlightSquares(sqCurrentSquare, true);

                            //Force a repaint
                            paint(getGraphics());

                            //Find index to Clue Answer for highlighting in List boxes
                            ClueAnswer tmpClueAnswer = sqSelSquare.getClueAnswerRef(bIsAcross);
                            int ClueAnswerIdx = 0;
                            for (int k = 0; k < nNumQuestions; k++){
                                if (tmpClueAnswer == caPuzzleClueAnswers[k]){
                                    ClueAnswerIdx = k;
                                    break;
                                }
                            }

                            //Selects the item in the list box relative to ClueAnswer and direction
                            if(bIsAcross)
                                lstClueAcross.select(ClueAnswerIdx);
                            else
                                lstClueDown.select(ClueAnswerIdx - lstClueAcross.countItems());

                        }
                        return true;
                    }

                    catch (Exception e) {
                        //Catch the exception
                        System.out.println("Exception " + e + " occurred in method mouseUp");
                    }
               }

            }

            ////////////////////////////////////////////////////////
            //QuickCrossword
            //
            if(szPuzzleType.equals("QX")){

                //If all images are not loaded.. eat the event
                if (bAllImagesLoaded) {

                    if (!bPuzzleFinished && !bSetFinished) {
                        if(polyQXCheck.inside(evt.x, evt.y)){   //Check Words clicked
                            clickedBtnCheck();
                            return true;
                        }
                        if(polyQXClear.inside(evt.x, evt.y)){   //Clear Word clicked
                            clickedBtnClear();
                            return true;
                        }
                    }

                    if(polyQXNext.inside(evt.x, evt.y)){    //Next puzzle clicked

                        //Flag that next was pressed
                        bNextPressed = true;

                        //if(nPuzzleId != Integer.parseInt(szLastPuzzleID.substring(2,8)))
                        if(bMorePuzzles)
                            getNextPuzzle();
                        else {
                            endPuzzleSequence();
                            /*nBubbleOut = 6;
                            bNewBackFlush = true;
                            bIsFinished = true;
                            bPuzSetFinished = true;
                            bBackButtonOn = false;
                            buildBackBuffer();
                            paint(getGraphics());

                            try{
                                thMainThread.sleep(14300);
                            }catch(InterruptedException ie){}


                            nBubbleOut = -99;
                            bNewBackFlush = true;
                            bIsFinished = true;
                            bPuzSetFinished = true;
                            bBackButtonOn = true;
                            buildBackBuffer();
                            paint(getGraphics());*/

                        }

                        return true;
                    }

                    //If both the puzzle is finished and the puzzle set is finished..disable the hints
                    if (!bPuzzleFinished && !bSetFinished) {

                        //Inside polygon region for Get Letters
                        if (polyQXCliff.inside(evt.x, evt.y)){  //Character clicked

                            //Next Puzzle is not ready...and Hint is clicked clear the bubble.
                            if(evt.when >= lLastHint){
                                if ((!bIsNextPuzzleReady)){
                                	nBubbleOut = -99;
                                    bBufferDirty = true;
                                    buildBackBuffer();
                                    repaint();
                                }
                            }

                            //Check when the evt time stamp occurred
                            if(evt.when >= lLastHint){
                                updateUserHint();
                                clickedBtnGetLetter(0);

                                if(nCurrHintBubble < 5)
                                    nCurrHintBubble++;

                                nBubbleOut = nCurrHintBubble;
                                paint(getGraphics());

                                if(nBubbleOut == 3){
                                    try{
                                        thMainThread.sleep(2200);
                                        lLastHint = evt.when + 2200;
                                    }catch(InterruptedException ie){}
                                }
                                else if(nBubbleOut == 4){
                                    try{
                                        thMainThread.sleep(3500);
                                        lLastHint = evt.when + 3500;
                                    }catch(InterruptedException ie){}
                                }
                                else if(nBubbleOut == 5){
                                    try{
                                        thMainThread.sleep(3000);
                                        lLastHint = evt.when + 3000;
                                    }catch(InterruptedException ie){}
                                }

                                nBubbleOut = -99;
                                buildBackBuffer();
                                paint(getGraphics());
                            }

                            return true;
                        }
                    }
                }
                return true;

            }

            ////////////////////////////////////////////////////////
            //TVCrossword
            //
            if(szPuzzleType.equals("TX")) {

                 //If all images are not loaded.. eat the event
                 if (bAllImagesLoaded) {

                    //Inside polygon region for Next
                    if(polyTXNext.inside(evt.x, evt.y)){

                        //Flag that next was pressed
                        bNextPressed = true;

                        //if(nPuzzleId != Integer.parseInt(szLastPuzzleID.substring(2,8)))
                        if(bMorePuzzles)
                            getNextPuzzle();
                        else {
                            endPuzzleSequence();
                            /*nBubbleOut = 5;
                            bNewBackFlush = true;
                            bIsFinished = true;
                            bPuzSetFinished = true;
                            bBackButtonOn = false;
                            buildBackBuffer();
                            paint(getGraphics());

                            try{
                                thMainThread.sleep(18800);
                            }catch(InterruptedException ie){}

                            nBubbleOut = -99;
                            bNewBackFlush = true;
                            bIsFinished = true;
                            bPuzSetFinished = true;
                            bBackButtonOn = true;
                            buildBackBuffer();
                            paint(getGraphics());*/
                        }

                        return true;
                    }

                    if (!bPuzzleFinished && !bSetFinished) {
                        //Inside polygon region for Check words
                        if(polyTXCheck.inside(evt.x, evt.y)){
                            clickedBtnCheck();
                            return true;
                        }
                    }

                    //If both the puzzle is finished and the puzzle set is finished..disable the hints
                    if (!bPuzzleFinished && !bSetFinished) {

                        //Inside polygon region for Get Letters
                        if(polyTXRay.inside(evt.x, evt.y)){

                            //Next Puzzle is not ready...and Hint is clicked clear the bubble.
                            if(evt.when >= lLastHint){
                                if ((!bIsNextPuzzleReady)){
                                	nBubbleOut = -99;
                                    bBufferDirty = true;
                                    buildBackBuffer();
                                    repaint();
                                }
                            }

                            //Check when the evt time stamp occurred
                            if(evt.when >= lLastHint){
                                updateUserHint();
                                clickedBtnGetLetter(0);
                                if(nCurrHintBubble == 4) {
                                    nCurrHintBubble = 4;
                                }
                                else {
                                    nCurrHintBubble++;
                                }

                                nBubbleOut = nCurrHintBubble;

                                paint(getGraphics());

                                if(nBubbleOut == 2){
                                    try{
                                        thMainThread.sleep(3500);
                                        lLastHint = evt.when + 3500;
                                    }catch(InterruptedException ie){}
                                }
                                else if(nBubbleOut == 3){
                                    try{
                                        thMainThread.sleep(3500);
                                        lLastHint = evt.when + 3500;
                                    }catch(InterruptedException ie){}
                                }
                                else if(nBubbleOut == 4){
                                    try{
                                        thMainThread.sleep(3500);
                                        lLastHint = evt.when + 3500;
                                    }catch(InterruptedException ie){}
                                }

                                nBubbleOut = -99;
                                buildBackBuffer();
                                paint(getGraphics());
                            }

                           return true;
                        }
                    }

                    //Inside polygon region for Clear word
                    if (!bPuzzleFinished && !bSetFinished) {
                        if(polyTXClear.inside(evt.x, evt.y)){
                            clickedBtnClear();
                            return true;
                        }
                    }

                }

                return true;
            }

            ///////////////////////////////////////////////////
            //JuniorCrossword
            if(szPuzzleType.equals("JX")) {

                //If all images are not loaded.. eat the event
                if (bAllImagesLoaded) {

                    //Inside polygon region for Next
                    if(polyJXNext.inside(evt.x, evt.y)){

                        //Flag that next was pressed
                        bNextPressed = true;

                        //if(nPuzzleId != Integer.parseInt(szLastPuzzleID.substring(2,8)))
                        if(bMorePuzzles)
                            getNextPuzzle();
                        else {
                            endPuzzleSequence();

                            //Added 06/03/97 - AJS
                            /*lstClueAcross.hide();
                            lstClueDown.hide();
                            buildBackBuffer();
                            paint(getGraphics());

                            nBubbleOut = 5;
                            bNewBackFlush = true;
                            bIsFinished = true;
                            bPuzSetFinished = true;
                            bBackButtonOn = false;
                            buildBackBuffer();
                            paint(getGraphics());

                            try{
                                thMainThread.sleep(15400);
                            }catch(InterruptedException ie){}

                            nBubbleOut = -99;
                            bNewBackFlush = true;
                            bIsFinished = true;
                            bPuzSetFinished = true;
                            bBackButtonOn = true;
                            buildBackBuffer();
                            paint(getGraphics());*/
                        }

                        return true;
                    }

                    //Inside polygon region for Check words
                    /*if(polyJXCheck.inside(evt.x, evt.y)){
                        //Do nothing!!
                        //clickedBtnCheck();
                        return true;
                    }*/

                    //If both the puzzle is finished and the puzzle set is finished..disable the hints
                    if (!bPuzzleFinished && !bSetFinished) {

                        //Inside polygon region for Get Letters
                        if(polyJXBilby.inside(evt.x, evt.y)){

                            //Next Puzzle is not ready...and Hint is clicked clear the bubble.
                            if(evt.when >= lLastHint){
                                if ((!bIsNextPuzzleReady)){
                                	nBubbleOut = -99;
                                    bBufferDirty = true;
                                    buildBackBuffer();
                                    repaint();
                                }
                            }

                            //If all images are not loaded.. eat the event
                            if(bAllImagesLoaded){

                                //Check when the evt time stamp occurred
                                if(evt.when >= lLastHint){

                                    updateUserHint();
                                    clickedBtnGetLetter(0);
                                    if(nCurrHintBubble == 4) {
                                        nCurrHintBubble = 4; //2;
                                    }
                                    else {
                                        nCurrHintBubble++;
                                    }

                                    nBubbleOut = nCurrHintBubble;

                                    paint(getGraphics());

                                    if(nBubbleOut == 2){
                                        try{
                                            thMainThread.sleep(2500);
                                            lLastHint = evt.when + 2500;
                                        }catch(InterruptedException ie){}
                                    }
                                    else if(nBubbleOut == 3){
                                        try{
                                            thMainThread.sleep(2000);
                                            lLastHint = evt.when + 2000;
                                        }catch(InterruptedException ie){}
                                    }
                                    else if(nBubbleOut == 4){
                                        try{
                                            thMainThread.sleep(4000);
                                            lLastHint = evt.when + 4000;
                                        }catch(InterruptedException ie){}
                                    }

                                    nBubbleOut = -99;
                                    buildBackBuffer();
                                    paint(getGraphics());
                                }
                            }
                            return true;
                        }
                    }

                    //Inside polygon region for Clear word
                    if (!bPuzzleFinished && !bSetFinished) {
                        if(polyJXClear.inside(evt.x, evt.y)){
                            clickedBtnClear();
                            return true;
                        }
                    }
                }
            }


            //
            ///////////////////////////////////////////////////
        }

         return true;
    }

    /*---------------------------------------------------------------*/

    public boolean mouseMoveThis(Event evt){

        //Puzzle status info
        ////////////////////////////////////////////////////////
        //QuickCrossword
        //
        if(szPuzzleType.equals("QX")){
            if(polyQXCheck.inside(evt.x, evt.y)){   //Check Words
                this.showStatus("Not sure heah? Click here to check all entered words.");
                return true;
            }
            if(polyQXClear.inside(evt.x, evt.y)){   //Clear Word
                this.showStatus("Which dictionary are you using? Click here to clear your word and try again.");
                return true;
            }
            if(polyQXNext.inside(evt.x, evt.y)){    //Next puzzle
                this.showStatus("Have you finished or are you just giving up? Click here to go to the next crossword.");
                return true;
            }
            if (polyQXCliff.inside(evt.x, evt.y)){  //Character
                this.showStatus("Okay so you need a hint or some help - just click on me and I'll see what I can do.");
	            return true;

            }
            if((bBackButtonOn) && (rectQXBACK_BUTTON.inside(evt.x, evt.y))){
                this.showStatus("Now that you've finished the set click here to go back to Puzzle Land.");
                return true;
            }
            else {
                 this.showStatus("Here's a quick crossword the operative word being quick. Just completing the puzzle is not enough - time waits for no puzzler.");
	             return true;
            }
        }

        ////////////////////////////////////////////////////////
        //TVCrossword
        //
        if(szPuzzleType.equals("TX")){

            //Inside polygon region for Next
            if(polyTXNext.inside(evt.x, evt.y)){
               this.showStatus("Ready for another one - well done darling. Click here for the next crossword");
               return true;
            }

            //Inside polygon region for Check words
            if(polyTXCheck.inside(evt.x, evt.y)){
                this.showStatus("Click here and I'll check your answers.");
                return true;
            }

            //Inside polygon region for Get Letters
            if(polyTXRay.inside(evt.x, evt.y)){
                this.showStatus("Need a hand? click on me.");
                return true;
            }

            //Inside polygon region for Clear word
            if(polyTXClear.inside(evt.x, evt.y)){
                this.showStatus("Want to try another word - click here to clear the word and try again.");
                return true;
            }
            if((bBackButtonOn) && (rectTXBACK_BUTTON.inside(evt.x, evt.y))){
                this.showStatus("Now that you've finished the set click here to go back to Puzzle Land.");
                return true;
            }
            else {
                this.showStatus("Here's a Crossword for TV and Movie buffs.");
	            return true;
            }
        }

        ///////////////////////////////////////////////////
        //JuniorCrossword
        if(szPuzzleType.equals("JX")){

            //Inside polygon region for Next
            if(polyJXNext.inside(evt.x, evt.y)){
                this.showStatus("Ready for another one - well done. Click here for the next crossword");
                return true;
            }

            //Inside polygon region for Check words
            /*if(polyJXCheck.inside(evt.x, evt.y)){
                this.showStatus("Click here and I'll check your answers.");
                return true;
            }*/

            //Inside polygon region for Get Letters
            if(polyJXBilby.inside(evt.x, evt.y)){
                this.showStatus("Hi I'm Bara Bilby if you need a hand? Click on me.");
                return true;
            }

            //Inside polygon region for Clear word
            if(polyJXClear.inside(evt.x, evt.y)){
                this.showStatus("Not happy with your answer - just click here to clear the word");
                return true;
            }

            if((bBackButtonOn) && (rectJXBACK_BUTTON.inside(evt.x, evt.y))){
                this.showStatus("Now that you've finished the set click here to go back to Puzzle Land.");
                return true;
            }

            else {
                this.showStatus("Here's a Crossword for you to try.");
                return true;
            }
        }

        return true;

    }

    /*---------------------------------------------------------------*/

    //Keyup event
    public boolean keyUp(Event evt, int key) {
        return true;
    }

    /*---------------------------------------------------------------*/

    //Keypress event - Display the character in the square region
    public boolean keyDown(Event evt, int key) {

           //if puzzle is finished...eat the event
        if (!bPuzzleFinished) {

            try {
                    //Spacebar pressed to change orientation...bIsAcross.
                    if (key == nSPACEBAR){

                        //Deselect the listbox based on direction
                        if(!bIsAcross)
                            lstClueDown.deselect(lstClueDown.getSelectedIndex());
                        else
                            lstClueAcross.deselect(lstClueAcross.getSelectedIndex());

                        //Sets the highlighting of the square.
                        sqCurrentSquare.getClueAnswerRef(bIsAcross).HighlightSquares(sqCurrentSquare, false);

                        //Change orientation if possible
                        if(bIsAcross){
                            if (sqCurrentSquare.CanFlipDirection(bIsAcross))
                                bIsAcross = false;
                        }
                        else{
                            if (sqCurrentSquare.CanFlipDirection(bIsAcross))
                	            bIsAcross = true;
                        }

                	    //Sets the highlighting of the square.
                        sqCurrentSquare.getClueAnswerRef(bIsAcross).HighlightSquares(sqCurrentSquare, true);

                    }

                    //Set the focus if the tab key is pressed
                    if (key == nTAB_KEY) {
                        if (nTabPress == 0){
                            //Give the Across list the focus
                            lstClueAcross.select(0);
                            selChangeLstClueAcross(evt);
                            lstClueAcross.requestFocus();
                            nTabPress = 1;
                            nFocusState = 1;
                        }

                        //Give the Down list the focus
                        else if (nTabPress == 1){
                            lstClueDown.select(0);
                            selChangeLstClueDown(evt);
                            lstClueDown.requestFocus();
                            nTabPress = 2;
                            nFocusState = 2;
                        }

                        //Give the applet back the focus
                        else if (nTabPress == 2){
                            this.requestFocus();
                            nTabPress = 0;
                            nFocusState = 0;
                        }
                    }

                    //Only allow list box navigation if they have the focus.
                    //Up and down arrows for the listbox navigation
                    if ((nFocusState == 1) || (nFocusState == 2)) {
                        NavigateList(bIsAcross, key, evt);
                    }


                    //If the applet has the focus then allow the arrow keys to navigate around
                    if (nFocusState == 0){
                        NavigatePuzzle(key, evt);
                    }


                    //Delete present square's contents if Delete key is pressed
                    if (key == nDELETE_KEY){
                        sqCurrentSquare.setLetter(' ', bIsAcross);
                        repaint();
                    }


                    //Check to see if a backspace was entered
                    if (key == nBACKSPACE_KEY){
                        sqCurrentSquare.setLetter(' ', bIsAcross);
                        sqCurrentSquare = sqCurrentSquare.getPrevsq(bIsAcross);
                        sqCurrentSquare.getClueAnswerRef(bIsAcross).HighlightSquares(sqCurrentSquare, true);
                        repaint();
                    }


                    //Check that the char falls into our range.
                    if ((key >= 'A' && key <= 'Z') || (key >= 'a' && key <= 'z')) {

                	    //Sets the letter in the current square
                	    sqCurrentSquare.setLetter(String.valueOf((char)key).toUpperCase().charAt(0), bIsAcross);

                	    //get next sq or myself(same sq)  if not available
                	    sqCurrentSquare = sqCurrentSquare.getNextsq(bIsAcross);

                        //Sets the highlighting of the square.
                        sqCurrentSquare.getClueAnswerRef(bIsAcross).HighlightSquares(sqCurrentSquare, true);

                        //If Jnr Crossword then update the score
                        if (szPuzzleType.equals("JX")){
                            if (!bSetFinished)
                                clickedBtnCheck();
                        }

                	    //Force a repaint
                	    repaint();
                    }

                }

                catch (Exception e) {

                    //Catch the exception
                    System.out.println("Exception " + e + " occurred in method keyDown");
                }

        }


        return true;
    }

    /*---------------------------------------------------------------*/

    //Allows up and down navigation of the listbox contents.
    private void NavigateList(boolean bIsAcross, int nKeyPressed, Event evt){

        try {
            if (bIsAcross) { //If Across then allow operations on the across list
                if (nKeyPressed == nUP_ARROW){
                    if (lstClueAcross.getSelectedIndex() != 0){
                        lstClueAcross.select(lstClueAcross.getSelectedIndex() - 1);
                        selChangeLstClueAcross(evt);
                    }
                }
                else if (nKeyPressed == nDOWN_ARROW){
                        lstClueAcross.select(lstClueAcross.getSelectedIndex() + 1);
                        selChangeLstClueAcross(evt);
                }

            }
            else if (!bIsAcross) { //if Down
                if (nKeyPressed == nUP_ARROW){
                    if (lstClueDown.getSelectedIndex() != 0){
                        lstClueDown.select(lstClueDown.getSelectedIndex() - 1);
                        selChangeLstClueDown(evt);
                    }
                }
                else if (nKeyPressed == nDOWN_ARROW){
                    lstClueDown.select(lstClueDown.getSelectedIndex() + 1);
                    selChangeLstClueDown(evt);
                }
            }
       }
       catch (Exception e) { //Catch the exception
            System.out.println("Exception " + e + " occurred in method NavigateList");
       }
    }

    /*---------------------------------------------------------------*/

    private void NavigatePuzzle(int nKeyPressed, Event evt){

        try {

            //Deselect the listbox based on direction
            if(!bIsAcross)
                lstClueDown.deselect(lstClueDown.getSelectedIndex());
            else
                lstClueAcross.deselect(lstClueAcross.getSelectedIndex());

            //Sets the highlighting of the square.
            sqCurrentSquare.getClueAnswerRef(bIsAcross).HighlightSquares(sqCurrentSquare, false);

            //If left arrow key pressed get prev sq
            if (nKeyPressed == nLEFT_ARROW) {
                if (bIsAcross)
                    sqCurrentSquare = sqCurrentSquare.getPrevsq(bIsAcross);
                else{
                    sqCurrentSquare = sqCurrentSquare.getPrevsq(!bIsAcross);
                    if(sqCurrentSquare.clDown == null)
                        bIsAcross = !bIsAcross;
                }
            }

            //If right arrow pressed get next sq
            else if (nKeyPressed == nRIGHT_ARROW) {
                if (bIsAcross)
                    sqCurrentSquare = sqCurrentSquare.getNextsq(bIsAcross);
                else{
                    sqCurrentSquare = sqCurrentSquare.getNextsq(!bIsAcross);
                    if(sqCurrentSquare.clDown == null)
                        bIsAcross = !bIsAcross;
                }
            }


            //If up arrow key pressed
            else if (nKeyPressed == nUP_ARROW) {
        	    if (bIsAcross){
                    sqCurrentSquare = sqCurrentSquare.getPrevsq(!bIsAcross);
                    if(sqCurrentSquare.clAcross == null){
                        bIsAcross = !bIsAcross;
                    }
                }
                else
                    sqCurrentSquare = sqCurrentSquare.getPrevsq(bIsAcross);
            }

              //If down arrow pressed get next sq
             else if (nKeyPressed == nDOWN_ARROW) {
                if (bIsAcross){
                    sqCurrentSquare = sqCurrentSquare.getNextsq(!bIsAcross);
                    if(sqCurrentSquare.clAcross == null){
                        bIsAcross = !bIsAcross;
                    }
                }
                else
                    sqCurrentSquare = sqCurrentSquare.getNextsq(bIsAcross);
            }

            //Sets the highlighting of the square.
            sqCurrentSquare.getClueAnswerRef(bIsAcross).HighlightSquares(sqCurrentSquare, true);

            ///////////////////////////////////////
            //Listbox linkage stuff
            //
            //Find index to Clue Answer for highlighting in List boxes
            ClueAnswer tmp = sqCurrentSquare.getClueAnswerRef(bIsAcross);
            int ClueAnswerIdx = 0;
            for (int k = 0; k < nNumQuestions; k++){
                if (tmp == caPuzzleClueAnswers[k]){
                    ClueAnswerIdx = k;
                    break;
                }
            }

            //Selects the item in the list box relative to the ClueAnswer
            //and the orientation.
            if(bIsAcross)
                lstClueAcross.select(ClueAnswerIdx);
            else
                lstClueDown.select(ClueAnswerIdx - lstClueAcross.countItems());
            ///////////////////////////////////////

    	    //Force a repaint
    	    repaint();

        }
        catch (Exception e) { //Catch the exception
            System.out.println("Exception " + e + " occurred in method NavigatePuzzle");
        }
    }

    /*---------------------------------------------------------------*/

    //Updates the crossword score
    private void updateCrosswordScore(){
        nScore = 0;
        for (int i=0;i<nNumQuestions; i++) {
            if (caPuzzleClueAnswers[i].isCorrect()){
                nScore++;
            }
        }

        buildBackBuffer();
        paint(getGraphics());
    }

    /*---------------------------------------------------------------*/

    //Updates the crossword hint count
    private void updateUserHint(){
        nUserHintPress++;
    }

    /*---------------------------------------------------------------*/

    //Draws the Across/Down listbox Headers
    private void drawListHeaders(Graphics g){

        //Quick
        if(szPuzzleType.equals("QX")){
            g.setFont(fntListhead);
            g.setColor(Color.black);
            g.drawString("Clues Across", ptTopAcross.x, ptTopAcross.y);
            g.drawString("Clues Down", ptTopDown.x, ptTopDown.y);
        }

        //TV
        else if(szPuzzleType.equals("TX")){ //TV
            g.setFont(fntListhead);
            g.setColor(Color.white);
            g.drawString("Clues Across", ptTopAcross.x, ptTopAcross.y);
            g.drawString("Clues Down", ptTopDown.x, ptTopDown.y);
        }

        //Junior
        else if(szPuzzleType.equals("JX")){
            g.setFont(fntListhead );
            g.setColor(Color.black);
            g.drawString("Clues Across", ptTopAcross.x, ptTopAcross.y);
            g.drawString("Clues Down", ptTopDown.x, ptTopDown.y);
        }

    }

    /*---------------------------------------------------------------*/

    //Draws the Crossword score
    private void drawCrosswordScore(Graphics g){

        //Quick
        if(szPuzzleType.equals("QX")){
            g.setFont(fntScore);
            g.setColor(Color.black);
            g.drawString("Your Score: " + String.valueOf(nScore), ptQXPlayerScoreLabel.x, ptQXPlayerScoreLabel.y);
            g.drawString("Max Score: " + String.valueOf(nNumQuestions), ptQXPlayerScore.x, ptQXPlayerScore.y);
        }

        //TV
        else if(szPuzzleType.equals("TX")){
            g.setFont(fntScore);
            g.setColor(Color.white);
            g.drawString("Your Score: " + String.valueOf(nScore), ptTXPlayerScoreLabel.x, ptTXPlayerScoreLabel.y);
            g.drawString("Max Score: " + String.valueOf(nNumQuestions), ptTXPlayerScore.x, ptTXPlayerScore.y);
        }

        //Junior
        else if(szPuzzleType.equals("JX")){
            g.setFont(fntScore);
            g.setColor(Color.red);
            g.drawString("Your Score: " + String.valueOf(nScore), ptJXPlayerScoreLabel.x, ptJXPlayerScoreLabel.y);
            g.drawString("Max Score: " + String.valueOf(nNumQuestions), ptJXPlayerScore.x, ptJXPlayerScore.y);
        }

    }

    /*---------------------------------------------------------------*/

    //Check words button event handler
    public void clickedBtnCheck() {

        //Increment the score if the answer is correct
        updateCrosswordScore();

        for(int i = 0; i<nNumQuestions; i++)
           caPuzzleClueAnswers[i].checkWord();

        //If the crossword score == the number of questions, then it is the end of the game
        if (nScore == nNumQuestions){

            //Flag that we have finished
            bPuzzleFinished = true;

			//Play the ending puzzle sound
			authEndPuz.playIt();

			//Delay to allow clapping au file to finish
            try{
                thMainThread.sleep(1800);
            }catch(InterruptedException ie){}

            //Update the puzzle stats
            setWebStats();

            //Added 10/03/97 - AJS
            //As per Jim's request
            endPuzzleSequence();

        }

        //Force a repaint
        repaint();

    }

    /*---------------------------------------------------------------*/

    //Gets letters from the data stream and puts them into the crossword
	//Hint implementation
    public void clickedBtnGetLetter(int nCount) {
        //Exception handling added 25/02/97
        try {

            //New hint implememtation - 20/02/97
            boolean bHintSupplied = false, bAllHintLettersChecked = false, bTmpResult;//, bFinished = false;

            while((!bHintSupplied)&&(!bAllHintLettersChecked)){
            //while(!bHintSupplied){
                if(szTmpGetLetters.length() > 0){
                    char chHintLetter = szTmpGetLetters.charAt(0);
                    szTmpGetLetters = szTmpGetLetters.substring(1);
                    for(int i = 0; i<nNumQuestions; i++) {
                        bTmpResult = caPuzzleClueAnswers[i].checkHint(chHintLetter);
                        if(bTmpResult) {
                            bHintSupplied = true;
                        }
                    }
                    nCount++;
                    if(nCount == szGetLetters.length())
                        bAllHintLettersChecked = true;
                }
                else
                {
                    szTmpGetLetters = szGetLetters;
                    clickedBtnGetLetter(nCount);
                    bHintSupplied = true;

                }
            }


            //If Jnr Crossword then update the score
            if (szPuzzleType.equals("JX")){
                clickedBtnCheck();
            }

            repaint();
        }
        catch (Exception e) { //Catch the exception
            System.out.println("Exception " + e + " occurred in method clickedBtnGetLetter()");
        }
    }

    /*---------------------------------------------------------------*/

    //Clear word button event handler
    public void clickedBtnClear() {

        //Clears the current word squares
        if(bIsAcross){
            sqCurrentSquare.clAcross.ResetWord(bIsAcross);
        }
        else{
            sqCurrentSquare.clDown.ResetWord(bIsAcross);
        }

        //Force a repaint
        repaint();
    }

    /*---------------------------------------------------------------*/

    //Event handler for the Across listbox
    private void selChangeLstClueAcross(Event ev) {
        sqCurrentSquare.getClueAnswerRef(bIsAcross).HighlightSquares(sqCurrentSquare, false);

        if (!bIsAcross){
            bIsAcross=true;
            lstClueDown.deselect(lstClueDown.getSelectedIndex());
        }
        sqCurrentSquare = caPuzzleClueAnswers[lstClueAcross.getSelectedIndex()].getSquare();
        caPuzzleClueAnswers[lstClueAcross.getSelectedIndex()].HighlightSquares(sqCurrentSquare, true);
        repaint();
    }

    /*---------------------------------------------------------------*/

    //Event handler for the Down listbox
    private void selChangeLstClueDown(Event ev) {
        sqCurrentSquare.getClueAnswerRef(bIsAcross).HighlightSquares(sqCurrentSquare, false);
        if (bIsAcross){
            bIsAcross=false;
            lstClueAcross.deselect(lstClueAcross.getSelectedIndex());
        }
        sqCurrentSquare = caPuzzleClueAnswers[lstClueAcross.countItems() + lstClueDown.getSelectedIndex()].getSquare();
        caPuzzleClueAnswers[lstClueAcross.countItems() + lstClueDown.getSelectedIndex()].HighlightSquares(sqCurrentSquare, true);
        repaint();
    }

    /*---------------------------------------------------------------*/

    //Returns back to the puzzleland URL page.
    private void returnToPuzzleLand() {
        URL urlPuzLand;
        try{
            //urlPuzLand = new URL("http://www.cyberpuzzles.aust.com/core/index.asp");
            urlPuzLand = new URL("http://www.cyberpuzzles.aust.com/core/index.asp");

            //Modified 17/10/97 to show in html frame context
            //getAppletContext().showDocument(urlPuzLand);
            getAppletContext().showDocument(urlPuzLand, "_self");
        }catch(MalformedURLException murle){}

    }

    /*---------------------------------------------------------------*/

    //Resize and position the Across/down listboxes
    public void initClueLists(FontMetrics fmListFontMets){
        int nMaxClueStringWidth = 0, nScrollBarWidth = 30, nOffsetY = 40;
        for(int nClueIdx = 0; nClueIdx < szClues.length ; nClueIdx++){
            if(fmListFontMets.stringWidth(nClueIdx + " .  " + szClues[nClueIdx]) > nMaxClueStringWidth){
                nMaxClueStringWidth = fmListFontMets.stringWidth(szClues[nClueIdx]) + fmListFontMets.stringWidth(nClueIdx + " .  ");
            }
        }

        if(szPuzzleType.equals("QX")){  //Quick crossword
            /*Rectangle nBigListRect = new Rectangle(nMAX_CROSS_WIDTH + 10, nOffsetY+10, nMaxClueStringWidth + nScrollBarWidth, nCrossOffsetY + nNumRows * nSquareHeight - nOffsetY + (2*nCROSS_BORDER_WIDTH) + 10);
            int nVertSize = (int)((nBigListRect.height - (2*nHORIZ_LIST_GAP))/2) - 7;
            lstClueAcross.reshape(nBigListRect.x, nBigListRect.y + nHORIZ_LIST_GAP, nMaxClueStringWidth + nScrollBarWidth, nVertSize);
            lstClueDown.reshape(nBigListRect.x, nBigListRect.y + (2*nHORIZ_LIST_GAP) + nVertSize, nMaxClueStringWidth + nScrollBarWidth, nVertSize);
            ptTopAcross = new Point(nMAX_CROSS_WIDTH + 10, lstClueAcross.bounds().y - 2);
            ptTopDown = new Point(nMAX_CROSS_WIDTH + 10, lstClueDown.bounds().y - 2);*/

            Rectangle nBigListRect = new Rectangle(nMAX_CROSS_WIDTH + 10, nOffsetY+10, nMaxClueStringWidth + nScrollBarWidth, nCrossOffsetY + nNumRows * nSquareHeight - nOffsetY + (2*nCROSS_BORDER_WIDTH) + 10);
            int nVertSize = (int)((nBigListRect.height - (2*nHORIZ_LIST_GAP))/2) - 7;
            lstClueAcross.reshape(nBigListRect.x, nBigListRect.y + nHORIZ_LIST_GAP, nMaxClueStringWidth + nScrollBarWidth, nVertSize);
            lstClueDown.reshape(nBigListRect.x, nBigListRect.y + (2*nHORIZ_LIST_GAP) + nVertSize, nMaxClueStringWidth + nScrollBarWidth, nVertSize);
            ptTopAcross = new Point(nMAX_CROSS_WIDTH + 10, lstClueAcross.bounds().y - 2);
            ptTopDown = new Point(nMAX_CROSS_WIDTH + 10, lstClueDown.bounds().y - 2);
        }
        else if(szPuzzleType.equals("TX")){ //TV crossword
            //Rectangle nBigListRect = new Rectangle(nMAX_CROSS_WIDTH + 10, nOffsetY+10, nMaxClueStringWidth + nScrollBarWidth, nCrossOffsetY + nNumRows * nSquareHeight - nOffsetY + (2*nCROSS_BORDER_WIDTH) + 10);
            //int nVertSize = (int)((nBigListRect.height - (8*nHORIZ_LIST_GAP))/2) - 7;
            //lstClueAcross.reshape(nBigListRect.x, 44 + nBigListRect.y + nHORIZ_LIST_GAP, nMaxClueStringWidth + nScrollBarWidth, nVertSize);
            //lstClueDown.reshape(nBigListRect.x, 44 + nBigListRect.y + (2*nHORIZ_LIST_GAP) + nVertSize, nMaxClueStringWidth + nScrollBarWidth, nVertSize);

            Rectangle nBigListRect = new Rectangle(nMAX_CROSS_WIDTH + 10, nOffsetY+10, this.bounds().width - (nMAX_CROSS_WIDTH + 15), nCrossOffsetY + nNumRows * nSquareHeight - nOffsetY + (2*nCROSS_BORDER_WIDTH));
            //hmmmmmmmmmmmmmmm.................fudge factor
            int nVertSize = (int)((nBigListRect.height - (2*nHORIZ_LIST_GAP))/2) - 7 - 30;

            lstClueAcross.reshape(nBigListRect.x, 34+  nBigListRect.y + nHORIZ_LIST_GAP, nBigListRect.width, nVertSize);
            lstClueDown.reshape(nBigListRect.x, 34 + nBigListRect.y + (2*nHORIZ_LIST_GAP) + nVertSize, nBigListRect.width, nVertSize);


            ptTopAcross = new Point(nMAX_CROSS_WIDTH + 10, lstClueAcross.bounds().y - 2);
            ptTopDown = new Point(nMAX_CROSS_WIDTH + 10, lstClueDown.bounds().y - 2);
        }
        else if(szPuzzleType.equals("JX")){ //Junior crossowrd
            Rectangle nBigListRect = new Rectangle(nMAX_CROSS_WIDTH + 10, nOffsetY+10, nMaxClueStringWidth + nScrollBarWidth, nCrossOffsetY + nNumRows * nSquareHeight - nOffsetY + (2*nCROSS_BORDER_WIDTH) + 10);
            int nVertSize = (int)((nBigListRect.height - (2*nHORIZ_LIST_GAP))/2)-7;
            lstClueAcross.reshape(nBigListRect.x + 6*nHORIZ_LIST_GAP, (nBigListRect.y + nHORIZ_LIST_GAP)+15, nMaxClueStringWidth + nScrollBarWidth, nVertSize);
            lstClueDown.reshape(nBigListRect.x + 6*nHORIZ_LIST_GAP, (nBigListRect.y + (2*nHORIZ_LIST_GAP) + nVertSize)+15, nMaxClueStringWidth + nScrollBarWidth, nVertSize);
            ptTopAcross = new Point(nMAX_CROSS_WIDTH  + 82, (lstClueAcross.bounds().y - 2));
            ptTopDown = new Point(nMAX_CROSS_WIDTH  + 82, (lstClueDown.bounds().y - 2));
        }
    }

    /*---------------------------------------------------------------*/

    //Applet destroy method
    public void destroy() {

        //Cleanup the audio threads..Check to see if they live
        if ((authIntro != null) && authIntro.isAlive())
            authIntro.cleanUp();
        if ((authHint1 != null) && authHint1.isAlive())
            authHint1.cleanUp();
        if ((authHint2 != null) && authHint2.isAlive())
            authHint2.cleanUp();
        if ((authHint3 != null) && authHint3.isAlive())
            authHint3.cleanUp();
        if ((authFinish != null) && authFinish.isAlive())
            authFinish.cleanUp();
        if ((authEndPuz != null) && authEndPuz.isAlive())
            authEndPuz.cleanUp();

        //CleanUp the data thread
        if ((dtCrosswordDataThread != null) && dtCrosswordDataThread.isAlive())
            dtCrosswordDataThread.cleanUp();

        //CleanUp the image threads for puzzle images
        if ((imthHiLiteSqu != null) && imthHiLiteSqu.isAlive())
            imthHiLiteSqu.cleanUp();
        if ((imthSquWord != null) && imthSquWord.isAlive())
            imthSquWord.cleanUp();
        if ((imthNormSqu != null) && imthNormSqu.isAlive())
            imthNormSqu.cleanUp();
        if ((imthBackground != null) && imthBackground.isAlive());
            imthBackground.cleanUp();
        if ((imthPuzzleBack != null) && imthPuzzleBack.isAlive())
            imthPuzzleBack.cleanUp();

        //CleanUp the image threads for balloon help
        if ((imthBubble1 != null) && imthBubble1.isAlive())
            imthBubble1.cleanUp();
        if ((imthBubble2 != null) && imthBubble2.isAlive())
            imthBubble2.cleanUp();
        if ((imthBubble3 != null) && imthBubble3.isAlive())
            imthBubble3.cleanUp();
        if ((imthBubble4 != null) && imthBubble4.isAlive())
            imthBubble4.cleanUp();
        if ((imthBubble5 != null) && imthBubble5.isAlive())
            imthBubble5.cleanUp();
        if ((imthBubble6 != null) && imthBubble6.isAlive())
            imthBubble6.cleanUp();

        //Kill the main thread
        if ((thMainThread != null) && thMainThread.isAlive()) {
            thMainThread.stop();
            thMainThread = null;
        }

        //Force Garbage collection
        System.gc();

    }

    /*---------------------------------------------------------------*/

    //Overload stop method
    public synchronized void stop(){

        //Stop the audio playing immediately
        try {
            authIntro.acAudio.stop();
            authHint1.acAudio.stop();
            authHint2.acAudio.stop();
            authHint3.acAudio.stop();
            authFinish.acAudio.stop();
            authEndPuz.acAudio.stop();
        }
        catch(Exception ie){
            System.out.println("Stopping the audio raised an exception");
        }


        //Call the destroy method
        destroy();

        //Stop the super class
        super.stop();
    }

    /*---------------------------------------------------------------*/

    //Init applet controls
    private void initControls(){

        //Creates a back buffer
        rectCrossWord = new Rectangle(nCrossOffsetX,nCrossOffsetY,nCrosswordWidth, nCrosswordHeight);

        //Get the FontMetrics of the main font
        Graphics g = getGraphics();
        fntmFont = g.getFontMetrics(fntFont);

        //Do some housekeeping.
        MrParser = null;
        System.gc();

        ////////////////////////////////////////////////////////
        //Now to handle each of the components
        //

        //Dynamically add and resize the listboxes
        lstClueAcross=new List();
        lstClueAcross.hide();
        add(lstClueAcross);
        lstClueDown=new List();
        lstClueDown.hide();
        add(lstClueDown);

        //Listbox fonts
        Font fntTmpFont = fntListFont;
        if(szPuzzleType.equals("QX")) //Quick
           fntTmpFont = fntListFont;
        else if(szPuzzleType.equals("TX")) //TV
           fntTmpFont = fntSmFormFont;
        else if(szPuzzleType.equals("JX")) //Jnr
           fntTmpFont = fntListFont;

        //Resize the listboxes based on the fontsize
        initClueLists(getFontMetrics(fntTmpFont));

        //Implicitly Set the list box font
        lstClueAcross.setFont(fntTmpFont);
        lstClueDown.setFont(fntTmpFont);

        /*---------------------------------------------------------------*/

        //Crossword Parsed Dataset
        //
        //Dimension array for crossword data
        udtDataSet = new nDatasetUDT[nNumQuestions];
        for (int i=0; i<nNumQuestions; i++)
            udtDataSet[i] = new nDatasetUDT(nRowRef[i], nColRef[i], szAnswers[i], szClues[i], bDataIsAcross[i], nQuesNum[i]);

        //Set points for Player Score
        if(szPuzzleType.equals("QX")){
            ptQXPlayerScoreLabel = new Point(nMAX_CROSS_WIDTH + 10*nCrosswordOffset+95, 2*nLISTHEIGHT + 10*nCrosswordOffset+65);
            ptQXPlayerScore = new Point(nMAX_CROSS_WIDTH + 10*nCrosswordOffset+95, 2*nLISTHEIGHT + 10*nCrosswordOffset + 80);
        }

        //TV
        else if(szPuzzleType.equals("TX")){
            g.setFont(fntScore);
            g.setColor(Color.white);
            ptTXPlayerScoreLabel = new Point(nMAX_CROSS_WIDTH+11, 2*nLISTHEIGHT + 10*nCrosswordOffset+93);
            ptTXPlayerScore = new Point(nMAX_CROSS_WIDTH+11, 2*nLISTHEIGHT + 10*nCrosswordOffset+106);
            g.drawString("Your Score: " + String.valueOf(nScore), ptTXPlayerScoreLabel.x, ptTXPlayerScoreLabel.y);
            g.drawString("Max Score: " + String.valueOf(nNumQuestions), ptTXPlayerScore.x, ptTXPlayerScore.y);
        }

        //Junior
        else if(szPuzzleType.equals("JX")){
            g.setFont(fntScore);
            g.setColor(Color.red);
            ptJXPlayerScoreLabel = new Point(nMAX_CROSS_WIDTH-90, 2*nLISTHEIGHT + 10*nCrosswordOffset+30);
            ptJXPlayerScore = new Point(nMAX_CROSS_WIDTH-90, 2*nLISTHEIGHT + 10*nCrosswordOffset + 50);
            g.drawString("Your Score: " + String.valueOf(nScore), ptJXPlayerScoreLabel.x, ptJXPlayerScoreLabel.y);
            g.drawString("Max Score: " + String.valueOf(nNumQuestions), ptJXPlayerScore.x, ptJXPlayerScore.y);
        }


    }

       /*---------------------------------------------------------------*/

	//Init puzzle data sets
    private void initPuzzleData(){

        //Parser class
        //Parser Implementation
        MrParser = new crosswordparser();

        //Wait till the thread has finished loading
        while(!dtCrosswordDataThread.bLoaded){
            try{
                thMainThread.sleep(100);
            }catch(InterruptedException ie){
                System.out.println("Sleep state for image retrieval thread threw Interrupted Exception");}
        }
        szPuzData = dtCrosswordDataThread.szBuffer;

        //Parse the datastream
        while(!MrParser.parseData(szPuzData));

        ///////////////////////////////////////////////////////////////////
        //Testing stuff
        //
        // Parse Quick Crossword data set
        //while(!MrParser.parseData("645*QX000000*0909*0 0 1 1#6 0 1 4#3 2 1 6#0 3 1 8#3 5 1 11#0 6 1 13#0 8 1 15#4 8 1 16#1 0 2 2#4 0 2 3#6 0 2 4#8 0 2 5#3 2 2 6#5 2 2 7#7 4 2 9#0 5 2 10#4 5 2 12#2 6 2 14*Bread maker#Skill#Receive#Calm#Real#Taunts#Apple _ _ _#Midday meal#Irritate#Wealthy#Queen,King,_ _ _#Ballet skirt#Book of maps#100 make a dollar#Conjuring#Cease#Prison room#Length of life*BAKER#ART#ACCEPT#SOOTHE#ACTUAL#TEASES#PIE#LUNCH#ANNOY#RICH#ACE#TUTU#ATLAS#CENTS#MAGIC#STOP#CELL#AGE*ABCEGHIKLMNOPRSTUY*30 1 1 0 1 5*Use the clues to solve this crossword and earn CyberSilver. If you have not played our crosswords before and want help, then click the HELP button. Have fun!"));
        //while(!MrParser.parseData("750*QX000003*0909*0 0 1 1#5 0 1 4#0 2 1 7#5 2 1 8#3 3 1 9#0 4 1 10#5 4 1 12#2 5 1 14#0 6 1 15#5 6 1 16#0 8 1 17#5 8 1 18#1 0 2 2#3 0 2 3#6 0 2 5#8 0 2 6#5 2 2 8#0 4 2 10#2 4 2 11#7 4 2 13*Young sheep#Fret#Another spelling of Cola#Qualified#Dividing structure#Stamp of authenticity#Prison room#Note#Above#Restore health#Fathers#After due time#Love#Person in a noisy quarrel#Piece of furniture#Skateboard roller#Intoxicating beverage#Reprove#Alter#Smallest*LAMB#STEW#KOLA#ABLE#WALL#SEAL#CELL#MEMO#OVER#HEAL#DADS#LATE#ADORE#BRAWLER#TABLE#WHEEL#ALCOHOL#SCOLD#AMEND#LEAST*LBCDEHKAMNORSTVW*30 1 1 0 1 5*Use the clues to solve this crossword and earn CyberSilver. If you have not played our crosswords before and want help, then click the HELP button. Have fun!"));

        //while(!MrParser.parseData("645*QX000000*1111*0 0 1 1#6 0 1 4#3 2 1 6#0 3 1 8#3 5 1 11#0 6 1 13#0 8 1 15#4 8 1 16#1 0 2 2#4 0 2 3#6 0 2 4#8 0 2 5#3 2 2 6#5 2 2 7#7 4 2 9#0 5 2 10#4 5 2 12#2 6 2 14*Bread maker#Skill#Receive#Calm#Real#Taunts#Apple _ _ _#Midday meal#Irritate#Wealthy#Queen,King,__ __ __#Ballet skirt#Book of maps#100 make a dollar#Conjuring#Cease#Prison room#Length of life*BAKER#ART#ACCEPT#SOOTHE#ACTUAL#TEASES#PIE#LUNCH#ANNOY#RICH#ACE#TUTU#ATLAS#CENTS#MAGIC#STOP#CELL#AGE*ABCEGHIKLMNOPRSTUY*30 1 1 0 1 5*Use the clues to solve this crossword and earn CyberSilver. If you have not played our crosswords before and want help, then click the HELP button. Have fun!"));

        // Parse TV Crossword data set
        //while(!MrParser.parseData("2542*TX000000*1515*0 0 1 1#6 0 1 3#0 2 1 8#11 2 1 9#0 4 1 11#9 5 1 14#4 6 1 17#0 7 1 19#10 7 1 21#6 8 1 23#0 9 1 24#6 10 1 26#0 12 1 30#5 12 1 31#0 14 1 32#10 14 1 33#0 0 2 1#4 0 2 2#7 0 2 4#9 0 2 5#11 0 2 6#14 0 2 7#13 2 2 10#2 4 2 12#6 4 2 13#14 5 2 15#0 6 2 16#8 6 2 18#3 7 2 20#10 7 2 21#12 7 2 22#1 9 2 25#7 10 2 27#14 10 2 28#5 11 2 29#0 12 2 30*Humphrey Bogart classic: 'The _ _ _ _ _ Mutiny'.#Actor in the series 'Skirts'.#Character played by Warren Mitchell in 15 down.#Actor in 'Dr Quinn:Medicine Woman': _ _ _ _ Allen.#Film about the mafia: 'The _ _ _ _ _ _ _ _ _'.#Occupation of one of the fathers in 'My Two Dads'.#Who starred as Mr Humphries in 'Are You Being Served?'. First name is John.#Richard Harris/Bo Derek feature: 'Orca/The Killer _ _ _ _ _'.#Sally Field feature about a woman with 16 personalities.#Character played by Luke Halpin in 'Flipper'. First name was Sandy.#At what game was Pat Cash very good?#Hosted the 'The Midday Show'.#Show with Robert Culp: 'The Greatest American _ _ _ _'.#'Home And Away' actor who played Shannon.#Character played by Woody Harrelson in the series 'Cheers'.#Talk show host: _ _ _ _ _ Winfrey.#Actor in 'Nanny'. (Last name)#Actor in 'How The West Was Won'. Last name is Saint.#He played Superman in the film of the same name. (Last name)#Clint Eastwood and Burt Reynolds film: '_ _ _ _ Heat'.#One of the puppets introduced by Edgar Bergen. First name was Charlie.#'General Hospital' character: _ _ _ Ashton.#Science fiction feature: 'Planet Of The _ _ _ _'.#Brent Spiner character in 'Star Trek'.#One of 'The Simpsons'. (First name)#Comedy series starring Warren Mitchell: '_ _ _ _ Death Us Do Part'.#Played Hot Lips Houlihan in 'M.A.S.H.'. (Last name)#'Full House' character: _ _ _ _ _ Katsopolis.#Occupation of Mr Roper in 'Three's Company'.#Mike Connors' cruise ship program.#Character played by Jack Kelly in 'Maverick'. (First name)#Singer: _ _ _ _ Adams.#Actor in 'Cheers'. First name is Kirstie.#American Civil War series:'_ _ _ _ _ And South'.#Surname ofthe Bee Gees.#Last name of the actor who played Roger Bannister in 'The Four Minute Mile'.*CAINE#TRACYMANN#ALFGARNETT#CHAD#GODFATHER#ARTIST#INMAN#WHALE#SYBIL#RICKS#TENNIS#RAYMARTIN#HERO#ISLAFISHER#WOODYBOYD#OPRAH#CRAIG#EVAMARIE#REEVE#CITY#MCCARTHY#NED#APES#DATA#HOMER#TILL#SWIT#NICKY#LANDLORD#SSCASINO#BART#EDIE#ALLEY#NORTH#GIBB#HUW*ABCDEFGHIKLMNOPRSTUVWY*30 1 1 0 1 5*Use the clues to solve this crossword and earn CyberSilver. If you have not played our crosswords before and want help, then click the HELP button. Have fun!"));
        //while(!MrParser.parseData("2617*TX000002*1515*5 0 1 3#10 0 1 5#0 1 1 8#7 2 1 9#0 3 1 11#7 4 1 13#0 5 1 14#6 6 1 17#0 7 1 19#10 7 1 20#4 8 1 21#9 9 1 22#0 10 1 24#6 11 1 26#4 12 1 29#9 13 1 30#0 14 1 31#6 14 1 32#0 0 2 1#2 0 2 2#5 0 2 3#7 0 2 4#10 0 2 5#12 0 2 6#14 0 2 7#8 2 2 10#3 3 2 12#0 5 2 14#2 5 2 15#4 5 2 16#6 6 2 17#14 6 2 18#9 9 2 22#11 9 2 23#0 10 2 24#7 10 2 25#12 11 2 27#14 11 2 28*Gil Gerard series: '_ _ _ _ Rogers In The 25th Century'.#Played a government agent in 'Get Smart'. (Last name)#Actor who starred in the film 'Star Wars'. (Last name)#Cartoon hero: _ _ _ _ Ant.#Actor in the series 'C.A.T.S. Eyes'.#She played Christine Francis in 'Hotel'. (Last name)#Was Cassie Barsby in 'Paradise Beach'. (Last name)#Jane Seymour series: 'Dr _ _ _ _ _: Medicine Woman'.#Was The Joker in the TV 'Batman'. (First name)#Talk show host. First name is Michael.#Was Amy Vining in 'General Hospital'. (First name)#Actor in the mini-series 'Roots'. First name was Vic.#Who played Carol Hathaway in 'E.R.'? (First name)#In 'The Last Resort', what was left to the daughters?#'Take 40 TV' host: _ _ _ _ Gaha.#Character in 'Police Rescue'. First name was Kathy.#Reg Varney series: 'On The _ _ _ _ _'.#Cartoon series : 'Quick _ _ _ _ McGraw'.#First name of the actor who played Sergeant Bilko.#Played Audrey Hardy in 21 across: Rachel _ _ _ _.#Actor in 'Baywatch': Yasmine _ _ _ _ _ _.#Movie with Michael Keaton: 'Working _ _ _ _ _ Man'.#In 'Happy Days', who played Al?#Damian Walshe Howling character in 'Blue Heelers'.#Character played by Jane Turner in 'Fast Forward'.#Police series set in Sun Hill Station.#Actor in the film 'Gorky Park'. Last name was Marvin.#Actor who played a misfit in 18 down. (First name)#Sherry Stringfield character in the 24 across series.#Roger Moore series: 'The _ _ _ _ _ _ _ _ _ _'.#Played Dolly in 'Certain Women': _ _ _ _ _ _ _ Ashton.#Film: 'One _ _ _ _ Over the Cuckoo's Nest'.#Robin Williams comedy: '_ _ _ _ _ _ On The Hudson'.#Nickname of Alf's daughter in 'Home And Away'.#Historian and TV personality: _ _ _ _ _ Bronowski.#He won an Emmy for 'Lou Grant': Ed _ _ _ _ _.#Actor in 'The Sweeney'. First name is John.#'Charlie's Angels' actor: Cheryl _ _ _ _.*BUCK#ADAMS#HAMILL#ATOM#LESLIEASH#SELLECCA#JOSEPH#QUINN#CESAR#ASPEL#SHELL#MORROW#JULIANNA#ISISHOTEL#EDEN#ORLAND#BUSES#DRAW#PHIL#AMES#BLEETH#CLASS#ALMOLINARO#ADAMCOOPER#SVETA#THEBILL#LEE#JACK#SUSANLEWIS#PERSUADERS#QUEENIE#FLEW#MOSCOW#ROO#JACOB#ASNER#THAW#LADD*AOTPERHISKLMNBDQFJCUVW*30 1 1 0 1 5*Use the clues to solve this crossword and earn CyberSilver. If you have not played our crosswords before and want help, then click the HELP button. Have fun!"));


        // Parse Junior Crossword data set
        //while(!MrParser.parseData("505*JX000000*0707*0 0 1 1#0 2 1 4#0 3 1 7#4 3 1 8#1 4 1 10#0 6 1 11#0 0 2 1#2 0 2 2#5 0 2 3#1 2 2 5#4 2 2 6#6 3 2 9*Try#Writing surface#Night bird#Truck#Opposite of male#Nearest#Car#Piece of furniture#Part of a flower#Terrible#Avoid#Unscramble XNET*ATTEMPT#TABLET#OWL#VAN#FEMALE#CLOSEST#AUTO#TABLE#PETAL#AWFUL#EVADE#NEXT*ABCDEFLMNOPSTUVWX*30 1 1 0 1 5*Use the clues to solve this crossword and earn CyberSilver. If you have not played our crosswords before and want help, then click the HELP button. Have fun!"));

        ///////////////////////////////////////////////////////////////////

        //PuzzleType
        szPuzzleType = MrParser.szPuzzleType;

        //Number of Columns
        nNumCols = MrParser.nNumCols;

        //Number of rows
        nNumRows = MrParser.nNumRows;

        //Num Across
        nNumAcross = MrParser.nNumAcross;

        //Num Down
        nNumDown = MrParser.nNumDown;

        //Puzzle ID
        nPuzzleID = MrParser.nPuzzleId;

        //Number of questions
        nNumQuestions = MrParser.nNumQuestions;

        //Declare dimensions for arrays of crossword data
        szClues = new String[nNumQuestions];
        szAnswers = new String[nNumQuestions];
        nColRef = new int[nNumQuestions];
        nRowRef = new int[nNumQuestions];
        bDataIsAcross = new boolean[nNumQuestions];
        nQuesNum = new int[nNumQuestions];
    }

    /*---------------------------------------------------------------*/

    //Init puzzle data from parser
	private void initData(){

        //Initialise arrays of crossword data
        for(int i = 0; i<nNumQuestions; i++){
            nColRef[i] = MrParser.nColRef[i];
            nRowRef[i] = MrParser.nRowRef[i];
            if(MrParser.nIsAcross[i] == 1)
                bDataIsAcross[i] = true;
            else if(MrParser.nIsAcross[i] == 2)
                bDataIsAcross[i] = false;
            nQuesNum[i] = MrParser.nQuesNum[i];
            szClues[i] = MrParser.szClues[i];
            szAnswers[i] = MrParser.szAnswers[i];
        }

        //Initialise Cybersilver costs
        for (int i=0; i<6; i++){
            nCosts[i] = MrParser.nCosts[i];
        }

        //Initialise Hint letters
        szGetLetters = MrParser.szGetLetters;
        szTmpGetLetters = MrParser.szGetLetters;

        //Initialise Blurb
        szBlurb = MrParser.szBlurb;

        ///////////////////////////////////////////////////////////////////
        //
        //Create instances of the fonts and set square height and widths.
        //
        //TV Crossword
        if(szPuzzleType.equals("TX")){
            fntnumFont = new Font("Helvetica", Font.BOLD, 7);
            fntFont = new Font("Helvetica", Font.BOLD, 12);
            nSquareWidth = nSquareHeight = 18;
            fntScore = new Font("Helvetica", Font.BOLD, 12);
            fntListhead = new Font("Helvetica", Font.BOLD, 12);
            fntSmFormFont = new Font("Helvetica", Font.PLAIN, 12);
            fntLgFormFont = new Font("Dialog",Font.PLAIN,16);
            fntListFont = new Font("Helvetica", Font.PLAIN, 12);
            nBubbleOut = -99;
            nCurrHintBubble = 1;
        }

        //Junior Crossword
        else if(szPuzzleType.equals("JX")){
            fntnumFont = new Font("Helvetica", Font.BOLD, 8);
            fntFont = new Font("Helvetica", Font.BOLD, 16);
            nSquareWidth = nSquareHeight = 25;
            fntScore = new Font("Helvetica", Font.BOLD, 12);
            fntListhead = new Font("Helvetica", Font.BOLD, 11);
            fntSmFormFont = new Font("Dialog",Font.PLAIN,9);
            fntLgFormFont = new Font("Dialog",Font.BOLD,16);
            fntListFont = new Font("Helvetica", Font.PLAIN, 12);
            nBubbleOut = -99;
            nCurrHintBubble = 1;
        }

        //Quick Crossword
        else if(szPuzzleType.equals("QX")){
            fntnumFont = new Font("Helvetica", Font.BOLD, 8);
            fntFont = new Font("Helvetica", Font.BOLD, 16);
            //nSquareWidth = nSquareHeight = 25;
            nSquareWidth = nSquareHeight = 27;
            fntScore = new Font("Helvetica", Font.BOLD, 12);
            fntListhead = new Font("Helvetica", Font.BOLD, 11);
            fntSmFormFont = new Font("Dialog",Font.PLAIN,9);
            fntLgFormFont = new Font("Dialog",Font.BOLD,16);
            fntListFont = new Font("Helvetica", Font.PLAIN, 11);
            nBubbleOut = -99;
            nCurrHintBubble = 2;
        }

        //
        ///////////////////////////////////////////////////////////////////

        //Initialise dimension variables
        nCrosswordWidth = nNumCols * nSquareWidth;
        nCrosswordHeight= nNumRows * nSquareHeight;

        //Crossword X/Y Offsets
        if(szPuzzleType.equals("QX")){ //Quick
            nCrossOffsetX = (int)(nMAX_CROSS_WIDTH/2) - (int)((nCrosswordWidth +(2*nCROSS_BORDER_WIDTH))/2);
            nCrossOffsetY = (int)(nMAX_CROSS_HEIGHT/2) - (int)((nCrosswordHeight +(2*nCROSS_BORDER_WIDTH))/2);
        }
        else if(szPuzzleType.equals("TX")){ //Tv
            nCrossOffsetX = (int)(nMAX_CROSS_WIDTH/2) - (int)((nCrosswordWidth +(2*nCROSS_BORDER_WIDTH))/2);
            nCrossOffsetY = 20 + (int)(nMAX_CROSS_HEIGHT/2) - (int)((nCrosswordHeight +(2*nCROSS_BORDER_WIDTH))/2)+50;

        }
        else if(szPuzzleType.equals("JX")){ //Junior
            nCrossOffsetX = ((int)(nMAX_CROSS_WIDTH/2) - (int)((nCrosswordWidth +(2*nCROSS_BORDER_WIDTH))/2))+130;
            nCrossOffsetY = (int)(nMAX_CROSS_HEIGHT/2) - (int)((nCrosswordHeight +(2*nCROSS_BORDER_WIDTH))/2)+20;
        }
    }

    /*---------------------------------------------------------------*/

    //Cheater Implementation
    private void quickSolver() {
        try {

             for(int p = 0; p<nNumQuestions; p++) {
                    for(int j = 0; j<nNumQuestions; j++) {
                        if(szTmpGetLetters.length() > 0){
                            char chHintLetter = szTmpGetLetters.charAt(0);
                            szTmpGetLetters = szTmpGetLetters.substring(1);
                            for(int i = 0; i<nNumQuestions; i++)
                                caPuzzleClueAnswers[i].checkHint(chHintLetter);
                        }
                    }
             }



            //Increment the score if the answer is correct
            updateCrosswordScore();

            for(int i = 0; i<nNumQuestions; i++)
               caPuzzleClueAnswers[i].checkWord();

            //If the crossword score == the number of questions, then it is the end of the game
            if (nScore == nNumQuestions){

                //Flag that we have finished
                bPuzzleFinished = true;

                //Record stats
                //case 1 - Puzzle completion

                authEndPuz.playIt();

    			//Delay to allow clapping au file to finish
                try{
                    thMainThread.sleep(900);
                }catch(InterruptedException ie){}


                //Update the puzzle stats
                setWebStats();
            }
            repaint();

        }
        catch (Exception e) { //Catch the exception
            System.out.println("Exception " + e + " occurred in method quickSolver()");
        }
    }

    /*---------------------------------------------------------------*/

    //Ending puzzle sequence.
    private void endPuzzleSequence() {
        if (bAllImagesLoaded) {
            if (!bMorePuzzles) {

                ///////////////////////////////////////////////////
                //Quickcrossword
                if(szPuzzleType.equals("QX")){
    			    nBubbleOut = 6;
                    bNewBackFlush = true;
                    bPuzzleFinished = true;
                    bSetFinished = true;
                    bBackButtonOn = false;
                    buildBackBuffer();
                    paint(getGraphics());

                    try{
                        thMainThread.sleep(14300);
                    }catch(InterruptedException ie){}


                    nBubbleOut = -99;
                    bNewBackFlush = true;
                    bPuzzleFinished = true;
                    bSetFinished = true;
                    bBackButtonOn = true;
                    buildBackBuffer();
                    paint(getGraphics());
                }

                ///////////////////////////////////////////////////
                //TV crossword
                if(szPuzzleType.equals("TX")) {
    			    nBubbleOut = 5;
                    bNewBackFlush = true;
                    bPuzzleFinished = true;
                    bSetFinished = true;
                    bBackButtonOn = false;
                    buildBackBuffer();
                    paint(getGraphics());

                    try{
                        thMainThread.sleep(18800);
                    }catch(InterruptedException ie){}

                    nBubbleOut = -99;
                    bNewBackFlush = true;
                    bPuzzleFinished = true;
                    bSetFinished = true;
                    bBackButtonOn = true;
                    buildBackBuffer();
                    paint(getGraphics());

                }

                ///////////////////////////////////////////////////
                //Junior crossword
                if(szPuzzleType.equals("JX")) {
    			    //Added 06/03/97 - AJS
                    lstClueAcross.hide();
                    lstClueDown.hide();
                    buildBackBuffer();
                    paint(getGraphics());

                    nBubbleOut = 5;
                    bNewBackFlush = true;
                    bPuzzleFinished = true;
                    bSetFinished = true;
                    bBackButtonOn = false;
                    buildBackBuffer();
                    paint(getGraphics());

                    try{
                        thMainThread.sleep(15400);
                    }catch(InterruptedException ie){}

                    nBubbleOut = -99;
                    bNewBackFlush = true;
                    bPuzzleFinished = true;
                    bSetFinished = true;
                    bBackButtonOn = true;
                    buildBackBuffer();
                    paint(getGraphics());

                }
            }

        }

     }

    /*---------------------------------------------------------------*/

     //Starts the Thread Monitor diagnostic classes
    /*private void startThreadMonitor(){
        try {
            Applet tm = getAppletContext().getApplet("ThreadDude");

            if (tm != null) {
                if (tm instanceof ThreadMon) {
                    ((ThreadMon)tm).ThMoninit(bIsThrOn);
                }
            }

        } catch (Exception e) {
            System.out.println("Exception in startThreadMonitor() " + e);
        }
    }*/

    }

    /*---------------------------------------------------------------*/

    //Build Dataset
    class nDatasetUDT {

        int nCoordAcross = 0;
        int nCoordDown = 0;

        //Answer
        String szAnswer = null;

        //Clue
        String szClue = null;


        //Is Across or down??
        boolean IsAcross = false;

        //Question number
        int nQuestionNum = 0;

        //default constructor
        public nDatasetUDT(){
        }

        //Dataset Constructor
        public nDatasetUDT(int nCoordAcross,int nCoordDown,String szAnswer,String szClue,
                            boolean IsAcross,int nQuestionNum){

            this.nCoordAcross = nCoordAcross;
            this.nCoordDown = nCoordDown;
            this.szAnswer = szAnswer;
            this.szClue = szClue;
            this.IsAcross = IsAcross;
            this.nQuestionNum = nQuestionNum;

        }

    }

    /*---------------------------------------------------------------*/
