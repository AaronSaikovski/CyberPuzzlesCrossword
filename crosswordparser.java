//////////////////////////////////////////////////////////////////////////////
//                                                                          //
//      Module:     crosswordparser.java                                    //
//      Author:     Aaron Saikovski                                         //
//      Date:       23/01/97                                                //
//      Version:    1.0                                                     //
//      Purpose:    Utilizes a String Tokenizer to parse the crossword      //
//                  puzzle components from a data set string.               //
//                                                                          //
//////////////////////////////////////////////////////////////////////////////

///////////////////////////////
// Example data set String
// "1075*QXW2410*0909*0 0 1 1#6 0 1 4#3 2 1 6#0 3 1 8#3 5 1 11#0 6 1 13#0 8 1 15#4 8 1 16#1 0 2 2#4 0 2 3#6 0 2 4#8 0 2 5#3 2 2 6#5 2 2 7#7 4 2 9#0 5 2 10#4 5 2 12#2 6 2 14*Bread maker#Skill#Receive#Calm#Real#Taunts#Apple _ _ _#Midday meal#Irritate#Wealthy#Queen,King,__ __ __#Ballet skirt#Book of maps#100 make a dollar#Conjuring#Cease#Prison room#Length of life*BAKER#ART#ACCEPT#SOOTHE#ACTUAL#TEASES#PIE#LUNCH#ANNOY#RICH#ACE#TUTU#ATLAS#CENTS#MAGIC#STOP#CELL#AGE*ABCEGHIKLMNOPRSTUY*30 1 1 0 1*Use the clues to solve this crossword and earn CyberSilver. If you have not played our crosswords before and want help, then click the HELP button. Have fun!"

//Jim's New Dataset as at 03/01/97
// "645*QX000000*0909*0 0 1 1#6 0 1 4#3 2 1 6#0 3 1 8#3 5 1 11#0 6 1 13#0 8 1 15#4 8 1 16#1 0 2 2#4 0 2 3#6 0 2 4#8 0 2 5#3 2 2 6#5 2 2 7#7 4 2 9#0 5 2 10#4 5 2 12#2 6 2 14*Bread maker#Skill#Receive#Calm#Real#Taunts#Apple _ _ _#Midday meal#Irritate#Wealthy#Queen,King,__ __ __#Ballet skirt#Book of maps#100 make a dollar#Conjuring#Cease#Prison room#Length of life*BAKER#ART#ACCEPT#SOOTHE#ACTUAL#TEASES#PIE#LUNCH#ANNOY#RICH#ACE#TUTU#ATLAS#CENTS#MAGIC#STOP#CELL#AGE*ABCEGHIKLMNOPRSTUY*30 1 1 0 1 5*Use the clues to solve this crossword and earn CyberSilver. If you have not played our crosswords before and want help, then click the HELP button. Have fun!"

import java.util.StringTokenizer;
import java.util.NoSuchElementException;

public class crosswordparser{

    //Instance variables for holding parsed QuickCrossword data
    String szPuzzleType = null;                                       //PuzzleId
    int nNumCols = 0, nNumRows = 0, nNumAcross = 0, nNumDown = 0, nPuzzleId = 0;
    int[] nColRef, nRowRef, nIsAcross, nQuesNum;
    String[] szClues = null, szAnswers = null;
    int[] nCosts = {0,0,0,0,0,0};
    String szGetLetters = null, szBlurb = null;
    int nNumQuestions;

    //Public constructor
    public crosswordparser() {
    }

