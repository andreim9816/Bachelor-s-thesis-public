package licenta.allbank.utils.graphics;

import android.util.Log;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.List;

import okhttp3.Handshake;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class SslCertificateLogger implements Interceptor {

    public static final String TAG = "SSL";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            Log.d(TAG, "<-- HTTP FAILED: " + e);
            throw e;
        }

        Handshake handshake = response.handshake();
        if (handshake == null) {
            Log.d(TAG, "no handshake");
            return response;
        }


        Log.d(TAG, "handshake success");
        List<Certificate> certificates = handshake.peerCertificates();
        if (certificates.isEmpty()) {
            Log.d(TAG, "no peer certificates");
            return response;
        }

        String s;
        for (Certificate certificate : certificates) {
            s = certificate.toString();
            Log.d(TAG, s);
        }

        return response;
    }
}
