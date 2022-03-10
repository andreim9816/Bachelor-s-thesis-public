package licenta.allbank.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import licenta.allbank.data.api.BcrApi;

public class ServiceBcr {

    public final static String ENDPOINT = "https://webapi.developers.erstegroup.com/api/bcr/sandbox/v1/";
    public final static String BCR = "BCR";
    public final static String STATE_TOKEN = "token_state_bcr";
    public final static String STATE_PAYMENT = "payment_state_bcr";
    public final static String STATE_LOGIN_FOR_PAYMENT = "login_for_payment_confirm_bcr";
    public final static String STATE_ADD_ACCOUNT = "add_account_bcr";
    private static String accessToken;
    private static String refreshToken;
    private static String API_KEY;
    private static String CLIENT_ID;
    private static ServiceBcr serviceBcr;
    private final BcrApi bcrApi;

    private ServiceBcr(BcrApi bcrApi) {
        this.bcrApi = bcrApi;
    }

    public static void getAuthAccessCodeBcr(Context context, String state) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceBcr.ENDPOINT
                + "sandbox-idp/auth?redirect_uri=" + ServiceServer.REDIRECT_URI
                + "&client_id=" + ServiceBcr.CLIENT_ID
                + "&response_type=code"
                + "&access_type=offline"
                + "&state=" + state));
        context.startActivity(intent);
    }

    public static void signPayment(Context context, String scaRedirect) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scaRedirect
                + "?redirect_uri=" + ServiceServer.REDIRECT_URI
                + "&state=" + ServiceBcr.STATE_PAYMENT));
        context.startActivity(intent);
    }

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setRefreshToken(String token) {
        refreshToken = token;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static String getBcrClientId() {
        return CLIENT_ID;
    }

    public static void setBcrClientId(String bcrClientId) {
        CLIENT_ID = bcrClientId;
    }
}
