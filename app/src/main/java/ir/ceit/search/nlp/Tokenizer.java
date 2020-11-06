package ir.ceit.search.nlp;

import java.util.ArrayList;

import ir.ceit.search.model.VerbTense;

public class Tokenizer {
    public Stemmer stemmer;

    public Tokenizer(Stemmer stemmer) {
        this.stemmer = stemmer;
    }

    public boolean expressionCheck(String input) {
        String[] expressionList = {"فی مابین", "چنان چه", "بنا بر این", "علی ای حال", "مع ذلک", "کن فیکون", "کان لم یکن", "ان شا الله",
                "کمافی السابق", "علی رغم", "خواه ناخواه", "به هر حال", "جست و جو", "بر خلاف", "خاطر نشان", "خارق العاده", "فوق العاده",
                "کثیر الانتشار", "دست اندرکار", "غیر قابل قبول", "قابل قبول", "با این وجود"};
        boolean match = false;
        for (int i = 0; i < expressionList.length; i++) {
            if (input.equals(expressionList[i]) && !match) {
                match = true;
            }
        }
        return match;
    }

    public ArrayList<String> tokenize(String input) {
        ArrayList<String> output = new ArrayList<>();
        String[] words = input.trim().split(" ");
        for (int i = 0; i < words.length; i++) {
            if (i < words.length - 1) {
                if (expressionCheck(words[i] + " " + words[i + 1])) {
                    output.add(words[i] + " " + words[i + 1]);
                    i++;
                } else if (stemmer.isVerb(words[i], words[i + 1]) != VerbTense.NONE) {
                    output.add(stemmer.stem(words[i], words[i + 1], stemmer.isVerb(words[i], words[i + 1])));
                    i++;
                } else {
                    if (i < words.length - 2) {
                        if (expressionCheck(words[i] + " " + words[i + 1] + " " + words[i + 2])) {
                            output.add(words[i] + " " + words[i + 1] + " " + words[i + 2]);
                            i = i + 2;
                        } else if (stemmer.isVerb(words[i], words[i + 1], words[i + 2]) != VerbTense.NONE) {
                            output.add(stemmer.stem(words[i], words[i + 1], words[i + 2], stemmer.isVerb(words[i], words[i + 1], words[i + 2])));
                            i = i + 2;
                            ;
                        } else {
                            if (!words[i].equals(""))
                                output.add(words[i]);
                        }
                    } else {
                        if (!words[i].equals(""))
                            output.add(words[i]);
                    }
                }
            } else {
                if (!words[i].equals(""))
                    output.add(words[i]);
            }
        }
        return output;
    }
}
