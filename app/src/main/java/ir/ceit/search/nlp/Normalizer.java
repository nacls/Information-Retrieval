package ir.ceit.search.nlp;

public class Normalizer {

    public String normalize(String s){
        return replaceLetters(deleteEmojis(deleteAbnormal(deletePunctuation(replaceSeparator(s)))));
    }

    public String replaceLetters(String s) {
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case 'ئ':
                case 'ي':
                    newString.append('ی');
                    break;
                case 'ة':
                    newString.append('ت');
                    break;
                case 'ۀ':
                    newString.append('ه');
                    break;
                case '‌'://NIM FASELE
                case '‏':
                    newString.append(' ');
                    break;
                case 'ك':
                    newString.append('ک');
                    break;
                case 'ؤ':
                    newString.append('و');
                    break;
                case 'إ':
                case 'أ':
                case 'آ':
                    newString.append('ا');
                    break;
                case 'ء':
                case '\u064B': //FATHATAN
                case '\u064C': //DAMMATAN
                case '\u064D': //KASRATAN
                case '\u064E': //FATHA
                case '\u064F': //DAMMA
                case '\u0650': //KASRA
                case '\u0651': //SHADDA
                case '\u0652': //SUKUN
                    break;
                default:
                    newString.append(s.charAt(i));
            }
        }
        return newString.toString();
    }

    public String deletePunctuation(String s){
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '!':
                case '.':
                case '،':
                case '؟':
                case ':':
                case ';':
                case '؛':
                case '«':
                case '»':
                case '(':
                case ')':
                case '[':
                case ']':
                case '}':
                case '{':
                    newString.append(' ');
                    break;
                default:
                    newString.append(s.charAt(i));
            }
        }
        return newString.toString().trim().replaceAll("\\s{2,}", " ");
    }

    public String deleteAbnormal(String s){
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '%':
                case '@':
                case '#':
                case '$':
                case '^':
                case '&':
                case '*':
                case '+':
                case '=':
                case '×':
                case '|':
                case '"':
                case '>':
                case '<':
                case '~':
                case '/':
                    newString.append(' ');
                    break;
                default:
                    newString.append(s.charAt(i));
            }
        }
        return newString.toString();
    }

    public String replaceSeparator(String s){
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '-':
                case '_': 
                    newString.append(' ');
                    break;
                default:
                    newString.append(s.charAt(i));
            }
        }
        return newString.toString();
    }
    
    public String deleteEmojis(String s){
        String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
        String emotionless = s.replaceAll(characterFilter,"");

        return emotionless;
    }
}
