////////////////////////////////////////////////////////////////////////////
//                                                                        //
//      Module:     ClueAnswer.java                                       //
//      Author:     Aaron Saikovski                                       //
//      Date:       26/02/97                                              //
//      Version:    1.0                                                   //
//      Purpose:    Clue + Answer references class                        //
//                                                                        //
////////////////////////////////////////////////////////////////////////////

import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.io.*;
import java.util.*;

/*---------------------------------------------------------------*/

//ClueAnswer Class
public class ClueAnswer {
    String szAnswer = null;
    String szClue = null;
    int nQuestionNumber = 0;
    boolean bIsAcross = true;
    Square sqAnswerSquares[] = null;

    /*---------------------------------------------------------------*/

    //ClueAnswer Constructor
    public ClueAnswer(){
    }

    /*---------------------------------------------------------------*/

    //Highlights the current word and sets active square.
    public void HighlightSquares(Square sq, boolean bSetHighLighted){
        for (int i=0; i<szAnswer.length(); i++) {
            if (!bSetHighLighted)
                sqAnswerSquares[i].setHighlighted(crossword.nCURRENT_NONE);
            else {
                if (sqAnswerSquares[i] == sq)
                    sqAnswerSquares[i].setHighlighted(crossword.nCURRENT_LETTER);
                else
                    sqAnswerSquares[i].setHighlighted(crossword.nCURRENT_WORD);
            }

        }

    }

    /*---------------------------------------------------------------*/

    //Sets the object reference.
    public void setObjectRef(String szAnswer, String szClue, int nQuestionNumber,
                                boolean bIsAcross, Square sqAnswerSquares[]){

        this.szAnswer = szAnswer;
        this.szClue = szClue;
        this.nQuestionNumber = nQuestionNumber;
        this.bIsAcross = bIsAcross;

        //Initialise the answer squares array.
        this.sqAnswerSquares = new Square[szAnswer.length()];

        for(int i=0; i<szAnswer.length(); i++) {
            this.sqAnswerSquares[i] = new Square();
            this.sqAnswerSquares[i].CreateSquare(0, 0);
        }


	    //Copy the array
        try {
            for (int k = 0; k<szAnswer.length(); k++)
            {
                this.sqAnswerSquares[k] = sqAnswerSquares[k];
            }
        }
        catch (Exception e) {
            System.out.println("Exception " + e + "occurred in method setObjectRef");
        }


        //setup reference pointers back to me for each square
        for (int i=0; i<szAnswer.length();i++){
            sqAnswerSquares[i].setObjectRef(this.bIsAcross, this);
        }
    }

    /*---------------------------------------------------------------*/

    //Returns the Answer/char match
    public char getChar(Square sq){
        int i = 0;
        while (i < szAnswer.length()){
            if (sq == sqAnswerSquares[i])
                return szAnswer.charAt(i);
            i++;
        }
        return '@';
    }

    /*---------------------------------------------------------------*/

    //Gets the first square referenced by my answer.
    public Square getSquare(){
        return sqAnswerSquares[0];
    }

    /*---------------------------------------------------------------*/

    //Returns the next square
    public Square getNextsq(Square sq){
        int i = 0;
        while (i < szAnswer.length()){
            if (sq == sqAnswerSquares[i])
                if (i < szAnswer.length() - 1)
                    return sqAnswerSquares[i + 1];
            i++;
        }
        return sq;

    }

    /*---------------------------------------------------------------*/

    //Returns the previous square
    public Square getPrevsq(Square sq){
        int i = (szAnswer.length() -1);
        while (i > -1){
            if (sq == sqAnswerSquares[i])
                if (i != 0){
                    return sqAnswerSquares[i - 1];

                }
                else{
                    return sq;
                }
            i--;
        }
        return sq;

    }

    /*---------------------------------------------------------------*/

    //Returns true if all answer letters are correct and false otherwise
    public boolean isCorrect(){
        for(int i = 0; i < szAnswer.length(); i++){
            if(sqAnswerSquares[i].chLetter != szAnswer.charAt(i))
                return false;
        }
        return true;
    }

    /*---------------------------------------------------------------*/

    //Sets the Hint letter if Get Letter is pressed
    public boolean checkHint(char chHintLetter){
        boolean bResult = false;
        for(int i = 0; i < szAnswer.length(); i++)
            if((szAnswer.charAt(i) == chHintLetter)&&(sqAnswerSquares[i].chLetter != chHintLetter)){
                sqAnswerSquares[i].setLetter(chHintLetter, bIsAcross);
                bResult = true;
            }
        return bResult;
    }

    /*---------------------------------------------------------------*/

    //Sets the letter colour if Check words is pressed.
    public void checkWord(){
        for(int i = 0; i < szAnswer.length(); i++)
            sqAnswerSquares[i].checkLetter(szAnswer.charAt(i));
    }

    /*---------------------------------------------------------------*/

    //Checks if the Answer squares are populated with chars.
    public boolean isPopulated(){
        for(int i = 0; i < szAnswer.length(); i++){
            if((sqAnswerSquares[i].clAcross==null)||(sqAnswerSquares[i].clDown==null))
                if(sqAnswerSquares[i].isPopulated())
                    return true;
        }
        return false;
    }

    /*---------------------------------------------------------------*/

    //Resets the current word squares - Simply overtypes the current
    //sq contents with ' ' chars.
    public void ResetWord(boolean bIsAcross){
        for(int i = 0; i < szAnswer.length(); i++){
            if((sqAnswerSquares[i].clAcross!=null)&&(sqAnswerSquares[i].clDown!=null)){
                if(bIsAcross){
                    if(!sqAnswerSquares[i].clDown.isPopulated()){
                        sqAnswerSquares[i].chLetter = ' ';
                        sqAnswerSquares[i].bIsDirty = true;
                    }
                }
                else{
                    if(!sqAnswerSquares[i].clAcross.isPopulated()){
                        sqAnswerSquares[i].chLetter = ' ';
                        sqAnswerSquares[i].bIsDirty = true;
                    }
                }
            }else{
                sqAnswerSquares[i].chLetter = ' ';
                sqAnswerSquares[i].bIsDirty = true;
            }
        }
    }

    /*---------------------------------------------------------------*/
}