    //Main - used to parse QuickCrossword data set from string
    //Pre  : szParseData is NOT null
    //Post : Returns true if data has been succesfully parsed from String and false otherwise
    public boolean parseData(String szParseData) {

        StringTokenizer strData, strGridData, strSubGridData, strClues, strAnswers, strCosts;
        strData = new StringTokenizer(szParseData, "*");
        int nNumTokens = strData.countTokens();

        //Loop over for each of the tokens
        for(int nTokenIdx = 0; nTokenIdx < nNumTokens; nTokenIdx++){

            switch (nTokenIdx){

                //Eat the first String - number of bytes in data set - eg. "1075"
                case 0: try{
                            strData.nextToken();
                            }catch(NoSuchElementException nsee){return false;}
                        break;

                //Puzzle ID  - eg. "QXW000000"
                case 1: try {
                            szPuzzleType = strData.nextToken();
                            nPuzzleId = Integer.parseInt(szPuzzleType.substring(2));
                            szPuzzleType =  szPuzzleType.substring(0,2);
                            } catch (NoSuchElementException nsee){return false;}
                        break;

                //Number of Columns and Rows - eg. "0909"
                case 2: try{
                            String szTemp = strData.nextToken();
                            nNumCols = Integer.parseInt(szTemp.substring(0,2));
                            nNumRows = Integer.parseInt(szTemp.substring(2));
                            }catch(NoSuchElementException nsee){return false;}
                        break;

                //Grid positions and Data for across and down numbers - eg. "0 0 1 1#6 0 1 4#3 2 1 6#0 3 1 8#3 5 1 11#0 6 1 13#0 8 1 15#4 8 1 16#1 0 2 2#4 0 2 3#6 0 2 4#8 0 2 5#3 2 2 6#5 2 2 7#7 4 2 9#0 5 2 10#4 5 2 12#2 6 2 14"
                case 3: strGridData = new StringTokenizer(strData.nextToken(),"#");
                        nNumQuestions = strGridData.countTokens();
                        nColRef = new int[nNumQuestions];
                        nRowRef = new int[nNumQuestions];
                        nIsAcross = new int[nNumQuestions];
                        nQuesNum = new int[nNumQuestions];
                        for(int nTokIdx=0; nTokIdx<nNumQuestions; nTokIdx++){
                            try{
                                strSubGridData = new StringTokenizer(strGridData.nextToken()," ");
                                for(int i = 0; i < 4; i++){
                                    switch (i){
                                        case 0: try {
                                                    nColRef[nTokIdx] = Integer.parseInt(strSubGridData.nextToken());
                                                } catch (NumberFormatException nfe) {return false;}
                                                break;
                                        case 1: try {
                                                    nRowRef[nTokIdx] = Integer.parseInt(strSubGridData.nextToken());
                                                } catch (NumberFormatException nfe) {return false;}
                                                break;
                                        case 2: try {
                                                    nIsAcross[nTokIdx] = Integer.parseInt(strSubGridData.nextToken());
                                                } catch (NumberFormatException nfe) {return false;}
                                                break;
                                        case 3: try {
                                                    nQuesNum[nTokIdx] = Integer.parseInt(strSubGridData.nextToken());
                                                } catch (NumberFormatException nfe) {return false;}
                                                break;
                                    }
                                }
                            }catch(NoSuchElementException nsee){return false;}
                        }
                        break;

                //Clues - eg. "Bread maker#Skill#Receive#Calm#Real#Taunts#Apple _ _ _#Midday meal#Irritate#Wealthy#Queen,King,__ __ __#Ballet skirt#Book of maps#100 make a dollar#Conjuring#Cease#Prison room#Length of life"
                case 4: strClues = new  StringTokenizer(strData.nextToken(),"#");
                        szClues = new String[nNumQuestions];
                        for(int j=0; j<nNumQuestions; j++){
                            try {
                                szClues[j] = strClues.nextToken();
                                } catch (NoSuchElementException nsee){return false;}
                            }
                        break;

                //Answers - eg BAKER#ART#ACCEPT#SOOTHE#ACTUAL#TEASES#PIE#LUNCH#ANNOY#RICH#ACE#TUTU#ATLAS#CENTS#MAGIC#STOP#CELL#AGE
                case 5: strAnswers = new  StringTokenizer(strData.nextToken(),"#");
                        szAnswers = new String[nNumQuestions];
                        for(int k=0; k<nNumQuestions; k++){
                            try {
                                szAnswers[k] = strAnswers.nextToken();
                                } catch (NoSuchElementException nsee){return false;}
                            }
                        break;

                //Hint letters - eg "ABCEGHIKLMNOPRSTUY"
                case 6:try {
                            szGetLetters = strData.nextToken();
                            } catch (NoSuchElementException nsee){return false;}
                        break;

                //CyberSilver level costs & bonuses - eg. "30 1 1 0 1 5"
                case 7: strCosts = new StringTokenizer(strData.nextToken()," ");
                        for (int loopIdx=0; loopIdx<6; loopIdx++){
                            try {
                                nCosts[loopIdx] = Integer.parseInt(strCosts.nextToken());
                                } catch (NumberFormatException nfe) {return false;}
                        }
                        break;

                //Jim's crappy blurb - eg. "Use the clues to solve this crossword and earn CyberSilver. If you have not played our crosswords before and want help, then click the HELP button. Have fun!"
                case 8: try {
                            szBlurb = strData.nextToken();
                            } catch (NoSuchElementException nsee){return false;}
                        break;
            }
        }
        return true;
       }
}
