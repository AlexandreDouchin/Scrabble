
import java.awt.Point;
import java.io.*;
import java.util.*;

public class Runner {

        public static void main(String[] args)
        {
            Scrabble scrabble = new Scrabble();
            String filePath =  "/Users/alexandredouchin/Documents/workspace/Java/ScrabbleSolverDeluxe/src/main/uswords.txt";


            scrabble.GetWordListFromFile(filePath);
            ArrayList<String> scrabbleLetterTemplate = new ArrayList<String>();

            //                          0123456789012345678
            scrabbleLetterTemplate.add("                   "); //0
            scrabbleLetterTemplate.add("                   "); //1
            scrabbleLetterTemplate.add("                   "); //2
            scrabbleLetterTemplate.add("                   "); //3
            scrabbleLetterTemplate.add("                   "); //4
            scrabbleLetterTemplate.add("         vain      "); //5
            scrabbleLetterTemplate.add("        jan e      "); //6
            scrabbleLetterTemplate.add("            z      "); //7
            scrabbleLetterTemplate.add("                   "); //8
            scrabbleLetterTemplate.add("                   "); //9
            scrabbleLetterTemplate.add("                   "); //10
            scrabbleLetterTemplate.add("                   "); //11
            scrabbleLetterTemplate.add("                   "); //12
            //                          0123456789012345678

            String availableLetters = "nvjezka";

            scrabble.loadScrabbleLetterTemplate(scrabbleLetterTemplate);
            scrabble.loadAvailableWords(availableLetters);
            ArrayList<String> scrabbleLetterBonusPositions = new ArrayList<String>();
            //                                0123456789012345678
            scrabbleLetterBonusPositions.add("4     4     4     4"); //0
            scrabbleLetterBonusPositions.add(" 3     3   3     3 "); //1
            scrabbleLetterBonusPositions.add("  3     2 2     3  "); //2
            scrabbleLetterBonusPositions.add("   2     2     2   "); //3
            scrabbleLetterBonusPositions.add("    2         2    "); //4
            scrabbleLetterBonusPositions.add("     2       2     "); //5
            scrabbleLetterBonusPositions.add("4     2     2     4"); //6         POINTS ON THE BOARD
            scrabbleLetterBonusPositions.add("     2       2     "); //7         Internet scoring, everything is multiple word scores.
            scrabbleLetterBonusPositions.add("    2         2    "); //8
            scrabbleLetterBonusPositions.add("   2     2     2   "); //9
            scrabbleLetterBonusPositions.add("  3     2 2     2  "); //10
            scrabbleLetterBonusPositions.add(" 3     3   3     3 "); //11
            scrabbleLetterBonusPositions.add("4     4     4     4"); //12
            //                                0123456789012345678
            scrabble.loadScrabbleLetterBonusPositionsTemplate(scrabbleLetterBonusPositions);

            String letterList      = "abcdefghijklmnopqrstuvwxyz";
            String letterValueList = "13321424189121138111149999";

            scrabble.loadLetterValues(letterList, letterValueList);
            scrabble.verifyIntegrityOfInput();

            if (scrabble.mainTemplateIsEmpty())
            {
                ArrayList bestFirstWords = scrabble.bestFirstWords();
                ArrayList bestFirstWordsValues = scrabble.bestFirstWordsValues();

                for(int x = bestFirstWords.size()-1; x >= 0 ; x--)
                {
                    System.out.println(bestFirstWordsValues.get(x).toString() + "   " + bestFirstWords.get(x).toString());
                }
            }
            else
            {

                ArrayList [] quadValueWordDirStart = new ArrayList[4];


                quadValueWordDirStart = scrabble.bestNextWord();

                int len = quadValueWordDirStart[0].size();

                int [] quadValueColumn0Ints = new int[len];
                String [] quadValueColumn1Ints = new String[len];
                String [] quadValueColumn2Ints = new String[len];
                Point [] quadValueColumn3Ints = new Point[len];

                for(int x = 0; x < quadValueWordDirStart[0].size(); x++)
                {
                    quadValueColumn0Ints[x] = ((Integer)quadValueWordDirStart[0].get(x)).intValue();
                    quadValueColumn1Ints[x] = quadValueWordDirStart[1].get(x).toString();
                    quadValueColumn2Ints[x] = quadValueWordDirStart[2].get(x).toString();
                    quadValueColumn3Ints[x] = (Point)quadValueWordDirStart[3].get(x);
                }
                try
                {
                    scrabble.InsertionSortQuadArray(quadValueColumn0Ints, quadValueColumn1Ints, quadValueColumn2Ints, quadValueColumn3Ints, 0, quadValueColumn0Ints.length-1);
                }
                catch (Exception e)
                {
                    System.out.println("erreur! ");
                    e.printStackTrace();
                }

                quadValueWordDirStart[0] = new ArrayList();
                quadValueWordDirStart[1] = new ArrayList<String>();
                quadValueWordDirStart[2] = new ArrayList();
                quadValueWordDirStart[3] = new ArrayList();

                for(int x = 0; x < quadValueColumn0Ints.length; x++)
                {
                    quadValueWordDirStart[0].add(new Integer(quadValueColumn0Ints[x]));
                    quadValueWordDirStart[1].add(quadValueColumn1Ints[x].toString());
                    quadValueWordDirStart[2].add(quadValueColumn2Ints[x].toString());
                    quadValueWordDirStart[3].add((Point)quadValueColumn3Ints[x]);

                }

                scrabble.displayQuadArray3Columns(quadValueWordDirStart);
            }

            System.out.println("Suivant");
        }


}
