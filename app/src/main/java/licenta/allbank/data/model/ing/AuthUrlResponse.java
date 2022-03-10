package licenta.allbank.data.model.ing;

import com.google.gson.annotations.SerializedName;

public class AuthUrlResponse {
    @SerializedName("locationId")
    private String location;
    @SerializedName("accessToken")
    private String appToken;

    public String getLocation() {
        return location;
    }

    public String getAppToken() {
        return appToken;
    }
}
