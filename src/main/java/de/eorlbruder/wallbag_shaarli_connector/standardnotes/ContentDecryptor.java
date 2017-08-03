package de.eorlbruder.wallbag_shaarli_connector.standardnotes;

import kotlin.text.Charsets;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class ContentDecryptor {
//
//    public static JSONObject decrypt(JSONObject item) {
//        try {
//
//            if (item.getContent() != null) {
//                String contentJson;
//                String version = item.getContent().substring(0, 3);
//                String contentToDecrypt = item.getContent().substring(3);
//                if (version.equals("000")) {
//                    contentJson = new String(Base64.decode(contentToDecrypt, Base64.NO_PADDING), Charsets.UTF_8);
//                } else if (version.equals("001") || version.equals("002")) {
//                    Keys keys = Crypt.getItemKeys(item, version);
//
//                    if (version.equals("002")) {
//                        String[] contentBits = contentToDecrypt.split(":");
//                        String authHash = contentBits[1];
//                        String uuid = contentBits[2];
//                        String ivHex = contentBits[3];
//                        String cipherText = contentBits[4];
//                        String stringToAuth = version + ":" + uuid + ":" + ivHex + ":" + cipherText;
//
//                        if(!uuid.equals(item.getUuid())) {
//                            throw new Exception("Could not authenticate item");
//                        }
//
//                        // authenticate
//                        String hash = createHash(stringToAuth, keys.ak);
//                        if (!hash.equals(authHash)) {
//                            throw new Exception("could not authenticate item");
//                        }
//
//                        contentJson = Crypt.decrypt(cipherText, keys.ek, ivHex);
//                    } else { // "001"
//                        // authenticate
//                        contentJson = decryptV001(item, contentToDecrypt, keys);
//                    }
//                } else {
//                    throw new RuntimeException("Encryption version " + version + " not supported");
//                }
//
//                T thing = SApplication.Companion.getInstance().getGson().fromJson(contentJson, type);
//                copyInEncryptableItemFields(item, thing, version);
//                return thing;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final IvParameterSpec emptyIvSpec = new IvParameterSpec(new byte[]{0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
    final private static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String decryptV002(String encryptedContent, String ak, String mk, String uuid)
            throws Exception {
        String contentJson;
        String[] contentBits = encryptedContent.split(":");
        String authHash = contentBits[1];
        String contentUuid = contentBits[2];
        String ivHex = contentBits[3];
        String cipherText = contentBits[4];
        String stringToAuth = "002" + ":" + contentUuid + ":" + ivHex + ":" + cipherText;

        if (!contentUuid.equals(uuid)) {
            throw new Exception("Could not authenticate item");
        }

        // authenticate
        String hash = createHash(stringToAuth, ak);
        if (!hash.equals(authHash)) {
            throw new Exception("could not authenticate item with hash " + hash + " against hash "
                    + authHash);
        }

        contentJson = decrypt(cipherText, mk, ivHex);
        return contentJson;
    }


    public static String decryptV000(String encryptedContent)
            throws Exception {
        return new String(DatatypeConverter.parseBase64Binary(encryptedContent), Charsets.UTF_8);
    }

    public static String decryptV001(String encryptedContent, String ak, String mk, String authHash)
            throws Exception {
        String contentJson;
        String hash = createHash(encryptedContent, ak);
        if (!hash.toLowerCase().equals(authHash.toLowerCase())) {
            throw new Exception("could not authenticate item with hash " + hash + " against hash "
                    + authHash);
        }
        contentJson = decrypt(encryptedContent.substring(3), mk, null);
        return contentJson;
    }

    private static String decrypt(String base64Text, String hexKey, String hexIv) throws Exception {
        byte[] base64Data = DatatypeConverter.parseBase64Binary(base64Text);
        Cipher ecipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
        byte[] key = fromHexString(hexKey);
        SecretKey sks = new SecretKeySpec(key, AES);
        ecipher.init(Cipher.DECRYPT_MODE, sks, hexIv == null ? emptyIvSpec :
                new IvParameterSpec(fromHexString(hexIv)));
        byte[] resultData = ecipher.doFinal(base64Data);
        return new String(resultData, Charsets.UTF_8);
    }

    private static String createHash(String text, String ak) throws Exception {
        Mac sha256_HMAC = Mac.getInstance(HMAC_SHA_256);
        byte[] decodedAk = fromHexString(ak);
        SecretKeySpec secretKey = new SecretKeySpec(decodedAk, HMAC_SHA_256);
        sha256_HMAC.init(secretKey);
        byte[] hash = sha256_HMAC.doFinal(text.getBytes(Charsets.UTF_8));
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] fromHexString(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}
