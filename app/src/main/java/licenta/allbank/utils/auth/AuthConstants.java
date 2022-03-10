package licenta.allbank.utils.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthConstants {
    /* Tokens stored in Shared Preferences */
    public static String ACCESS_TOKEN_ING = "ACCESS_TOKEN_ING";
    public static String ACCESS_TOKEN_BCR = "ACCESS_TOKEN_BCR";

    public static String REFRESH_TOKEN_ING = "REFRESH_TOKEN_ING";
    public static String REFRESH_TOKEN_BCR = "REFRESH_TOKEN_BCR";

    private static final String APP_KEY = "APP_KEY";
    private static final String DEFAULT_VALUE = "";

    public static void setStringSharedPreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringSharedPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, DEFAULT_VALUE);
    }
}
