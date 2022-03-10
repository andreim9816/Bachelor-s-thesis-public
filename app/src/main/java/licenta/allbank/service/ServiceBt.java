package licenta.allbank.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ServiceBt {
    public final static String BT = "BT";
    public static final String STATE_ADD_ACCOUNT = "add_account_bt";
    public static final String LOGIN_FOR_PAYMENT_CONFIRM = "login_payment_confirm_bt";
    public static final String STATE_AIS = "require_token_bt";
    public static final String STATE_PIS = "init_payment_bt";
    private static final String CLIENT_ID = "oq0EhtaGQ2ULzTuuITNn";
    private static String ID_CONSENT_ACCOUNTS;
    private static String accessToken;
    private static String refreshToken;

    public static void getAuthAccessCodeBT(Context context, String state) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://apistorebt.ro/mga/sps/oauth/oauth20/authorize?"
                + "response_type=code"
                + "&client_id=" + CLIENT_ID
                + "&redirect_uri=" + ServiceServer.REDIRECT_URI
                + "&scope=AIS:" + ID_CONSENT_ACCOUNTS
                + "&state=" + state
                + "&code_challenge=73oehA2tBul5grZPhXUGQwNAjxh69zNES8bu2bVD0EM."
                + "&code_challenge_method=S256"));
        context.startActivity(intent);
    }

    public static void signPayment(Context context, String paymentId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://apistorebt.ro/mga/sps/oauth/oauth20/authorize?"
                + "response_type=code"
                + "&client_id=" + CLIENT_ID
                + "&redirect_uri=" + ServiceServer.REDIRECT_URI
                + "&scope=PIS:" + paymentId
                + "&state=" + STATE_PIS
                + "&code_challenge=73oehA2tBul5grZPhXUGQwNAjxh69zNES8bu2bVD0EM."
                + "&code_challenge_method=S256"));
        context.startActivity(intent);
    }

    public static String getIdConsentAccounts() {
        return ID_CONSENT_ACCOUNTS;
    }

    public static void setIdConsentAccounts(String idConsentAccounts) {
        ID_CONSENT_ACCOUNTS = idConsentAccounts;
    }

    public static void setAccessToken(String accessToken) {
        ServiceBt.accessToken = accessToken;
    }

    public static void setRefreshToken(String refreshToken) {
        ServiceBt.refreshToken = refreshToken;
    }
}
