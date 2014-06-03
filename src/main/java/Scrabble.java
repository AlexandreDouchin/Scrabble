
import java.util.*;
import java.awt.Point;
import java.io.*;

public class Scrabble
{
    ArrayList [] allWords;
    ArrayList<String> scrabbleLetterTemplate;
    ArrayList<String> scrabbleLetterBonusTemplate;
    ArrayList sortedAllPossibleWordsValues;


    int templateHeight;
    int templateWidth;
    String availableLetters;

    String letterList;
    String letterValueList;
    final int lengthOfLongestWord = 50;
    public Scrabble()
    {
        scrabbleLetterTemplate = new ArrayList<String>();
        scrabbleLetterBonusTemplate = new ArrayList<String>();
        letterList = "";
        letterValueList= "";
        templateHeight = -1;
        templateWidth = -1;
    }
    public void loadScrabbleLetterTemplate(ArrayList<String> template)
    {
        scrabbleLetterTemplate = new ArrayList<String>();

        for(int x = 0; x < template.size(); x++)
            scrabbleLetterTemplate.add(template.get(x).toString());

        if (scrabbleLetterTemplate == null || scrabbleLetterTemplate.size() == 0)
            throw new RuntimeException("Input a blank arrayList");

        templateHeight = scrabbleLetterTemplate.size();
        templateWidth = scrabbleLetterTemplate.get(0).toString().length();
    }
    public void loadScrabbleLetterBonusPositionsTemplate(ArrayList<String> template)
    {
        scrabbleLetterBonusTemplate = new ArrayList<String>();
        for(int x = 0; x < template.size(); x++)
            scrabbleLetterBonusTemplate.add(template.get(x).toString());
    }
    public void verifyIntegrityOfInput()
    {
        int scrabbleLetterTemplateHeight = scrabbleLetterTemplate.size();
        int scrabbleLetterTemplateWidth = scrabbleLetterTemplate.get(0).toString().length();

        int scrabbleLetterBonusHeight = scrabbleLetterBonusTemplate.size();
        int scrabbleLetterBonusWidth = scrabbleLetterBonusTemplate.get(0).toString().length();

        int height = scrabbleLetterTemplateHeight;
        int width = scrabbleLetterTemplateWidth;

        wordListBySizeIsPopulated(allWords);
        String s;
        for(int y = 0; y < scrabbleLetterTemplate.size(); y++)
            for(int x = 0; x < scrabbleLetterTemplate.get(y).toString().length(); x++)
            {
                s = scrabbleLetterTemplate.get(y).toString().substring(x, x+1);

            }
    }
    public ArrayList bestFirstWords()
    {


        ArrayList<String> allPossibleWords = new ArrayList<String>();
        ArrayList wordsFitRMask = new ArrayList();
        String questionMark = "?";
        String filterMask = "";
        for(int x = 7; x > 1; x--)
        {
            filterMask = "";
            for(int y = 0; y < x; y++)
                filterMask += questionMark;

            wordsFitRMask.clear();
            wordsFitRMask = GetWordsFromRestrictionMask(filterMask, availableLetters);
            for(int z = 0; z < wordsFitRMask.size(); z++)
                allPossibleWords.add((String)wordsFitRMask.get(z));
        }

        ArrayList allPossibleWordsSortedByValue = GetValuesAndSortWordsByValue(allPossibleWords);

        return allPossibleWordsSortedByValue;

    }
    public ArrayList [] bestNextWord()
    {
        ArrayList [] tempQuadValueWordDirStart = new ArrayList[4];

        ArrayList [] mainQuadValueWordDirStart = new ArrayList[4];
        mainQuadValueWordDirStart[0] = new ArrayList<Integer>();
        mainQuadValueWordDirStart[1] = new ArrayList<String>();
        mainQuadValueWordDirStart[2] = new ArrayList<String>();
        mainQuadValueWordDirStart[3] = new ArrayList<Point>();
        for(int y = 0; y < templateHeight; y++)
        {
            for(int x = 0; x < templateWidth; x++)
            {
                if (PointIsAcceptableForBuildingOn(x, y, "horizontal") != -1 || PointIsAcceptableForBuildingOn(x, y, "vertical") != -1)
                {
                    tempQuadValueWordDirStart[0] = new ArrayList<Integer>();
                    tempQuadValueWordDirStart[1] = new ArrayList<String>();
                    tempQuadValueWordDirStart[2] = new ArrayList<String>();
                    tempQuadValueWordDirStart[3] = new ArrayList<Point>();

                    tempQuadValueWordDirStart = bestNextWordForPoint(x, y);

                    for(int i = 0; i < tempQuadValueWordDirStart[0].size(); i++)
                    {
                        mainQuadValueWordDirStart[0].add((Integer)tempQuadValueWordDirStart[0].get(i));
                        mainQuadValueWordDirStart[1].add((String)tempQuadValueWordDirStart[1].get(i));
                        mainQuadValueWordDirStart[2].add((String)tempQuadValueWordDirStart[2].get(i));
                        mainQuadValueWordDirStart[3].add((Point)tempQuadValueWordDirStart[3].get(i));
                    }
                }


            }
        }


        return mainQuadValueWordDirStart;

    }
    public ArrayList [] bestNextWordForPoint(int x, int y)
    {
        ArrayList [] quadValueWordDirStart = new ArrayList[4];
        quadValueWordDirStart[0] = new ArrayList();
        quadValueWordDirStart[1] = new ArrayList();
        quadValueWordDirStart[2] = new ArrayList();
        quadValueWordDirStart[3] = new ArrayList();

        ArrayList hMask = getHorizontalMaskFromStartingPoint(x, y);
        ArrayList vMask = getVerticalMaskFromStartingPoint(x, y);

        ArrayList<String> hWords = new ArrayList<String>();
        ArrayList<String> vWords = new ArrayList<String>();

        hWords = getWordsThatFitMask(hMask);
        vWords = getWordsThatFitMask(vMask);


        ArrayList<String> hvWords = new ArrayList<String>();
        ArrayList<Integer> valueHVWords = new ArrayList<Integer>();

        ArrayList<String> directionArray = new ArrayList<String>();

        hvWords.addAll(hWords);
        hvWords.addAll(vWords);

        ArrayList<Integer> valueHWords = new ArrayList<Integer>();
        for(int i = 0; i < hWords.size(); i++)
        {
            boolean isParentWord = true;
            valueHWords.add(new Integer(getScrabbleValueForWord(hWords.get(i).toString(), "horizontal", new Point(x, y), ((Integer)hMask.get(0)).intValue(), ((Integer)hMask.get(1)).intValue(), isParentWord)));
            directionArray.add("Horizontal");
        }

        ArrayList<Integer> valueVWords = new ArrayList<Integer>();
        for(int i = 0; i < vWords.size(); i++)
        {
            boolean isParentWord = true;
            valueHWords.add(new Integer(getScrabbleValueForWord(vWords.get(i).toString(), "vertical", new Point(x,y), ((Integer)vMask.get(0)).intValue(), ((Integer)vMask.get(1)).intValue(), isParentWord )));
            directionArray.add("Vertical");
        }

        valueHVWords.addAll(valueHWords);
        valueHVWords.addAll(valueVWords);


        ArrayList<Point> pointList = new ArrayList<Point>();
        for (int i = 0; i < valueHVWords.size(); i++)
        {
            pointList.add(new Point(x, y));
        }
        for(int i = 0; i < valueHVWords.size(); i++)
        {
            quadValueWordDirStart[0].add((Integer)valueHVWords.get(i));
            quadValueWordDirStart[1].add((String)hvWords.get(i));
            quadValueWordDirStart[2].add((String)directionArray.get(i));
            quadValueWordDirStart[3].add((Point)pointList.get(i));
        }

        return quadValueWordDirStart;
    }

