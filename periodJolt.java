import java.applet.Applet;
import java.util.Date;

// -- object just sleeps for 10 seconds
public class periodJolt extends Thread {

    private static final long TIMEOUT = 30000;   //milli-sec

    pulse ptrParent = null;     //Applet can't be passed to ref a generic
                                //obj as java is too strongly Type Checked

    private boolean loaded = false;
    private long lPeriod = 60000;   //default ms
    private long lSumofDeltas = 0;

    //private Date dtCreated = null;

    private long lBaseLine = 0;

    public periodJolt(pulse obj, long prd) {
        if (obj.getDebugMode()) System.out.println("periodJolt constructor");

        ptrParent = obj;
        lPeriod = prd;

        //dtCreated = new Date();
        lBaseLine = new Date().getTime();
        lSumofDeltas = 0;
        //System.out.println("lBaseLine = " + lBaseLine);

        //objParent.stop();
    }

    public void run() {
        if (ptrParent.getDebugMode()) System.out.println("periodJolt run()");
        loaded = true;

        try {
            sleep(30000);
        } catch (InterruptedException ie) {
            System.out.println("InteruptedException sleeping: " + ie);
        }
        //ptrParent.pullTrigger((int)(12), this);
        lSumofDeltas = (new Date().getTime()) - lBaseLine;
        ptrParent.pullTrigger((int)(lSumofDeltas/1000), 0);    //, this);

        while (loaded && (ptrParent != null)) {
            //System.out.println("this thread's date is: " + dtCreated);
            if (ptrParent.getDebugMode()) System.out.println("epoch delta = " + ((new Date().getTime()) - lBaseLine));

            try {
                sleep(lPeriod);
            } catch (InterruptedException ie) {
                System.out.println("InteruptedException sleeping: " + ie);
            }
            lSumofDeltas = (new Date().getTime()) - lBaseLine;

            // -- tell applet time is up
            ptrParent.pullTrigger((int)(lPeriod/1000), 0);     //, this);

            /*
            // --
            try {
                sleep(TIMEOUT);
            } catch (InterruptedException ie) {
                System.out.println("InteruptedException sleeping: " + ie);
            }
            if (!this.interrupted()) {
                System.out.println("Timed out");
                ptrParent.doTimeout();
            } else {
                System.out.println("Did not time out");
            }
            */
        }
    }

    public int getDeficitDiff() {
        return (int)((((new Date().getTime()) - lBaseLine) - lSumofDeltas)/1000);
    }

    public int getDuration() {
        return (int)(((new Date().getTime()) - lBaseLine)/1000);
    }


    public void destroy() {
        if (ptrParent.getDebugMode()) System.out.println("periodJolt destroy()");
        loaded = false;
        super.destroy();
    }
}