package de.eorlbruder.wallabag_shaarli_connector.standardnotes

import de.eorlbruder.wallabag_shaarli_connector.core.Connector
import de.eorlbruder.wallabag_shaarli_connector.core.Entry
import de.eorlbruder.wallabag_shaarli_connector.core.Sysconfig
import de.eorlbruder.wallabag_shaarli_connector.utils.ResponseUtils
import de.eorlbruder.wallabag_shaarli_connector.wallabag.WallabagConnector
import de.eorlbruder.wallbag_shaarli_connector.standardnotes.ContentDecryptor
import khttp.post
import mu.KLogging
import org.json.JSONArray
import org.json.JSONObject
import java.security.NoSuchAlgorithmException


class StandardnotesConnector(isSource: Boolean) : Connector {

    companion object : KLogging()

    val config : Sysconfig = Sysconfig()

    override fun getAllEntries(): List<Entry> {
        logger.info("Starting to extract all Entries from Standardnotes")
        val jsonRequestData = JSONObject()
        jsonRequestData.put("limit", 30)
        val headers = HashMap(getAuthHeader())
        headers.put("Content-type", "application/json")
        var response = post(config.STANDARDNOTES_URL + "/items/sync",
                headers = headers,
                data = jsonRequestData.toString())
        logger.debug(response.text)
        val result = java.util.ArrayList<Entry>()
        WallabagConnector.logger.debug("Processing Page with Status Code ${response.statusCode}")
        while (ResponseUtils.isSuccessfulStatusCode(response)) {
            val json = JSONObject(response.text)
            result.addAll(pruneAndExtractEntries(json))
            val cursorToken = json.get("cursor_token")
            if (cursorToken == JSONObject.NULL) {
                break
            }
            jsonRequestData.put("cursor_token", cursorToken as String)
            response = post(config.STANDARDNOTES_URL + "/items/sync",
                    headers = headers,
                    data = jsonRequestData.toString())
            WallabagConnector.logger.debug("Processing Page with Status Code ${response.statusCode}")
        }
        return result
    }

    fun pruneAndExtractEntries(json: JSONObject): List<Entry> {
        val result = ArrayList<Entry>()
        val retrievedItems = json.get("retrieved_items") as JSONArray
        retrievedItems.forEach {
            if (it is JSONObject) {
                val contentType = it.get("content_type") as String
                val deleted = it.get("deleted") as Boolean
                if (contentType == "Note" && !deleted) {
                    logger.debug(it.toString())
                    val authHash = it.get("auth_hash")
                    val uuid = it.get("uuid") as String
                    val content = it.get("content") as String
                    if (authHash is String) {
                        decrypt(content, authHash = authHash)
                    } else {
                        decrypt(content, uuid = uuid)
                    }
                    val description = ""
                    val title = ""
                    val tags = HashSet<String>()
                    tags.add(getName())
                    result.add(Entry(title, tags, description = description))
                    logger.debug("Added Entry with Content $content")
                }
            }
        }
        return result
    }

    fun decrypt(encryptedEntry: String, authHash: String = "", uuid: String = ""): String {
        if (encryptedEntry.substring(0..2) == "002") {
            return decryptWithV002(encryptedEntry, uuid)
        } else if (encryptedEntry.substring(0..2) == "001") {
            return decryptWithV001(encryptedEntry, authHash)
        } else if (encryptedEntry.substring(0..2) == "000") {
            return decryptWithV000(encryptedEntry)
        }
        throw NoSuchAlgorithmException()
    }

    fun decryptWithV001(encryptedEntry: String, authHash: String): String {
        return ContentDecryptor.decryptV001(encryptedEntry, config.STANDARDNOTES_AUTH_KEY,
                config.STANDARDNOTES_MASTER_KEY, authHash)
    }

    fun decryptWithV002(encryptedEntry: String, uuid: String): String {
        return ContentDecryptor.decryptV002(encryptedEntry, config.STANDARDNOTES_AUTH_KEY,
                config.STANDARDNOTES_MASTER_KEY, uuid)
    }

    fun decryptWithV000(encryptedEntry: String): String {
        return ContentDecryptor.decryptV000(encryptedEntry)
    }

    override fun getAccessToken(): String {
        val response = post(config.STANDARDNOTES_URL + "/auth/sign_in",
                params = mapOf("email" to config.STANDARDNOTES_EMAIL,
                        "password" to config.STANDARDNOTES_SERVER_KEY))
        val token = JSONObject(response.text).get("token") as String
        return token
    }

    override fun writeAllEntries(entries: List<Entry>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getName(): String = "Standardnotes"
}