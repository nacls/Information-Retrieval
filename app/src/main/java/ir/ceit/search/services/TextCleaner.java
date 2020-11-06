package ir.ceit.search.services;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class TextCleaner {
    public String removeHTMLTags(String string) {
        String htmlRemoved = string.replaceAll("\\<.*?\\>", "");
        return htmlRemoved;
    }
    public String removeWord(String string, String toBeDeleted) {
        String sentence = string;
        String word = toBeDeleted;
        int spaces = 0;
        int left = -1;
        int right = 0;
        int nonspacechar = 0;//leftmost nonspacechar
        int difference = 0;//difference between the outermost indexes
        int consecspaces = 0;
        ArrayList<Integer> format = new ArrayList();//declare arrayList
        for (int i = 0; i < sentence.length(); i++) {
            if (i == sentence.indexOf(word, right)) {//to get the left index after the word's right index
                if (nonspacechar == 0) {
                    left = nonspacechar;
                } else
                    left = nonspacechar + 1;
                format.add(left);//adding the left index to the arraylist
                if (sentence.length() - i == word.length()) {//if the word is at the end
                    right = i + word.length();
                    format.add(right);
                    format.add(2);
                    break;
                }
                i = i + word.length() - 1;
                continue;
            }
            if (sentence.charAt(i) != ' ') {
                if (left >= 0) {//to get the right non space index when left is set
                    right = i;
                    format.add(right);//add right outermost index
                    if (i == sentence.length() - 1 && sentence.charAt(i) == '.') {
                        format.add(2);
                    } else if (spaces > 1) {
                        format.add(1);

                    } else if (nonspacechar == 0) {
                        format.add(2);
                    } else
                        format.add(0);
                    left = -1;//reset left
                }
                nonspacechar = i;
                spaces = 0;
            } else if (left >= 0 && spaces == 1) {
                spaces = 0;
            } else
                spaces++;
        }
        int k = 0;
        StringBuilder newSentence = new StringBuilder(sentence);//declare the newsentence Stringbuilder to be replaced acording to indexes
        while (k < format.size()) {
            left = format.get(k);
            right = format.get(k + 1);
            if (k > 0) {
                if (consecspaces == 1) {//check last consecuative space condition
                    left = left - difference;
                    right = right - difference;
                } else {
                    left = left - (difference);//subtract last difference
                    right = right - (difference);
                }
            }
            consecspaces = format.get(k + 2);
            if (consecspaces == 1) {//check if consecuativespaces exist
                difference = difference + word.length();//adding word on difference
                newSentence = newSentence.replace(newSentence.indexOf(word, left), newSentence.indexOf(word, left) + word.length(), "");

            } else if (consecspaces == 2) {
                difference = difference + (right - left);
                newSentence = newSentence.replace(left, right, "");
            } else {
                difference = difference + (right - left) - 1; //adding word on difference
                newSentence = newSentence.replace(left, right, " ");
            }
            k = k + 3;
        }
        sentence = new String(newSentence);//get the new sentence
        return sentence;
    }
    public String removeChars(String string, String delete) {
        String charToDel = delete;
        String pat = "[" + Pattern.quote(charToDel) + "]";
        String charDeleted = string.replaceAll(pat, "");
        return charDeleted;
    }
    public String removeWords(String string) {
        String wordsDeleted = removeWord(string, "laquo");
        wordsDeleted = removeWord(wordsDeleted, "raquo");
        wordsDeleted = removeWord(wordsDeleted, "nbsp");
        return wordsDeleted;
    }
}
