package de.eorlbruder.bookmarksync.standardnotes.util

import mu.KLogging
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

class EntryDecrypter {

    companion object : KLogging() {

        private val AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding"
        private val AES = "AES"
        private val HMAC_SHA_256 = "HmacSHA256"
        private val emptyIvSpec = IvParameterSpec(
                byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00))


        fun decryptV002(encryptedContent: String, ak: String, mk: String, uuid: String): String {
            val contentJson: String
            val contentBits = encryptedContent.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val authHash = contentBits[1]
            val contentUuid = contentBits[2]
            val ivHex = contentBits[3]
            val cipherText = contentBits[4]
            val stringToAuth = "002:$contentUuid:$ivHex:$cipherText"

            if (contentUuid != uuid) {
                throw Exception("Could not authenticate item uuid " + contentUuid + "doesn't mathc" +
                        uuid)
            }


            val hash = createHash(stringToAuth, ak)
            if (hash != authHash) {
                throw Exception("could not authenticate item with hash " + hash + " against hash "
                        + authHash)
            }

            contentJson = decrypt(cipherText, mk, ivHex)
            return contentJson
        }

        fun decryptV000(encryptedContent: String): String {
            return String(DatatypeConverter.parseBase64Binary(encryptedContent), Charsets.UTF_8)
        }

        @Throws(Exception::class)
        fun decryptV001(encryptedContent: String, ak: String, mk: String, authHash: String): String {
            val contentJson: String
            val hash = createHash(encryptedContent, ak)
            if (hash.toLowerCase() != authHash.toLowerCase()) {
                throw Exception("could not authenticate item with hash " + hash + " against hash "
                        + authHash)
            }
            contentJson = decrypt(encryptedContent.substring(3), mk, null)
            return contentJson
        }

        private fun decrypt(base64Text: String, hexKey: String, hexIv: String?): String {
            val base64Data = DatatypeConverter.parseBase64Binary(base64Text)
            val ecipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING)
            val key = DatatypeConverter.parseHexBinary(hexKey)
            val sks = SecretKeySpec(key, AES)
            ecipher.init(Cipher.DECRYPT_MODE, sks, if (hexIv == null)
                emptyIvSpec
            else
                IvParameterSpec(DatatypeConverter.parseHexBinary(hexIv)))
            val resultData = ecipher.doFinal(base64Data)
            return String(resultData, Charsets.UTF_8)
        }

        private fun createHash(text: String, ak: String): String {
            val sha256_HMAC = Mac.getInstance(HMAC_SHA_256)
            val decodedAk = DatatypeConverter.parseHexBinary(ak)
            val secretKey = SecretKeySpec(decodedAk, HMAC_SHA_256)
            sha256_HMAC.init(secretKey)
            val hash = sha256_HMAC.doFinal(text.toByteArray(Charsets.UTF_8))
            return DatatypeConverter.printHexBinary(hash).toLowerCase()
        }
    }

}