    public ArrayList<String> getWordsThatFitMask(ArrayList mask)
    {
        ArrayList<String> WordsThatAvailableLettersCanCover = new ArrayList<String>();

        int len = mask.size()-2;

        ArrayList checkSizeList = new ArrayList();

        int choiceLettersStart = Integer.parseInt(((Integer)mask.get(0)).toString());
        int choiceLettersEnd = Integer.parseInt(((Integer)mask.get(1)).toString());
        int mandatoryLetter = Integer.parseInt(((Integer)mask.get(2)).toString());


        for(int x = 3; x < len; x++)
        {
            if (mask.get(x).getClass().toString().equals("class java.lang.String"))
            {

            }
            else if (mask.get(x).getClass().toString().equals("class java.util.ArrayList"))
            {

                if (((ArrayList)((ArrayList)mask.get(x))).size() == 0)
                    len = x-2;
            }
        }


        int startlength = 2;
        if (startlength < mandatoryLetter)
            startlength = mandatoryLetter;

        if (startlength < choiceLettersStart+1)
            startlength = choiceLettersStart+1;

        for(int z = startlength; z < len; z++)
        {
            int allWordsOfSizeLen = allWords[z].size();
            for(int x = 0; x < allWordsOfSizeLen; x++)
            {
                if (WordIsSupportedByAvailableLetters(allWords[z].get(x).toString(), choiceLettersStart, choiceLettersEnd, availableLetters))
                    if (allWords[z].get(x).toString().length() > (choiceLettersStart + mandatoryLetter))
                        if (wordFitsMask(allWords[z].get(x).toString(), mask, choiceLettersStart, choiceLettersEnd, mandatoryLetter))
                            WordsThatAvailableLettersCanCover.add(allWords[z].get(x).toString());
            }
        }

        return WordsThatAvailableLettersCanCover;
    }
    public boolean wordFitsMask(String word, ArrayList mask, int choiceLettersStart, int choiceLettersEnd, int mandatoryLetter)
    {
        String holdLetter;
        int lastPositionConsidered;
        ArrayList lettersSubList = new ArrayList(3);
        boolean letterPositionSatisfiesMask;

        boolean satisfiesMask = true;
        String currentWord = word;

        lettersSubList = new ArrayList();

        lastPositionConsidered = -1;

        for(int y = 3; y < mask.size() && y-3 < currentWord.length(); y++)
        {
            if (mask.get(y).getClass().toString().equals("class java.lang.String"))
            {
                lettersSubList = new ArrayList();
                lettersSubList.add(mask.get(y).toString());
            }
            else if (mask.get(y).getClass().toString().equals("class java.util.ArrayList"))
            {
                lettersSubList = (ArrayList)((ArrayList)mask.get(y)).clone();
            }
            letterPositionSatisfiesMask = false;
            for(int i = 0; i < lettersSubList.size(); i++)
            {
                holdLetter = lettersSubList.get(i).toString();
                if (holdLetter.equals(currentWord.charAt(y-3) + "") || holdLetter.equals("?"))
                {
                    letterPositionSatisfiesMask = true;
                    break;
                }
            }
            //the last position considered;
            lastPositionConsidered = y-3;

            if (letterPositionSatisfiesMask == false)
            {
                //this word does not fit this mask.  break out.
                satisfiesMask = false;
                break;
            }

        }

        if (satisfiesMask && (lastPositionConsidered < choiceLettersEnd || (lastPositionConsidered+1) == mask.size()-3))
        {
            if (WordIsSupportedByAvailableLetters(currentWord, choiceLettersStart, choiceLettersEnd, availableLetters))
                return true;
        }
        return false;
    }
    public boolean WordIsSupportedByAvailableLetters(String localCurrentWord, int choiceLettersStart, int choiceLettersEnd, String localAvailableLetters)
    {


        if (availableLetters.equals(""))
        {
            return false;
        }
        else
        {

            if (choiceLettersEnd >= localCurrentWord.length())
            {
                choiceLettersEnd = localCurrentWord.length();
            }
            else
            {
                choiceLettersEnd++;
            }



            if (WordCanBeMadeByLetters(localCurrentWord.toString().substring(choiceLettersStart, choiceLettersEnd), availableLetters))
            {
                return true;
            }

        }
        return false;
    }
    public ArrayList getHorizontalMaskFromStartingPoint(int x, int y)
    {

        ArrayList mask = new ArrayList();
        ArrayList tempList = new ArrayList();
        String lettersToLeft = "";
        String lettersToRight = "";
        int selectedLettersEnd = -1;
        int selectedLettersBegin = -1;
        Point endPoint = new Point(-1,-1);

        int mandatoryLetterPosition = PointIsAcceptableForBuildingOn(x, y, "horizontal");
        if (mandatoryLetterPosition != -1)
        {
            lettersToLeft = getBuilderWordFromIfExists(x, y, "horizontal", "left");
            selectedLettersBegin = lettersToLeft.length();
            mask.add(new Integer(selectedLettersBegin));
            endPoint = getEndPointFromStartPoint(x, y, "horizontal");
            selectedLettersEnd = (endPoint.x - x) + selectedLettersBegin;
            mask.add(new Integer(selectedLettersEnd));

            mask.add(new Integer(mandatoryLetterPosition));
            lettersToRight = getBuilderWordFromIfExists(x, y, "horizontal", "right");

            for(int i = 0; i < lettersToLeft.length(); i++)
            {
                mask.add(lettersToLeft.charAt(i) + "");
            }
            int selectedLettersLength = (selectedLettersEnd - selectedLettersBegin);
            String preword = "";
            String postword = "";
            for(int i = 0; i < selectedLettersLength+1; i++)
            {
                if (!PointHasLetterNorthOrSouth(x+i, y))
                {
                    tempList = new ArrayList();
                    tempList.add("?");
                    mask.add(tempList);
                }
                else
                {
                    if (PointHasLetterNorth(x+i, y))
                        preword = getBuilderWordFromIfExists(x+i, y, "vertical", "up");
                    else
                        preword = "";
                    if (PointHasLetterSouth(x+i, y))
                        postword = getBuilderWordFromIfExists(x+i, y, "vertical", "down");
                    else
                        postword = "";
                    mask.add(getLettersThatProduceAValidWordBetween(preword, postword));
                }
            }


            for(int i = 0; i < lettersToRight.length(); i++)
            {
                mask.add(lettersToRight.charAt(i) + "");
            }

        }
        else
        {
            ArrayList tempMask = new ArrayList();
            tempMask.add(new Integer("0"));
            tempMask.add(new Integer("0"));
            tempMask.add(new Integer("0"));
            tempMask.add(new ArrayList());
            return tempMask;
        }


        return mask;
    }

