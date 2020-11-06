package ir.ceit.search.nlp;

public class Processor {
    private CaseFolder caseFolder = new CaseFolder();
    private Normalizer normalizer = new Normalizer();

    public String process(String input) {
        String output;
        output = caseFolder.matchWords(normalizer.normalize(caseFolder.acronymMatch(input)));
        return output.trim();
    }

    public boolean isStopWord(String input) {
        String[] stopWords = {"و", "در", "از", "که", "با", "تا", "این", "ان", "به", "الی",
                "همچون", "حتی", "ولیکن", "نیز", "خاطر", "اما", "هم", "هر", "هیچ",
                "دیگر", "پس", "لذا", "الا", "مانند", "مثل", "ای", "برای", "ایا", "همین",
                "چیز", "زیرا", "چرا", "شاید", "چه", "هرچه", "نه", "بر", "ها", "تر", "ترین",
                "گر", "ی", "است", "اسا", "وار"};
        boolean match = false;
        for (int i = 0; i < stopWords.length; i++) {
            if (input.equals(stopWords[i])) {
                match = true;
                break;
            }
        }
        return match;
    }
}