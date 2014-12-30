///////////////////////////////
//  File   : imageThread.java
//  Author : Bryan Richards
//  Company: ObjectCentric
//  Date   : 9/12/96

///////////////////////////////
// Creates a thread of execution for an Applet to provide an asynchronous
// method for requesting and collecting an image file via URL. Applet can check
// current status via the public boolean flag variable loaded.

import java.awt.*;
import java.applet.*;

public class imageThread extends Thread{

    //ImageObserver

    Applet aApplet;          //Applet that creates the Thread
    Image imImage;           //Offscreen image to hold data
    MediaTracker mtImage;    //Tracks image as it is loading
    String strImageName;     //String to hold image file name
    boolean bLoaded = false; //Flag to signal completeion

    //Constructor
    public imageThread(String str, Applet a){
        aApplet = a;                        //Assign owning Applet
        mtImage = new MediaTracker(aApplet);//Create new MediaTracker
        strImageName = str;                 //Assign image file name
    }

    //Main imageThread routine
    public void run(){
        //aApplet.getAppletContext().showStatus("Loading " + strImageName + "...");   //Show status
        while(!bLoaded){                                                            //While not finished loading
            imImage = aApplet.getImage(aApplet.getCodeBase(), strImageName);        //Get the image
            mtImage.addImage(imImage, 0);                                           //Add image to MediaTracker
            try{
                mtImage.waitForID(0);                                               //Wait for the asynchronous load
                while(mtImage.COMPLETE != mtImage.statusID(0, true));               //Loop until completely loaded
                //System.out.println("image is: " + mtImage.COMPLETE);
                //System.out.println(mtImage.ABORTED + " " + mtImage.COMPLETE
                //                    + " " + mtImage.ERRORED
	            //                    + " " + mtImage.LOADING);
                bLoaded = true;                                                     //Reset boolean flag for completion
            }catch(InterruptedException ie){
                System.out.println("Image thread interrupted exception - " + strImageName);}    //Catch exception, but do nothing
        }
        //this.suspend();
    }

    //Destructor
    public void cleanUp(){
        mtImage = null;     //Assign all object references to null
        aApplet = null;
        imImage = null;
        strImageName = null;
        stop();             //Stop thread
    }

}