    public ArrayList getVerticalMaskFromStartingPoint(int x, int y)
    {
        ArrayList mask = new ArrayList();
        ArrayList tempList = new ArrayList();
        String lettersAbove = "";
        String lettersBelow = "";
        int selectedLettersEnd = -1;
        int selectedLettersBegin = -1;
        Point endPoint = new Point(-1,-1);
        int mandatoryLetterPosition = PointIsAcceptableForBuildingOn(x, y, "vertical");
        if (mandatoryLetterPosition != -1)
        {
            lettersAbove = getBuilderWordFromIfExists(x, y, "vertical", "up");

            selectedLettersBegin = lettersAbove.length();
            mask.add(new Integer(selectedLettersBegin));
            endPoint = getEndPointFromStartPoint(x, y, "vertical");
            selectedLettersEnd = (endPoint.y - y) + selectedLettersBegin;
            mask.add(new Integer(selectedLettersEnd));


            mask.add(new Integer(mandatoryLetterPosition));

            lettersBelow = getBuilderWordFromIfExists(x, y, "vertical", "down");

            for(int i = 0; i < lettersAbove.length(); i++)
            {
                mask.add(lettersAbove.charAt(i) + "");
            }
            int selectedLettersLength = (selectedLettersEnd - selectedLettersBegin);
            String preword = "";
            String postword = "";
            for(int i = 0; i < selectedLettersLength+1; i++)
            {

                if (!PointHasLetterEastOrWest(x, y+i))
                {
                    tempList = new ArrayList();
                    tempList.add("?");
                    mask.add(tempList);
                }
                else
                {
                    if (PointHasLetterWest(x, y+i))
                        preword = getBuilderWordFromIfExists(x, y+i, "horizontal", "left");
                    else
                        preword = "";
                    if (PointHasLetterSouth(x, y+i))
                        postword = getBuilderWordFromIfExists(x, y+i, "horizontal", "right");
                    else
                        postword = "";
                    preword = getBuilderWordFromIfExists(x, y+i, "horizontal", "left");
                    postword = getBuilderWordFromIfExists(x, y+i, "horizontal", "right");
                    mask.add(getLettersThatProduceAValidWordBetween(preword, postword));
                 }
            }


            for(int i = 0; i < lettersBelow.length(); i++)
            {
                mask.add(lettersBelow.charAt(i) + "");
            }

        }
        else
        {

            ArrayList tempMask = new ArrayList();
            tempMask.add(new Integer("0"));
            tempMask.add(new Integer("0"));
            tempMask.add(new Integer("0"));
            tempMask.add(new ArrayList());
            return tempMask;
        }





        return mask;
    }
    public ArrayList getLettersThatProduceAValidWordBetween(String preword, String postword)
    {

        String restrictionMask = preword + "?" + postword;

        int len = restrictionMask.length();
        ArrayList smallList = new ArrayList();

        smallList.addAll(allWords[len]);

        ArrayList smallerList = new ArrayList();
        ArrayList LettersThatCanGoInTheQuestionMark = new ArrayList();
        boolean satisfiesMask;

        for(int x = 0; x < smallList.size(); x++)
        {
            satisfiesMask = true;
            for(int y = 0; y < restrictionMask.length(); y++)
            {
                if (restrictionMask.charAt(y) != '?' && smallList.get(x).toString().charAt(y) != restrictionMask.charAt(y))
                {
                    satisfiesMask = false;
                }
            }
            if (satisfiesMask)
                smallerList.add(smallList.get(x).toString());
        }
        int locOfQuestionMark = preword.length();

        if (availableLetters.equals(""))
        {
            return smallerList;
        }
        else
        {
            String letter = "";
            for(int y = 0; y < smallerList.size(); y++)
            {
                letter = smallerList.get(y).toString().charAt(locOfQuestionMark) + "";
                if (!LettersThatCanGoInTheQuestionMark.contains(letter))
                {
                    LettersThatCanGoInTheQuestionMark.add("" + smallerList.get(y).toString().charAt(locOfQuestionMark));
                }
            }
        }

        return LettersThatCanGoInTheQuestionMark;

    }
    public String getBuilderWordFromIfExists(int x, int y, String direction, String side)
    {

        String returnValue = "";
        String tempChar;
        boolean startRecording = false;
        if (direction.equals("horizontal"))
        {
            if (side.equals("left"))
            {
                if (!(x > 0 && isLetter(getCharFromCoordinate(x-1, y)))){
                    return "";
                }
                int i = 0;
                while (true)
                {

                    tempChar = getCharFromCoordinate(x-i, y);
                    if (isLetter(tempChar))
                    {
                        returnValue = tempChar + returnValue;
                        startRecording = true;
                    }
                    if (x-i == 0 || (startRecording && tempChar.equals(" ")))
                    {
                        return returnValue;
                    }
                    i++;
                }
            }
            else if (side.equals("right"))
            {

                int i = 0;
                while (true)
                {
                    if (i > 7 && returnValue.equals("")) {
                        return returnValue;
                    }
                    tempChar = getCharFromCoordinate(x+i, y);
                    if (isLetter(tempChar))
                    {
                        returnValue = returnValue + tempChar;
                        startRecording = true;
                    }
                    if (x+i == templateWidth-1 || (startRecording && tempChar.equals(" ")))
                    {
                        return returnValue;
                    }
                    i++;
                }
            }
            else
            {
            }
        }
        else if (direction.equals("vertical"))
        {
            if (side.equals("up"))
            {
                if (!(y > 0 && isLetter(getCharFromCoordinate(x, y-1))))  {
                    return "";
                }
                int i = 0;
                while (true)
                {

                    tempChar = getCharFromCoordinate(x, y-i);
                    if (isLetter(tempChar))
                    {
                        returnValue = tempChar + returnValue;
                        startRecording = true;
                    }
                    if (y-i == 0 || (startRecording && tempChar.equals(" ")))
                    {
                        return returnValue;
                    }
                    i++;
                }
            }
            else if (side.equals("down"))
            {

                int i = 0;
                while (true)
                {
                    if (i > 7 && returnValue.equals(""))  {
                        return returnValue;
                    }
                    tempChar = getCharFromCoordinate(x, y+i);
                    if (isLetter(tempChar))
                    {
                        returnValue = returnValue + tempChar;
                        startRecording = true;
                    }
                    if (y+i == templateHeight-1 || (startRecording && tempChar.equals(" ")))
                    {
                        return returnValue;
                    }
                    i++;
                }
            }
            else
            {

            }
        }
        else
        {

        }
        return returnValue;
    }
    public Point getEndPointFromStartPoint(int x, int y, String direction)
    {


        if (direction.equals("horizontal"))
        {

            for(int i = 0; i < 7; i++)
            {

                if (isLetter(getCharFromCoordinate(x+i, y)))
                    return new Point(x+i-1, y);
                if (x+i == templateWidth-1)
                    return new Point(x+i, y);

            }

            return new Point(x+6, y);
        }
        else if (direction.equals("vertical"))
        {

            for(int i = 0; i < 7; i++)
            {

                if (isLetter(getCharFromCoordinate(x, y+i)))
                    return new Point(x, y+i-1);
                if (y+i == templateHeight-1)
                    return new Point(x, y+i);

            }
            return new Point(x, y+6);
        }
        else
        {
            throw new RuntimeException("Erreur direction");
        }
    }

