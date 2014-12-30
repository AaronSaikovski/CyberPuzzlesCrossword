////////////////////////////////////////////////////////////////////////////
//                                                                        //
//      Module:     Square.java                                           //
//      Author:     Aaron Saikovski                                       //
//      Date:       23/01/97                                              //
//      Version:    1.0                                                   //
//      Purpose:    Defines a Square and it's attributes                  //
//                                                                        //
////////////////////////////////////////////////////////////////////////////

import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.io.*;
import java.util.*;

/*---------------------------------------------------------------*/

//Square class
public class Square extends Object {
    public int nXCoord= 0 , nYCoord = 0, nXCharOffset = 0, nYCharOffset = 0;

    //implement wrapper functions to get these variables
    char chLetter = ' ';
    char chNumber = ' ';
    Color clForeColour = Color.black;
    Color clBackColour = Color.black;
    boolean bIsDirty = true, bIsCharAllowed = false;
    ClueAnswer clAcross = null, clDown = null;

    //private final static int i = QuickCrossword.nCURRENT_LETTER;

    /*---------------------------------------------------------------*/

    //Square Constructor
    public Square() {
    }

    /*---------------------------------------------------------------*/

    //Allocates graphics memory for blank square
    public void CreateSquare(int nXCoord, int nYCoord){
        this.nXCoord = nXCoord;
        this.nYCoord = nYCoord;
    }

    /*---------------------------------------------------------------*/

    //Checks to see if the current action occurs within a square.
    public boolean IsClickInsideMe(int nXMouseCoord, int nYMouseCoord){
        if((nXCoord + 1 <= nXMouseCoord) && (nXMouseCoord <= (nXCoord + (crossword.nSquareWidth) - 1))
                && (nYCoord +1 <= nYMouseCoord) && (nYMouseCoord <= (nYCoord + (crossword.nSquareHeight)) - 1))
            return true;
        else
            return false;
    }

    /*---------------------------------------------------------------*/

    //Set the object reference to clueanswer object
    public void setObjectRef(boolean bIsAcross, ClueAnswer cl){
        if (bIsAcross)
            this.clAcross = cl;
        else
            this.clDown = cl;

        bIsCharAllowed = true;
        bIsDirty = true;
        clBackColour = Color.white;

    }

    /*---------------------------------------------------------------*/

    //Sets the background colour of a square
    public void setHighlighted(int nHighlightType){
        switch (nHighlightType) {
        case 1 : //Current Letter
            if (!this.clBackColour.equals(Color.cyan)){
                this.clBackColour = Color.cyan;
                bIsDirty = true;
            }
            break;
        case 2 : //Current Word
            if (!this.clBackColour.equals(Color.yellow)){
                this.clBackColour = Color.yellow;
                bIsDirty = true;
            }
            break;
        case 3 : //Current None
            if (!this.clBackColour.equals(Color.white)){
                this.clBackColour = Color.white;
                bIsDirty = true;
            }
            break;
        default : //Something went wrong....
            if (!this.clBackColour.equals(Color.red)){
                System.out.println("Bogus color: " + nHighlightType);
                this.clBackColour = Color.red;
                bIsDirty = true;
            }
        }

    }

    /*---------------------------------------------------------------*/

    //returns the Clue/Answer reference
    public ClueAnswer getClueAnswerRef(boolean bIsAcross){
        if (bIsAcross)
            return clAcross;
        else
            return clDown;
    }

    /*---------------------------------------------------------------*/

    //Can the current orientation be flipped.
    public boolean CanFlipDirection(boolean bIsAcross){
        //if square is an intersection
        if ((bIsAcross) && (clDown != null))
            return true;
        else if ((!bIsAcross) && (clAcross != null))
            return true;
        else
            return false;
    }

    /*---------------------------------------------------------------*/

    //Check for correctness of letter based on input char parameter and toggles colour accordingly
    public void checkLetter(char chCorrectLetter){
        if(chLetter != ' '){
            if(chLetter == chCorrectLetter)
                clForeColour = Color.green;
            else
                clForeColour = Color.red;

            bIsDirty = true;
        }
    }

    /*---------------------------------------------------------------*/

    //Set the colour for a letter.
    //
    ////Set the colour for a letter..based also on assistant state
    //public void setLetter(char ch, boolean bIsAcross, boolean bIsAssistantOn){
    public void setLetter(char ch, boolean bIsAcross){
        chLetter = ch;
        bIsDirty = true;

        if (bIsAcross) {
            if (String.valueOf((char)clAcross.getChar(this)).equals(String.valueOf((char)chLetter).toUpperCase())){
                //If assistant is On then set correct colour to Green
                //if (bIsAssistantOn) {
                //    clForeColour = Color.green;
                //}
                //else {
                    clForeColour = Color.black;
                //}

            }

            else {
                //if (bIsAssistantOn) {
                //    clForeColour = Color.red;
                //}
                //else {
                    clForeColour = Color.black;
                //}
            }

        }
        else {
            if (String.valueOf((char)clDown.getChar(this)).equals(String.valueOf((char)chLetter).toUpperCase())){

                //If assistant is On then set correct colour to Green
                /*if (bIsAssistantOn) {
                    clForeColour = Color.green;
                }*/
                //else{
                    clForeColour = Color.black;
                //}
            }
            else {
                /*if (bIsAssistantOn) { //Change colour based on Assistant state
                    clForeColour = Color.red;
                }*/
                //else {
                    clForeColour = Color.black;
                //}

            }
        }

    }

    /*---------------------------------------------------------------*/

    //Gets the next available square
    public Square getNextsq(boolean bIsAcross){
        if (bIsAcross)
            if(clAcross != null)
                return clAcross.getNextsq(this);
            else
                return this;
        else
            if(clDown != null)
                return clDown.getNextsq(this);
            else
                return this;
    }

    /*---------------------------------------------------------------*/

    //Gets the previous available square
    public Square getPrevsq(boolean bIsAcross){
        if (bIsAcross)
            if(clAcross != null)
                return clAcross.getPrevsq(this);
            else
                return this;
        else
            if(clDown != null)
                return clDown.getPrevsq(this);
            else
                return this;
    }

    /*---------------------------------------------------------------*/

    //Returns boolean true/false based on square's contents
    public boolean isPopulated(){
        if (chLetter == ' '){
            return false;
        }
        else{
            return true;
        }
    }

    /*---------------------------------------------------------------*/

}

