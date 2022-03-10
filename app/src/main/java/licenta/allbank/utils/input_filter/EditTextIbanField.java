package licenta.allbank.utils.input_filter;

import android.text.InputFilter;
import android.text.Spanned;

import static licenta.allbank.utils.validation.Validator.REGEX_IBAN;

public class EditTextIbanField implements InputFilter {
    private static EditTextIbanField INSTANCE = null;

    public static EditTextIbanField getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EditTextIbanField();
        }
        return INSTANCE;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            //TODO

            if (source.equals("")) {
                return "";
            }

            if (source.toString().matches(REGEX_IBAN)) {
                return null;
            }

            if (end > dend) {
                if (end == 3 || end == 4) {
                    char c = source.charAt(end - 1);
                    if (c >= '0' && c <= '9') {
                        return null;
                    }
                    return dest;
                }

                char c = source.charAt(end - 1);
                if (Character.isLetter(c)) {
                    return null;
                }
            }
//            char c = source.charAt(0);
//            if (Character.isLetter(c)) {
//                return "" + Character.toUpperCase(c);
//            }
//            if (Character.isDigit(c)) {
//                return "" + c;
//            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
