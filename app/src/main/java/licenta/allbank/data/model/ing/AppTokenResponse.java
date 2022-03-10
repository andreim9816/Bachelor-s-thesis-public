package licenta.allbank.data.model.ing;

import com.google.gson.annotations.SerializedName;

public class AppTokenResponse {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("expires_in")
    private String expiresIn;
    @SerializedName("message")
    private String message;

    public String getAccessToken() {
        return accessToken;
    }
}
