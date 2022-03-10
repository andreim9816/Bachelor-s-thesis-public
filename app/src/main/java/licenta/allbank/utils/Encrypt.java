package licenta.allbank.utils;

public class Encrypt {
//    public static String generateDigest(String payload) throws NoSuchAlgorithmException {
////        echo -n "$payload" | openssl dgst -binary -sha256 | openssl base64
//        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
//        digest.reset();
//        digest.update(payload.getBytes(StandardCharsets.UTF_8));
//        return Base64.getEncoder().encodeToString(digest.digest());
//    }
//
//    public static String generateSignature(Context context, String signingString) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableEntryException, InvalidKeyException, SignatureException {
////        printf %s "$signingString" | openssl dgst -sha256 -sign "${certPath}example_eidas_client_signing.key" | openssl base64 -A
//        KeyStore keyStore = KeyStore.getInstance("BKS");
//        keyStore.load(context.getAssets().open("keystore_ing_signing_certificate.bks"), ServiceServer.getKeystoreSecretIng());
//
//        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(
//                keyStore.aliases().nextElement(),
//                new KeyStore.PasswordProtection(ServiceServer.getKeystoreSecretIng())
//        );
//        PrivateKey privateKey = privateKeyEntry.getPrivateKey();
//
//        Signature signature = Signature.getInstance("SHA256withRSA");
//        signature.initSign(privateKey);
//        signature.update(signingString.getBytes());
//        byte[] stringSignature = signature.sign();
//
//        return Base64.getEncoder().encodeToString(stringSignature);
//    }
//
//    public static String generateSigningString(String method, String endpoint, String date, String digest) {
//        return "(request-target): " + method.toLowerCase() + " " + endpoint
//                + "\n" + "date: " + date
//                + "\n" + "digest: " + digest;
//    }

//    public static String buildSignatureHeader(String signature) {
//        return "keyId=\"5ca1ab1e-c0ca-c01a-cafe-154deadbea75\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"" + signature + "\"";
//    }

    public static String buildBearerTokenHeader(String token) {
        return "Bearer " + token;
    }

//    public static String buildDateHeader(String date) {
//        return date + " GMT";
//    }
//
//    public static String buildDigestHeader(String digest) {
//        return "SHA-256=" + digest;
//    }
}