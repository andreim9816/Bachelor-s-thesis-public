package licenta.allbank.utils.security;

import org.tomitribe.auth.signatures.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static licenta.allbank.utils.security.Secret.REQUEST_SIGNING_SECRET;

public class HttpSigning {

    /**
     * @param payload www-url payload encoded
     * @return paylaod digest after being hashed using SHA-256
     * @throws NoSuchAlgorithmException
     */
    public static String generateDigest(String payload) throws NoSuchAlgorithmException {
//        echo -n "$payload" | openssl dgst -binary -sha256 | openssl base64
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(payload.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.encodeBase64(digest.digest()));
    }

    /**
     * @param digest
     * @return digest header used when generating <code>signing string</code>
     */
    public static String buildDigestHeader(String digest) {
        return "SHA-256=" + digest;
    }

    /**
     * @param method
     * @param endpoint
     * @param date
     * @param digest
     * @return Signing string used for building signature
     */
    public static String generateSigningString(String method, String endpoint, String date, String digest) {
        return "(request-target): " + method.toLowerCase() + " " + endpoint
                + "\n" + "date: " + date
                + "\n" + "digest: " + digest;
    }

    /**
     * Generates signature using HMAC-SHA256 algorithm
     *
     * @param signingString
     * @return signature
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String generateSignature(String signingString) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(REQUEST_SIGNING_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        final byte[] signedBytes = mac.doFinal(signingString.getBytes(StandardCharsets.UTF_8));

        return new String(Base64.encodeBase64(signedBytes), StandardCharsets.UTF_8);
    }

    /**
     * Function that build signature header
     *
     * @param signature
     * @param keyId
     * @return
     */
    public static String buildSignatureHeader(String signature, String keyId) {
        return "keyId=\"" + keyId + "\",algorithm=\"hmac-sha256\",headers=\"(request-target) date digest\",signature=\"" + signature + "\"";
    }

    /**
     * Function that generates signature header used in http signing
     *
     * @param method
     * @param url
     * @param payload
     * @return
     */
    public static String generateSignatureHeader(String method, String url, String payload, String today) {
        try {
            String digestHeader = buildDigestHeader(generateDigest(payload));
            String signingString = generateSigningString(method, url, today, digestHeader);
            String signature = generateSignature(signingString);

            return buildSignatureHeader(signature, Secret.REQUEST_KEY_ID);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return null;
        }
    }

    /**
     * Function that generates a www-form-urlencoded payload
     *
     * @param map Payload map
     * @return
     */
    public static String generatePayload(Map<String, String> map) {
        StringBuilder result = new StringBuilder("");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue().replaceAll(" ", "+");
            result.append(entry.getKey() + "=" + value + "&");
        }

        if (result.toString().equalsIgnoreCase("")) {
            return "";
        }
        return result.substring(0, result.length() - 1);
    }

//    public static String generateSignatureHeader(String method, String uri, String digest) {
//        final Date today = new Date(); // default window is 1 hour
//        final String stringToday = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).format(today);
//
//        final Signature signature = new Signature("key-alias", "hs2019", "hmac-sha256", null, null);
//        final Key key = new SecretKeySpec("secret".getBytes(), "HmacSHA256");
//        final Signer signer = new Signer(key, signature); // (3)
//
//        Map<String, String> headers = Map.of(
//                "(request-target)", method.toUpperCase() + " " + uri,
//                "Date", stringToday,
//                "Digest", digest
//        );
//        try {
//            Signature signed = signer.sign(method, uri, headers);
//            String signedString = signed.toString();
//            System.out.println(signedString);
//            return signedString;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static String generateDigestHeader(String digest) {
        return "SHA-256=" + digest;
    }
}
