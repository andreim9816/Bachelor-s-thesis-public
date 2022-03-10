package licenta.allbank.utils.messages;

import android.content.Context;
import android.widget.Toast;

public class MessageDisplay {
    public static void showLongMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showShortMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showErrorMessage(Context context, String message) {
        showLongMessage(context, message);
    }
}
