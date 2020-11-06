package ir.ceit.search.nlp;

import java.util.ArrayList;

import ir.ceit.search.model.Verb;
import ir.ceit.search.model.VerbTense;

public class Stemmer {
    private static final String[] pronounSuffix = {"ام", "است", "ایم", "اید", "ای", "اند"};
    public static final String[] cliticPronounSuffix = {"یم", "م", "ید", "ی", "ند", "د"};
    public ArrayList<Verb> verbs;

    public Stemmer(ArrayList<Verb> verbs) {
        this.verbs = verbs;
    }

    public boolean endsWithSamePronoun(String firstPart, String secondPart) {
        boolean samePronouns = false;
        for (int i = 0; i < cliticPronounSuffix.length; i++) {
            if (firstPart.endsWith(cliticPronounSuffix[i]) && secondPart.endsWith(cliticPronounSuffix[i])) {
                samePronouns = true;
                break;
            }
        }
        if (getPronoun(firstPart) == null && getPronoun(secondPart) == null)
            samePronouns = true;
        return samePronouns;
    }

    public boolean isSeparatePronoun(String input) {
        boolean isPronoun = false;
        for (int i = 0; i < pronounSuffix.length; i++) {
            if (input.equals(pronounSuffix[i])) {
                isPronoun = true;
                break;
            }
        }
        return isPronoun;
    }

    public String getPronoun(String verb) {
        String pronoun = null;
        for (int i = 0; i < cliticPronounSuffix.length; i++) {
            if (verb.endsWith(cliticPronounSuffix[i])) {
                pronoun = cliticPronounSuffix[i];
                break;
            }
        }
        return pronoun;
    }

    public VerbTense isVerb(String firstPart, String secondPart) {
        VerbTense verbTense = VerbTense.NONE;
        //ماضی استمراری: می رفت
        //مضارع اخباری: می رود
        if (firstPart.equals("می") || firstPart.equals("نمی")) {
            if (isVerbPastTense(stem(firstPart, secondPart, VerbTense.MAZI_ESTEMRARI)))
                return VerbTense.MAZI_ESTEMRARI;
            if (isVerbPresentTense(stem(firstPart, secondPart, VerbTense.MOZARE_EKHBARI)) && getPronoun(secondPart) != null)
                return VerbTense.MOZARE_EKHBARI;
        }
        //ماضی نقلی: رفته ام
        if (firstPart.endsWith("ه") && isSeparatePronoun(secondPart)) {
            if (isVerbPastTense(stem(firstPart, secondPart, VerbTense.MAZI_NAQLI)))
                return VerbTense.MAZI_NAQLI;
        }
        //ماضی بعید: رفته بودم
        if (firstPart.endsWith("ه") && secondPart.startsWith("بود")) {
            if (isVerbPastTense(stem(firstPart, secondPart, VerbTense.MAZI_BAEED)))
                return VerbTense.MAZI_BAEED;
        }
        //آینده: خواهم رفت
        if ((firstPart.startsWith("خواه") || firstPart.startsWith("نخواه")) && getPronoun(firstPart) != null) {
            if (isVerbPastTense(stem(firstPart, secondPart, VerbTense.AYANDE)))
                return VerbTense.AYANDE;
        }
        //ماضی التزامی: داشتم میرفتم
        if (firstPart.startsWith("داشت") && (secondPart.startsWith("می") || secondPart.startsWith("نمی")) && endsWithSamePronoun(firstPart, secondPart)) {
            if (isVerbPastTense(stem(firstPart, secondPart, VerbTense.MAZI_ELTEZAMI)))
                return VerbTense.MAZI_ELTEZAMI;
        }
        //مضارع مستمر: داری میروی
        if (firstPart.startsWith("دار") && (secondPart.startsWith("می") || secondPart.startsWith("نمی")) && endsWithSamePronoun(firstPart, secondPart)) {
            if (isVerbPresentTense(stem(firstPart, secondPart, VerbTense.MOZARE_MOSTAMAR)))
                return VerbTense.MOZARE_MOSTAMAR;
        }
        return verbTense;
    }

    public VerbTense isVerb(String firstPart, String secondPart, String thirdPart) {
        boolean isVerb = false;
        VerbTense verbTense = VerbTense.NONE;
        //ماضی التزامی: داشتم می رفتم
        if (firstPart.startsWith("داشت") && endsWithSamePronoun(firstPart, thirdPart) && (secondPart.equals("می") || secondPart.equals("نمی"))) {
            if (isVerbPastTense(stem(firstPart, secondPart, thirdPart, VerbTense.MAZI_ELTEZAMI)))
                return VerbTense.MAZI_ELTEZAMI;
        }
        //مضارع مستمر: داری می روی
        if (firstPart.startsWith("دار") && endsWithSamePronoun(firstPart, thirdPart) && secondPart.equals("می")) {
            return VerbTense.MAZI_ELTEZAMI;
        }
        return verbTense;
    }

