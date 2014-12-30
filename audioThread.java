////////////////////////////////////////////////////////////////////////////
//                                                                        //
//      Module:     audioThread.java                                      //
//      Author:     Aaron Saikovski                                       //
//      Date:       28/02/97                                              //
//      Version:    1.0                                                   //
//      Purpose:    Creates a thread of execution for an Applet to        //
//                  provide an asynchronous method for requesting         //
//                  and collecting sound (.au) files via URL.             //
//                  Applet can check current status via the               //
//                  public boolean flag variable loaded.                  //
//                                                                        //
////////////////////////////////////////////////////////////////////////////

import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.applet.AudioClip;
import java.lang.Thread;

public class audioThread extends Thread {

    Applet aApplet;          //Applet that creates the Thread
    AudioClip acAudio;          //Sound file to use
    String strSound_URL;        //String to hold image file name

    /*---------------------------------------------------------------*/

    //Audio Thread Constructor
    public audioThread(String strURL, Applet a){
        aApplet = a;                        //Assign owning Applet
        strSound_URL = strURL;              //Assign Sound file URL
    }

    /*---------------------------------------------------------------*/

    //Main audioThread routine
    public synchronized void run(){

        //Show status
        aApplet.getAppletContext().showStatus("Loading " + strSound_URL + "...");

        //Get the audio clip..to be returned to owner!!
        acAudio = aApplet.getAudioClip(aApplet.getCodeBase(), strSound_URL);

    }

    /*---------------------------------------------------------------*/

    //audioThread play soundroutine
    public synchronized void playIt(){

        //Play the tune
        acAudio.play();

    }
    /*---------------------------------------------------------------*/

    //Destructor
    public synchronized void cleanUp(){
        try {
            if (acAudio != null) {
                //acAudio.stop();         //Stop the audio
                acAudio = null;			//Assign all object references to null
            }

            aApplet = null;
            strSound_URL = null;
    		this.stop();            //Stop thread
    		System.gc();			//Request garbage collection

		}
		catch (Exception e){
		    System.out.println("Exception in class audioThread  - method cleanUp()");
		}
    }

    //Ende mit dem klass

}