    public int PointIsAcceptableForBuildingOn(int x, int y, String direction)
    {

        if (isLetter(getCharFromCoordinate(x, y)))
        {
            return -1;
        }
        else
        {

            if (y > 0 && isLetter(getCharFromCoordinate(x, y-1)))
                return 0;
            if (x > 0 && isLetter(getCharFromCoordinate(x-1, y)))
                return 0;

            if (y < templateHeight-1 && isLetter(getCharFromCoordinate(x, y+1)))
                return 0;
            if (x < templateWidth-1 && isLetter(getCharFromCoordinate(x+1, y)))
                return 0;


        }

        if (direction.equals("horizontal"))
        {
            for(int i = 1; i < 7; i++)
            {
                if ((x+i < templateWidth && PointHasLetterNorthOrSouth(x+i, y)) || (x+i < templateWidth && isLetter(getCharFromCoordinate(x+i, y))))
                {
                    return i;
                }
            }
            if (x+7 < templateWidth && isLetter(getCharFromCoordinate(x+7, y)))
                return 6;
        }
        else if (direction.equals("vertical"))
        {
            for(int i = 1; i < 7; i++)
            {
                if ((y+i < templateHeight && PointHasLetterEastOrWest(x, y+i)) || (y+i < templateHeight && isLetter(getCharFromCoordinate(x, y+i))))
                {
                    return i;
                }
            }
            if (y+7 < templateHeight && isLetter(getCharFromCoordinate(x, y+7)))
            {
                return 6;
            }

        }
        else
        {
            throw new RuntimeException("Erreur direction");
        }

        return -1;

    }


