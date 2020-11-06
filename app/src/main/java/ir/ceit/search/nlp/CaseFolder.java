package ir.ceit.search.nlp;

public class CaseFolder {
    public String matchWords(String input) {
        String output;
        StringBuilder sb = new StringBuilder();
        String[] words = input.split(" ");
        for (int i = 0; i < words.length; i++) {
            switch (words[i]) {
                case "اطاق":
                    output = "اتاق";
                    break;
                case "اطو":
                    output = "اتو";
                    break;
                case "باطری":
                    output = "باتری";
                    break;
                case "طوفان":
                    output = "توفان";
                    break;
                case "ذغال":
                    output = "زغال";
                    break;
                case "قرمه":
                case "غرمه":
                    output = "قورمه";
                    break;
                case "غورباغه":
                    output = "قورباغه";
                    break;
                case "غورت":
                    output = "قورت";
                    break;
                case "هلیم":
                    output = "حلیم";
                    break;
                case "هوله":
                    output = "حوله";
                    break;
                case "یقور":
                    output = "یغور";
                    break;
                case "بغچه":
                    output = "بقچه";
                    break;
                case "مليون":
                    output = "میليون";
                    break;
                case "سطبر":
                    output = "ستبر";
                    break;
                case "تومار":
                    output = "طومار";
                    break;
                case "غشغرق":
                    output = "قشقرق";
                    break;
                case "آزوغه":
                case "آزوقه":
                    output = "آذوقه";
                    break;
                case "عرابه":
                    output = "ارابه";
                    break;
                case "دگمه":
                case "تکمه":
                    output = "دکمه";
                    break;
                case "ملات":
                    output = "ملاط";
                    break;
                case "ناصور":
                    output = "ناسور";
                    break;
                case "یرقه":
                case "یرغه":
                case "یورقه":
                    output = "یورغه";
                    break;
                case "آغا":
                    output = "آقا";
                    break;
                case "پطرزبورگ":
                    output = "پترزبورگ";
                    break;
                case "ترقبه":
                    output = "طرقبه";
                    break;
                default:
                    output = words[i];
                    break;
            }
            if (!(words[i].equals(""))) {
                if (i != words.length - 1)
                    sb.append(output + " ");
                else
                    sb.append(output);
            }
        }
        return sb.toString();
    }
    public String acronymMatch(String input) {
        String output;
        StringBuilder sb = new StringBuilder();
        String[] words = input.split(" ");
        for (int i = 0; i < words.length; i++) {
            switch (words[i]) {
                case "ج.ا.":
                case "ج.ا":
                    output = "جمهوری اسلامی";
                    break;
                case "رجا":
                    output = "راه آهن جمهوری اسلامی ایران";
                    break;
                case "سمپاد":
                    output = "سازمان ملی پرورش استعدادهای درخشان";
                    break;
                case "ناجا":
                    output = "نیروی انتظامی جمهوری اسلامی ایران";
                    break;
                case "فاتب":
                    output = "فرماندهی انتظامی تهران بزرگ";
                    break;
                case "فاوا":
                    output = "فن آوری اطلاعات و ارتباطات";
                    break;
                case "فتا":
                    output = "پلیس فضای تولید و تبادل اطلاعات ناجا";
                    break;
                case "پیامک":
                    output = "پیام کوتاه";
                    break;
                case "پهپاد":
                    output = "پرنده هدایت پذیر از دور";
                    break;
                case "گاج":
                    output = "گروه آموزشی جوکار";
                    break;
                case "نزاجا":
                    output = "نیروی زمینی ارتش جمهوری اسلامی ایران";
                    break;
                case "نهسا":
                    output = "نیرو هوایی سپاه";
                    break;
                case "ازما":
                    output = "آزمون استخدامی متمرکز ادواری";
                    break;
                case "اتسا":
                    output = "انجمن تکفل و سرپرستی ایتام";
                    break;
                case "بهسامان":
                    output = "بهینه سازی مصرف انرژی";
                    break;
                case "ساف":
                    output = "سازمان آزادی بخش فلسطین";
                    break;
                case "پ.ن":
                case "پ.ن:":
                    output = "پی\u200Cنوشت";
                    break;
                case "پداجا":
                    output = "پدافند هوایی ارتش جمهوری اسلامی ایران";
                    break;
                case "وبدا":
                    output = "وزارت بهداشت، درمان و آموزش";
                    break;
                case "ماجد":
                    output = "مرکز انتشارات جهاد دانشگاهی";
                    break;
                default:
                    output = words[i];
                    break;
            }
            if (!words[i].equals("")) {
                if (i != words.length - 1)
                    sb.append(output + " ");
                else
                    sb.append(output);
            }
        }
        return sb.toString();
    }
}