package com.google.engedu.anagrams;

import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();

    private String currentWord;
    private int wordLength = DEFAULT_WORD_LENGTH - 1;

    private List<String> wordList;
    private HashSet<String> wordSet;
    private HashMap<String, ArrayList<String>> lettersToWord;
    private HashMap<Integer, ArrayList<String>> sizeToWords;

    public AnagramDictionary(InputStream wordListStream) throws IOException {

        wordList = new ArrayList<>();
        wordSet = new HashSet<>();
        lettersToWord = new HashMap<>();
        sizeToWords = new HashMap<>();

        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        String line;

        while ((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);

            if (lettersToWord.containsKey(sortLetters(word))) {
                ArrayList<String> currentAnagramsList = lettersToWord.get(sortLetters(word));
                currentAnagramsList.add(word);
                lettersToWord.put(sortLetters(word), currentAnagramsList);
            } else {
                ArrayList<String> anagramsList = new ArrayList<>();
                anagramsList.add(word);
                lettersToWord.put(sortLetters(word), anagramsList);
            }

            ArrayList<String> currentSizeToWordsList = sizeToWords.get(word.length());
            if (currentSizeToWordsList == null)
                currentSizeToWordsList = new ArrayList<>();
            currentSizeToWordsList.add(word);
            sizeToWords.put(word.length(), currentSizeToWordsList);
        }

        wordLength++;
    }

    public boolean isGoodWord(String word, String base) {

        if (wordSet.contains(word) && !word.contains(base))
            return true;

        return false;
    }

    public ArrayList<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<>();

        //using arrayList
//        for (String word : wordList) {
//            if (word.length() == targetWord.length()) {
//                if (sortLetters(word).equals(sortLetters(targetWord))) {
//                    if (isGoodWord(word, currentWord))
//                        result.add(word);
//                }
//            }
//        }

        //if this function has not been called from getAnagramsWithOneMoreLetter()
        if (currentWord == null) {
            currentWord = targetWord;
        }

        //using hashMap
        if (lettersToWord.containsKey(sortLetters(targetWord))) {
            for (String word : lettersToWord.get(sortLetters(targetWord)))
                if (isGoodWord(word, currentWord))
                    result.add(word);
        }

        return result;
    }

    private String sortLetters(String unsortedString) {

        //sortString

        //1. convert string to character array
        char[] unsortedChars = unsortedString.toCharArray();
        //2. sort character array
        Arrays.sort(unsortedChars);
        //3. converted sorted character array to string
        String sortedString = new String(unsortedChars);

        return sortedString;
    }

    public ArrayList<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<>();

        currentWord = word;

        for (char c = 'a'; c <= 'z'; c++) {
            result.addAll(getAnagrams(word + c));
        }

        return result;
    }

    public String pickGoodStarterWord() {

        if (wordLength == MAX_WORD_LENGTH + 1)
            wordLength = DEFAULT_WORD_LENGTH;

        ArrayList<String> potentialWordsList = sizeToWords.get(wordLength);

        String nextWord = potentialWordsList.get(random.nextInt(potentialWordsList.size()));

        if (getAnagramsWithOneMoreLetter(nextWord).size() >= MIN_NUM_ANAGRAMS) {
            wordLength++;
            return nextWord;
        } else
            return pickGoodStarterWord();
    }
}
