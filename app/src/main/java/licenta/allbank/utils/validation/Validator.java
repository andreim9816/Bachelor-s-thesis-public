package licenta.allbank.utils.validation;

import android.content.Context;

import licenta.allbank.R;

public class Validator {
    private static final String USERNAME_REGEX = "^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+$";
    private static final String PHONE_REGEX = "^07[0-9]{8}$";
    private static final String PASSWORD_REGEX = "^(?=.*[\\w])(?=.*[\\W])[\\w\\W]{8,}$";
    private static final String LAST_NAME_REGEX = "^[a-zA-Z][a-zA-Z .'-]*$";
    private static final String FIRST_NAME_REGEX = "^[a-zA-Z][a-zA-Z .'-]*$";
    public static String REGEX_IBAN = "^[A-Z]{2}\\d{2}[A-Z0-9]{20}$";

    public enum type {
        USERNAME,
        PHONE,
        PASSWORD,
        EMAIL,
        LAST_NAME,
        FIRST_NAME
    }

    public static String notMatches(Context context, String input, type type) {
        switch (type) {
            case USERNAME:
                if (input.matches(USERNAME_REGEX)) {
                    return null;
                } else {
                    return context.getString(R.string.error_username);
                }
            case PHONE:
                if (input.matches(PHONE_REGEX)) {
                    return null;
                } else {
                    return context.getString(R.string.error_phone);
                }
            case PASSWORD:
                if (input.matches(PASSWORD_REGEX)) {
                    return null;
                } else {
                    return context.getString(R.string.error_password);
                }
            case EMAIL:
                if (input.matches(EMAIL_REGEX)) {
                    return null;
                } else {
                    return context.getString(R.string.error_email);
                }
            case LAST_NAME:
                if (input.matches(LAST_NAME_REGEX)) {
                    return null;
                } else {
                    return context.getString(R.string.error_last_name);
                }
            case FIRST_NAME:
                if (input.matches(FIRST_NAME_REGEX)) {
                    return null;
                } else {
                    return context.getString(R.string.error_first_name);
                }
            default:
                return null;
        }
    }
}