    public boolean PointHasLetterWest(int x, int y)
    {
        if (x > 0 && isLetter(getCharFromCoordinate(x-1, y)))
            return true;
        return false;
    }

    public boolean PointHasLetterEastOrWest(int x, int y)
    {

        if (x > 0 && isLetter(getCharFromCoordinate(x-1, y)))
            return true;
        if (x < templateWidth-1 && isLetter(getCharFromCoordinate(x+1, y)))
            return true;

        return false;

    }
    public boolean PointHasLetterNorth(int x, int y)
    {
        if (y > 0 && isLetter(getCharFromCoordinate(x, y-1)))
            return true;
        return false;
    }
    public boolean PointHasLetterSouth(int x, int y)
    {

        if (y < templateHeight-1 && isLetter(getCharFromCoordinate(x, y+1)))
            return true;
        return false;
    }

    public boolean PointHasLetterNorthOrSouth(int x, int y)
    {

        if (y > 0 && isLetter(getCharFromCoordinate(x, y-1)))
            return true;
        if (y < templateHeight-1 && isLetter(getCharFromCoordinate(x, y+1)))
            return true;

        return false;

    }

    public boolean isLetter(String s)
    {

        char c [] = s.toCharArray();

        if (c[0] == ' ')
            return false;
        if (Character.isLetter(c[0]))
            return true;

        return true;
    }
    public boolean isDigit(String s)
    {
        char c [] = s.toCharArray();

        if (c[0] == ' ')
            return false;
        if (Character.isDigit(c[0]))
            return true;

        return true;
    }
    public String getCharFromCoordinate(int x, int y)
    {

        return scrabbleLetterTemplate.get(y).toString().substring(x, x+1);
    }
    public ArrayList bestFirstWordsValues()
    {
        return sortedAllPossibleWordsValues;
    }
    public boolean pointIsOnTemplate(int x, int y)
    {
        if (x < 0 || x >= templateWidth)
            return false;
        if (y < 0 || y >= templateHeight)
            return false;
        return true;
    }
    public ArrayList GetValuesAndSortWordsByValue(ArrayList wordsList)
    {

        ArrayList allPossibleWordsValues = new ArrayList();

        for(int x = 0; x < wordsList.size(); x++)
        {
            allPossibleWordsValues.add(new Integer(getSimpleScrabbleValueForWord(wordsList.get(x).toString())));
        }

        ArrayList sortedWordsList = new ArrayList();
        sortedAllPossibleWordsValues = new ArrayList();

        int wordsListSize = wordsList.size();

        for(int y = 0; y < wordsListSize; y++)
        {
            int largestValueIndex = -1;
            int largestValue = -1;
            for(int x = 0; x < wordsList.size(); x++)
            {
                if (Integer.parseInt(allPossibleWordsValues.get(x).toString()) > largestValue)
                {
                    largestValue = Integer.parseInt(allPossibleWordsValues.get(x).toString());
                    largestValueIndex = x;
                }
            }

            sortedWordsList.add(wordsList.get(largestValueIndex));
            sortedAllPossibleWordsValues.add(allPossibleWordsValues.get(largestValueIndex));
            wordsList.remove(largestValueIndex);
            allPossibleWordsValues.remove(largestValueIndex);
        }
        return sortedWordsList;
    }
    public void loadAvailableWords(String localAvailableLetters)
    {
        availableLetters = localAvailableLetters;
    }
    public void loadLetterValues(String localLetterList, String localLetterValueList)
    {
        letterList = localLetterList;
        letterValueList = localLetterValueList;
    }
    public void GetWordListFromFile(String filePath)
    {
        ArrayList wordList = new ArrayList();

        ArrayList[] wordListBySize = new ArrayList[lengthOfLongestWord];

        for(int x = 0; x < lengthOfLongestWord; x++)
        {
            wordListBySize[x] = new ArrayList();
        }



        if (new File(filePath).exists() == false)
            throw new RuntimeException("erreur fichier");
        try
        {
            String line;
            BufferedReader filein = new BufferedReader(new FileReader(filePath));
            while ((line = filein.readLine()) != null)
            {
                wordList.addAll(Arrays.asList(line.split(" ")));
            }
        }
        catch (Exception e)
        {
        }

        int len = 0;
        String word = "";
        for(int y = 0; y < wordList.size(); y++)
        {
            len = wordList.get(y).toString().length();
            word = wordList.get(y).toString();
            wordListBySize[len].add(word);
        }


        allWords = new ArrayList[50];
        for(int x = 0; x < 50; x++)
            allWords[x] = new ArrayList();


        for(int x = 0; x < 50; x++)
            allWords[x].addAll(wordListBySize[x]);


    }
    public boolean wordListBySizeIsPopulated(ArrayList [] mylist)
    {
        if (mylist == null)
            return false;

        boolean hasSomeData = false;
        for(int x = 0; x < 50; x++)
        {
            if (mylist[x] == null)
                return false;
            if (mylist[x].size() > 0)
                hasSomeData = true;
        }

        if (hasSomeData)
            return true;
        else
            return false;


    }

