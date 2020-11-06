package ir.ceit;


import java.util.ArrayList;

import htz.ir.stemming.PersianStemmer;
import ir.ceit.search.model.Verb;
import ir.ceit.search.nlp.CaseFolder;
import ir.ceit.search.nlp.Normalizer;
import ir.ceit.search.nlp.Processor;
import ir.ceit.search.nlp.Stemmer;
import ir.ceit.search.nlp.Tokenizer;

public class Main {
    public static void main(String[] args) throws Exception {
        String query = "پیشرفته: است این کشور عزیز ما";
        String word = "پیشرفته";
        ArrayList<String> queryTokens = new ArrayList<>();
        queryTokens.add("اسلام");
        queryTokens.add("مجلس");
        queryTokens.add("شورا");
        queryTokens.add("هفته");
        System.out.print("Query tokens: ");
        for (int i = 0; i < queryTokens.size() ; i++) {
            System.out.print(queryTokens.get(i));
            if (i != queryTokens.size()-1)
                System.out.print(", ");
            else
                System.out.println();
        }
        System.out.println(getBriefNews("اسلام دین ما مجلس شورا است. اسلام اسلام تا پایان هفته مجلس! این دین اسلام خوب ما دین اسلام خوبی است اسلام ما! قبول داری؟", queryTokens));
    }

    public static String getBriefNews(String news, ArrayList<String> queryTokens) throws Exception {
        Stemmer stemmer = new Stemmer(build());
        String[] newsSentences = news.split("[(?<=!)(?<=.)(?<=؟)]");
        int[] score = new int[newsSentences.length];
        String brief = "";
        int maxSentence = 1;
        int sentenceCounter = 0;
        for (int i = 0; i < newsSentences.length; i++) {
            ArrayList<String> sentenceTokens = getTokensBrief(newsSentences[i]);
            if (sentenceCounter == maxSentence)
                break;
            loop:
            for (int j = 0; j < queryTokens.size(); j++) {
                for (int k = 0; k < sentenceTokens.size(); k++) {
                    if (queryTokens.get(j).equals(sentenceTokens.get(k)) && queryTokens.get(j) != null) {
                        score[i]++;
                        //System.out.println("LOG " + "query word: " + queryBrief.get(j) + " sentence: " + newsSentences[i] + " LOG");
                    }
                }
            }
        }
        int maxIndex = 0;
        int maxScore = 0;
        for (int i = 0; i < score.length; i++) {
            System.out.println("Sentence " + i + " score is " + score[i]);
            if (score[i] > maxScore) {
                maxIndex = i;
                maxScore = score[i];
            }
        }
        System.out.println("Max index is " + maxIndex + "& Max score is " + maxScore);
        for (int i = 0; i < newsSentences.length; i++) {
            System.out.println("Sentence[" + i + "]: " + newsSentences[i]);
        }

        brief = newsSentences[maxIndex];
        if (brief.equals("") && newsSentences.length > 0) {
            //brief = brief + "..." + newsSentences[0];
            System.out.println("LOG NO BRIEF IN RESULT LIST ACTIVITY");
        }
        return brief;
    }

    public static ArrayList<String> getTokensBrief(String input) throws Exception {
        ArrayList<String> tokens;
        Stemmer stemmer = new Stemmer(build());
        PersianStemmer persianStemmer = new PersianStemmer();
        Tokenizer tokenizer = new Tokenizer(stemmer);
        Processor processor = new Processor();
        String cleanText = processor.process(input);
        tokens = tokenizer.tokenize(cleanText);
        for (int i = 0; i < tokens.size(); i++) {
            tokens.set(i, stemmer.stem(tokens.get(i)));
            tokens.set(i, persianStemmer.run(tokens.get(i)));
            if (processor.isStopWord(tokens.get(i)) || tokens.get(i).equals("cat") || tokens.get(i).equals("source")) {
                tokens.remove(i);
                i--;
            }
        }
        return tokens;
    }

    private static int getIndexInString(String input, String word) {
        String[] stringWords = input.split(" ");
        int index = 0;
        for (int i = 0; i < stringWords.length; i++) {
            if (word.equals(stringWords[i])) {
                break;
            }
            index = index + (stringWords[i].length() + 1);
        }
        return index;
    }

    public static ArrayList<String> getTokensS(String input) throws Exception {
        ArrayList<String> tokens;
        Stemmer stemmer = new Stemmer(build());
        PersianStemmer persianStemmer = new PersianStemmer();
        Tokenizer tokenizer = new Tokenizer(stemmer);
        Normalizer normalizer = new Normalizer();
        Processor processor = new Processor();
        CaseFolder caseFolder = new CaseFolder();
        String cleanText = caseFolder.matchWords(normalizer.replaceLetters(input));
        tokens = tokenizer.tokenize(cleanText);
        for (int i = 0; i < tokens.size(); i++) {
            tokens.set(i, stemmer.stem(tokens.get(i)));
            tokens.set(i, persianStemmer.run(tokens.get(i)));
            if (processor.isStopWord(tokens.get(i))) {
                tokens.remove(i);
                i--;
            }
        }
        return tokens;
    }


    public static ArrayList<String> getTokens(String input) throws Exception {
        ArrayList<String> tokens;
        Stemmer stemmer = new Stemmer(build());
        PersianStemmer persianStemmer = new PersianStemmer();
        Tokenizer tokenizer = new Tokenizer(stemmer);
        Processor processor = new Processor();
        String cleanText = processor.process(input);
        tokens = tokenizer.tokenize(cleanText);
        for (int i = 0; i < tokens.size(); i++) {
            tokens.set(i, stemmer.stem(tokens.get(i)));
            tokens.set(i, persianStemmer.run(tokens.get(i)));
            if (processor.isStopWord(tokens.get(i))) {
                tokens.remove(i);
                i--;
            }
        }
        return tokens;
    }

    public static ArrayList<Verb> build() throws Exception {
        ArrayList<Verb> verbs = new ArrayList<>();
        Verb verb1 = new Verb("گفت", "گو");
        Verb verb2 = new Verb("شنید", "شنو");
        Verb verb3 = new Verb("خورد", "خور");
        Verb verb4 = new Verb("رفت", "رو");
        Verb verb5 = new Verb("شد", "شو");
        Verb verb6 = new Verb("دید", "بین");
        Verb verb7 = new Verb("کرد", "کن");
        Verb verb8 = new Verb("امد", "یای");
        Verb verb9 = new Verb("خواست", "خواه");
        Verb verb10 = new Verb("داشت", "دار");
        Verb verb11 = new Verb("بود", "هست");
        Verb verb12 = new Verb("داد", "ده");
        Verb verb13 = new Verb("زد", "زن");
        Verb verb14 = new Verb("گذاشت", "گذار");
        Verb verb15 = new Verb("دانست", "دان");
        Verb verb16 = new Verb("گذشت", "گذر");

        verbs.add(verb1);
        verbs.add(verb2);
        verbs.add(verb3);
        verbs.add(verb4);
        verbs.add(verb5);
        verbs.add(verb6);
        verbs.add(verb7);
        verbs.add(verb8);
        verbs.add(verb9);
        verbs.add(verb10);
        verbs.add(verb11);
        verbs.add(verb10);
        verbs.add(verb11);
        verbs.add(verb12);
        verbs.add(verb13);
        verbs.add(verb14);
        verbs.add(verb15);
        verbs.add(verb16);
        return verbs;
    }
}