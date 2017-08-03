package de.eorlbruder.wallbag_shaarli_connector.standardnotes;

import kotlin.text.Charsets;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class EntryDecrypter {

    private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final IvParameterSpec emptyIvSpec = new IvParameterSpec(new byte[]{0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});

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
            throw new Exception("Could not authenticate item, mismatching uuid");
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
        byte[] key = DatatypeConverter.parseHexBinary(hexKey);
        SecretKey sks = new SecretKeySpec(key, AES);
        ecipher.init(Cipher.DECRYPT_MODE, sks, hexIv == null ? emptyIvSpec :
                new IvParameterSpec(DatatypeConverter.parseHexBinary(hexIv)));
        byte[] resultData = ecipher.doFinal(base64Data);
        return new String(resultData, Charsets.UTF_8);
    }

    private static String createHash(String text, String ak) throws Exception {
        Mac sha256_HMAC = Mac.getInstance(HMAC_SHA_256);
        byte[] decodedAk = DatatypeConverter.parseHexBinary(ak);
        SecretKeySpec secretKey = new SecretKeySpec(decodedAk, HMAC_SHA_256);
        sha256_HMAC.init(secretKey);
        byte[] hash = sha256_HMAC.doFinal(text.getBytes(Charsets.UTF_8));
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }

}