    public ArrayList GetWordsFromRestrictionMask(String restrictionMask, String availableLetters)
    {

        int len = restrictionMask.length();
        ArrayList smallList = new ArrayList();

        smallList.addAll(allWords[len]);

        ArrayList smallerList = new ArrayList();
        ArrayList WordsThatAvailableLettersCanCover = new ArrayList();
        boolean satisfiesMask;

        for(int x = 0; x < smallList.size(); x++)
        {
            satisfiesMask = true;
            for(int y = 0; y < restrictionMask.length(); y++)
            {
                if (restrictionMask.charAt(y) != '?' && smallList.get(x).toString().charAt(y) != restrictionMask.charAt(y))
                {
                    satisfiesMask = false;
                }
            }
            if (satisfiesMask)
                smallerList.add(smallList.get(x).toString());
        }

        if (availableLetters.equals(""))
        {
            return smallerList;
        }
        else
        {
            for(int y = 0; y < smallerList.size(); y++)
            {
                if (WordCanBeMadeByLetters(smallerList.get(y).toString(), availableLetters))
                {
                    WordsThatAvailableLettersCanCover.add(smallerList.get(y));
                }
            }
        }
        return WordsThatAvailableLettersCanCover;

    }
    public boolean WordCanBeMadeByLetters(String word, String localAvailableLetters)
    {
        if (word.length() > localAvailableLetters.length())
            return false;

        char letter;
        char blank = ' ';
        for(int x = 0; x < word.length(); x++)
        {
            letter = word.charAt(x);
            if (!localAvailableLetters.contains("" + letter))
            {

                return false;
            }
            else
            {
                localAvailableLetters = localAvailableLetters.replaceFirst("" + letter, "" + blank);
            }

        }
        return true;
    }
    public int getSimpleScrabbleValueForWord(String word)
    {

        int totalvalue = 0;
        for(int x = 0; x < word.length(); x++)
        {
            totalvalue += getScrabbleValueOfLetter(word.charAt(x));
        }
        return totalvalue;
    }
    public int getScrabbleValueForWord(String word, String direction, Point chosenWordsStartPoint, int startOfContributedLetters, int endOfContributedLetters, boolean isParentWord)
    {

        int totalvalue = 0;
        int multiplier = 1;
        Point currentPoint;
        if (direction.equals("horizontal"))
        {
            totalvalue = 0;
            multiplier = 1;


            for(int i = 0; i < word.length(); i++)
            {
                totalvalue += getScrabbleValueOfLetter(word.charAt(i));

                currentPoint = new Point((chosenWordsStartPoint.x - startOfContributedLetters)+i, chosenWordsStartPoint.y);

                if (i >= startOfContributedLetters && i <= endOfContributedLetters)
                {
                    if ( isParentWord)
                        multiplier = multiplier * getMultipleFromPoint(currentPoint);
                    String preword = "";
                    String postword = "";

                    if (PointHasLetterNorth(currentPoint.x, currentPoint.y))
                    {
                        preword = getBuilderWordFromIfExists(currentPoint.x, currentPoint.y, "vertical", "up");
                    }
                    if (PointHasLetterSouth(currentPoint.x, currentPoint.y))
                    {
                        postword = getBuilderWordFromIfExists(currentPoint.x, currentPoint.y, "vertical", "down");
                    }
                    if ((preword + postword).length() > 0)
                    {
                        totalvalue += getSimpleScrabbleValueForWord(preword + postword);
                    }
                }
            }

            int resultvalue = totalvalue * multiplier;
            return resultvalue;
        }
        else if (direction.equals("vertical"))
        {
            totalvalue = 0;
            multiplier = 1;

            for(int i = 0; i < word.length(); i++)
            {
                totalvalue += getScrabbleValueOfLetter(word.charAt(i));

                currentPoint = new Point(chosenWordsStartPoint.x, (chosenWordsStartPoint.y-startOfContributedLetters)+i);

                if ((i >= startOfContributedLetters && i <= endOfContributedLetters))
                {
                    if (isParentWord)
                        multiplier = multiplier * getMultipleFromPoint(currentPoint);
                    String preword = "";
                    String postword = "";
                    if (PointHasLetterEastOrWest(currentPoint.x, currentPoint.y))
                    {
                        preword = getBuilderWordFromIfExists(currentPoint.x, currentPoint.y, "horizontal", "left");
                        postword = getBuilderWordFromIfExists(currentPoint.x, currentPoint.y, "horizontal", "right");
                    }
                    if (PointHasLetterEastOrWest(currentPoint.x, currentPoint.y))
                    {
                        preword = getBuilderWordFromIfExists(currentPoint.x, currentPoint.y, "horizontal", "left");
                        postword = getBuilderWordFromIfExists(currentPoint.x, currentPoint.y, "horizontal", "right");
                    }
                    if ((preword + postword).length() > 0)
                    {
                        totalvalue += getSimpleScrabbleValueForWord(preword + postword);
                    }
                }
            }

            int resultvalue = totalvalue * multiplier;
            return resultvalue;
        }
        else
        {
            throw new RuntimeException("Erreur direction");
        }
    }
    public int getMultipleFromPoint(Point point)
    {

        int multiple = 1;
        String multipleStr = scrabbleLetterBonusTemplate.get(point.y).toString().substring(point.x, point.x+1);

        if (multipleStr.equals(" "))
            return 1;

        if (isDigit(multipleStr) && multiple > 0)
        {
            multiple = Integer.parseInt(multipleStr);
            return multiple;
        }
        else
            throw new RuntimeException("Erreur");
    }
    public int getScrabbleValueOfLetter(char letter)
    {
        char letter2 = ' ';
        for(int x = 0; x < letterList.length(); x++)
        {
            letter2 = letterList.charAt(x);
            if (letter == letter2)
                return Integer.parseInt("" + letterValueList.charAt(x));
        }
        throw new RuntimeException("Erreur");
    }