    public String stem(String firstPart, String secondPart, VerbTense verbTense) {
        String stem = "";
        switch (verbTense) {
            case MAZI_ESTEMRARI://می رفتم
                if (getPronoun(secondPart) != null&& secondPart.length() > 2)
                    return secondPart.substring(0, secondPart.indexOf(getPronoun(secondPart)));
                else
                    return secondPart;
            case MOZARE_EKHBARI://می روم
                if (getPronoun(secondPart) != null && secondPart.length() > 2)
                    return secondPart.substring(0, secondPart.indexOf(getPronoun(secondPart)));
            case MAZI_BAEED://رفته بودم
            case MAZI_NAQLI://رفته ام
                if (firstPart.startsWith("ن") && firstPart.length() > 3)
                    return firstPart.substring(1, firstPart.length() - 1);
                else
                    return firstPart.substring(0, firstPart.length() - 1);
            case AYANDE://خواهم رفت
                return secondPart;
            case MOZARE_MOSTAMAR://دارم میروم
            case MAZI_ELTEZAMI://داشتم میرفتم
                if (secondPart.startsWith("نمی") && secondPart.length() > 4) {
                    if (getPronoun(secondPart) != null)
                        return secondPart.substring(3, secondPart.indexOf(getPronoun(secondPart), 3));
                    else
                        return secondPart.substring(3);
                } else {
                    if (secondPart.length() > 3) {
                        if (getPronoun(secondPart) != null)
                            return secondPart.substring(2, secondPart.indexOf(getPronoun(secondPart), 2));
                        else
                            return secondPart.substring(2);
                    }

                }
        }
        return firstPart + " " + secondPart;
    }

    public String stem(String firstPart, String secondPart, String thirdPart, VerbTense
            verbTense) {
        String stem = "";
        switch (verbTense) {
            case MOZARE_MOSTAMAR://دارم می روم
            case MAZI_ELTEZAMI://داشتم می رفتم
                if (secondPart.startsWith("ن") && thirdPart.length() > 3) {
                    if (getPronoun(thirdPart) != null)
                        return thirdPart.substring(0, thirdPart.indexOf(getPronoun(thirdPart)));
                    else
                        return thirdPart;
                }
                if (getPronoun(thirdPart) != null && thirdPart.length() > 3)
                    return thirdPart.substring(0, thirdPart.indexOf(getPronoun(thirdPart), 2));
                else
                    return thirdPart;
            default:
                break;
        }

        return firstPart + " " + secondPart + " " + thirdPart;
    }

    public String stem(String verb) {
        //ماضی ساده: رفتم
        if (getPronoun(verb) != null) {
            if (verb.startsWith("ن") && verb.length() > 2) {
                if (isVerbPastTense(verb.substring(1, verb.indexOf(getPronoun(verb), 1)))) {
                    return verb.substring(1, verb.indexOf(getPronoun(verb), 1));
                }
            } else if (verb.length() > 2) {
                if (isVerbPastTense(verb.substring(0, verb.indexOf(getPronoun(verb), 1)))) {
                    return verb.substring(0, verb.indexOf(getPronoun(verb), 1));
                }
            }
        }
        //ماضی استمراری: میرفتیم
        if (getPronoun(verb) != null) {
            if (verb.startsWith("می") && verb.length() > 4) {
                if (isVerbPastTense(verb.substring(2, verb.indexOf(getPronoun(verb), 3)))) {
                    return verb.substring(2, verb.indexOf(getPronoun(verb), 3));
                }
            } else if (verb.startsWith("نمی") && verb.length() > 4) {
                if (isVerbPastTense(verb.substring(3, verb.indexOf(getPronoun(verb), 4)))) {
                    return verb.substring(3, verb.indexOf(getPronoun(verb), 4));
                }
            }
        } else {
            if (verb.startsWith("می") && verb.length() > 2) {
                if (isVerbPastTense(verb.substring(2))) {
                    return verb.substring(2);
                }
            } else if (verb.startsWith("نمی") && verb.length() > 3) {
                if (isVerbPastTense(verb.substring(3))) {
                    return verb.substring(3);
                }
            }
        }
        //مضارع اخباری: میروم
        if (getPronoun(verb) != null) {
            if (verb.startsWith("نمی") && verb.length() > 3) {
                if (isVerbPresentTense(verb.substring(3, verb.indexOf(getPronoun(verb), 3)))) {
                    return verb.substring(3, verb.indexOf(getPronoun(verb), 3));
                }
            } else if (verb.startsWith("می") && verb.length() > 3) {
                if (isVerbPresentTense(verb.substring(2, verb.indexOf(getPronoun(verb), 3)))) {
                    return verb.substring(2, verb.indexOf(getPronoun(verb), 3));
                }
            }
        }
        //مضارع التزامی: ببینم
        if (getPronoun(verb) != null) {
            if (verb.startsWith("ن") && verb.length() > 2) {
                if (isVerbPresentTense(verb.substring(1, verb.indexOf(getPronoun(verb), 1)))) {
                    return verb.substring(1, verb.indexOf(getPronoun(verb), 1));
                }
            } else if (verb.startsWith("ب") && verb.length() > 2) {
                if (isVerbPresentTense(verb.substring(1, verb.indexOf(getPronoun(verb), 1)))) {
                    return verb.substring(1, verb.indexOf(getPronoun(verb), 1));
                }
            }
        }
        //صفت مفعولی: رفته
        if (verb.endsWith("ه") && verb.length() > 3) {
            if (isVerbPastTense(verb.substring(0, verb.length() - 1))) {
                return verb.substring(0, verb.length() - 1);
            }
        }

        return verb;
    }

    public boolean isVerbPastTense(String verb) {
        boolean valid = false;
        for (int i = 0; i < verbs.size(); i++) {
            if (verbs.get(i).getPastTense().equals(verb)) {
                valid = true;
                break;
            }
        }
        return valid;
    }

    public boolean isVerbPresentTense(String verb) {
        boolean valid = false;
        for (int i = 0; i < verbs.size(); i++) {
            if (verbs.get(i).getPresentTense().equals(verb)) {
                valid = true;
                break;
            }
        }
        return valid;
    }
}
