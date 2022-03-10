package licenta.allbank.data.model.bt;

import com.google.gson.annotations.SerializedName;

public class TokenBt {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;
    @SerializedName("consents")
    private String consentId;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getConsentId() {
        return consentId;
    }
}
