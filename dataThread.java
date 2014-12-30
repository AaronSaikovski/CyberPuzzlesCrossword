//  imageThread class file
//  Author   : Bryan Richards
//  Company  : ObjectCentric
//  Date     : 9/12/96
//  Modified : 24/1/97

// Creates a thread of execution for an Applet to provide an asynchronous
// method for requesting and collecting image files via URL. Applet can check
// current status via the public boolean flag variable bLoaded.

import java.applet.*;
import java.net.*;
import java.io.*;
import java.lang.Thread;

public class dataThread extends Thread{

    Applet aApplet = null;
    Socket sSocket = null;
    DataOutputStream dosOutStream = null;
    DataInputStream  disInStream  = null;
    String szBuffer = null, szFileName = null, szRelDir = null;
    boolean bLoaded = false;

    public dataThread(String szFileName, String szRelDir, Applet aApplet){
        this.szFileName = szFileName;
        this.szRelDir = szRelDir;
        this.aApplet = aApplet;
    }

     //Main execution thread
    public synchronized void run(){
        //System.out.println("dataThread run");
        if(aApplet.getCodeBase().getHost().compareTo("")!=0){
            try {
                sSocket = new Socket(aApplet.getCodeBase().getHost(), 80);    //get stream socket on http's port, ie port 80
                //System.out.println("got socket");
            } catch (UnknownHostException uhe) {
                System.out.println("UnknownHostException: " + uhe);
            } catch (IOException ioe) {
                System.out.println("IOException: " + ioe);
            }

            //Create input and output streams
            if(sSocket != null){
                try {
                    dosOutStream = new DataOutputStream(sSocket.getOutputStream());
                    disInStream  = new DataInputStream(sSocket.getInputStream());

                    //System.out.println("dosOutStream = " + dosOutStream);
                    //System.out.println("disInStream = " + disInStream);


                } catch (IOException ioe) {
                    System.out.println("IOException creating input and output streams : " + ioe);
                }
            }

            //Request a specific url doc that can be filtered
            if ((dosOutStream != null) && (disInStream != null)) {
                try {
                    dosOutStream.writeBytes("GET " + szRelDir + szFileName + " HTTP/1.0\r\n\r\n");
                    System.out.println(" dosOutStream = GET " + szRelDir + szFileName + " HTTP/1.0\r\n\r\n");

                } catch (IOException ioe) {
                    System.out.println("IOException writing to output stream : " + ioe);
                }
            }

            //Handle web server http response -> exstensible
            if (dosOutStream != null) {
                //System.out.println("dosOutStream != null");
                String tmpBuffer = "";
                while(!bLoaded){
                    try {
                        tmpBuffer = disInStream.readLine();
                        //System.out.println("tmpBuffer = " + tmpBuffer);
                        if((tmpBuffer.indexOf("*", 0) <= 6)&&(tmpBuffer.indexOf("*", 0) > 0)){
                            szBuffer = tmpBuffer;
                            bLoaded = true;
                        }
                    } catch (IOException ioe) {
                        System.out.println("IOException reading data from input stream : " + ioe);
                    }
                }
            } else {
                System.out.println("Output stream not created correctly");
            }

            //cleanup
            if ((dosOutStream != null) && (disInStream != null)) {
                try {
                    dosOutStream.close();
                    disInStream.close();
                    sSocket.close();
                    //System.out.println("socket closed");
                } catch (IOException ioe) {
                    System.out.println("IOException - error closing streams : " + ioe);
                }
            }
        }
    }

    public synchronized void newDataSet(String szFileName){
        this.szFileName = szFileName;
        bLoaded = false;
        run();
    }

    //For Applet getting data
    public synchronized String getData(){
        //System.out.println("szBuffer = " + szBuffer);
        return szBuffer;
    }

    //Destructor
    public synchronized void cleanUp(){
        //System.out.println("Cleanup reached");
        aApplet = null;
        szBuffer = null;
        szFileName = null;
        System.gc();        //Request garbage collection
        stop();             //Stop thread
    }
}
