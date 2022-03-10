package licenta.allbank.service;

import android.content.Context;

import licenta.allbank.data.api.IngApi;

public class ServiceIng {
    private final static String ENDPOINT = "https://api.sandbox.ing.com/";
    private static ServiceIng serviceIng;
    private final IngApi ingApi;
    private static String appToken;
    private static String accessToken;
    private static String refreshToken;
    public final static String STATE = "require_token_ing";
    private Context context;

    private ServiceIng(IngApi IngApi, Context context) {
        ingApi = IngApi;
        this.context = context;
    }

    public static void setAppToken(String token) {
        appToken = token;
    }

    public static String getAppToken() {
        return appToken;
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
}