    public void displayQuadArray3Columns(ArrayList [] quadValueWordDirStart)
    {
        int spacingFirstColumn = 4;
        int spacingSecondColumn = 12;
        int spacingThirdColumn = 10;
        int spacingFourthColumn = 7;

        for(int x = 0; x < quadValueWordDirStart[0].size(); x+=3)
        {
            System.out.print(quadValueWordDirStart[0].get(x).toString() + " ");
            System.out.print(displaySpacesOverDelta(quadValueWordDirStart[0].get(x).toString().length(), spacingFirstColumn));

            System.out.print(quadValueWordDirStart[1].get(x).toString() + " ");
            System.out.print(displaySpacesOverDelta(quadValueWordDirStart[1].get(x).toString().length(), spacingSecondColumn));

            System.out.print(quadValueWordDirStart[2].get(x).toString() + " ");
            System.out.print(displaySpacesOverDelta(quadValueWordDirStart[2].get(x).toString().length(), spacingThirdColumn));

            String s = "(" + ((Point)(quadValueWordDirStart[3].get(x))).x + " " + ((Point)(quadValueWordDirStart[3].get(x))).y + ")";
            System.out.print(s);
            System.out.print(displaySpacesOverDelta(s.length(), spacingFourthColumn));
            System.out.print("  |  ");
            if (x+1 < quadValueWordDirStart[0].size())
            {
                System.out.print(quadValueWordDirStart[0].get(x+1).toString() + " ");
                System.out.print(displaySpacesOverDelta(quadValueWordDirStart[0].get(x+1).toString().length(), spacingFirstColumn));

                System.out.print(quadValueWordDirStart[1].get(x+1).toString() + " ");
                System.out.print(displaySpacesOverDelta(quadValueWordDirStart[1].get(x+1).toString().length(), spacingSecondColumn));

                System.out.print(quadValueWordDirStart[2].get(x+1).toString() + " ");
                System.out.print(displaySpacesOverDelta(quadValueWordDirStart[2].get(x+1).toString().length(), spacingThirdColumn));

                s = "(" + ((Point)(quadValueWordDirStart[3].get(x+1))).x + " " + ((Point)(quadValueWordDirStart[3].get(x+1))).y + ")";
                System.out.print(s);
                System.out.print(displaySpacesOverDelta(s.length(), spacingFourthColumn));
            }
            System.out.print("  |  ");
            if (x+2 < quadValueWordDirStart[0].size())
            {
                System.out.print(quadValueWordDirStart[0].get(x+2).toString() + " ");
                System.out.print(displaySpacesOverDelta(quadValueWordDirStart[0].get(x+2).toString().length(), spacingFirstColumn));

                System.out.print(quadValueWordDirStart[1].get(x+2).toString() + " ");
                System.out.print(displaySpacesOverDelta(quadValueWordDirStart[1].get(x+2).toString().length(), spacingSecondColumn));

                System.out.print(quadValueWordDirStart[2].get(x+2).toString() + " ");
                System.out.print(displaySpacesOverDelta(quadValueWordDirStart[2].get(x+2).toString().length(), spacingThirdColumn));

                s = "(" + ((Point)(quadValueWordDirStart[3].get(x+2))).x + " " + ((Point)(quadValueWordDirStart[3].get(x+2))).y + ")";
                System.out.print(s);
                System.out.print(displaySpacesOverDelta(s.length(), spacingFourthColumn));
            }

            System.out.println("");
        }
    }
    public String displaySpacesOverDelta(int widthItemTakes, int numSpaces)
    {
        String spaces = "";
        for(int x = 0; x < numSpaces-widthItemTakes; x++)
            spaces +=" ";
        return spaces;
    }
    public boolean mainTemplateIsEmpty()
    {
        for(int x = 0; x < scrabbleLetterTemplate.size(); x++)
        {
            if (!scrabbleLetterTemplate.get(x).toString().trim().equals(""))
                return false;
        }
        return true;
    }
    public void InsertionSortQuadArray(int a[], String b[], String c[], Point d[], int lo0, int hi0) throws Exception
    {
        int i;
        int j;
        int v;
        String str1;
        String str2;
        Point point;

        for (i=lo0+1;i<=hi0;i++)
        {
            v = a[i];
            str1 = b[i];
            str2 = c[i];
            point = d[i];

            j=i;
            while ((j>lo0) && (a[j-1]>v))
            {
                a[j] = a[j-1];
                b[j] = b[j-1];
                c[j] = c[j-1];
                d[j] = d[j-1];
                j--;
            }
            a[j] = v;
            b[j] = str1;
            c[j] = str2;
            d[j] = point;
        }
    }

}


