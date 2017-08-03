package de.eorlbruder.wallabag_shaarli_connector.standardnotes

import de.eorlbruder.wallabag_shaarli_connector.core.Connector
import de.eorlbruder.wallabag_shaarli_connector.core.Entry
import de.eorlbruder.wallabag_shaarli_connector.core.Sysconfig
import de.eorlbruder.wallabag_shaarli_connector.core.utils.ResponseUtils
import de.eorlbruder.wallabag_shaarli_connector.wallabag.WallabagConnector
import de.eorlbruder.wallbag_shaarli_connector.standardnotes.EntryDecrypter
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
        var response = post(config.STANDARDNOTES_URL + "items/sync",
                headers = headers,
                data = jsonRequestData.toString())
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
            response = post(config.STANDARDNOTES_URL + "items/sync",
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
                    var authHash = it.get("auth_hash")
                    if (authHash !is String) {
                        authHash = ""
                    }
                    val uuid = it.get("uuid") as String
                    val encItemKey = it.get("enc_item_key")
                    var ak = ""
                    var mk = ""
                    if (encItemKey is String) {
                        val decryptedKey = decrypt(encItemKey, uuid = uuid,
                                ak = config.STANDARDNOTES_AUTH_KEY,
                                mk = config.STANDARDNOTES_MASTER_KEY, authHash = authHash)
                        mk = decryptedKey.substring(0, decryptedKey.length / 2)
                        ak = decryptedKey.substring(decryptedKey.length / 2, decryptedKey.length)
                    }
                    val content = it.get("content") as String
                    val decryptedContent = decrypt(content, uuid = uuid, ak = ak, mk = mk,
                            authHash = authHash as String)
                    val description = ""
                    val title = ""
                    val tags = HashSet<String>()
                    tags.add(getName())
                    result.add(Entry(title, tags, description = description))
                    logger.debug("Added Entry with Content $decryptedContent")
                }
            }
        }
        return result
    }

    fun decrypt(encryptedEntry: String, authHash: String = "", uuid: String = "", ak: String = "", mk: String = ""): String {
        if (encryptedEntry.substring(0..2) == "002") {
            return EntryDecrypter.decryptV002(encryptedEntry, ak, mk, uuid)
        } else if (encryptedEntry.substring(0..2) == "001") {
            return EntryDecrypter.decryptV001(encryptedEntry, config.STANDARDNOTES_AUTH_KEY,
                    config.STANDARDNOTES_MASTER_KEY, authHash)
        } else if (encryptedEntry.substring(0..2) == "000") {
            return EntryDecrypter.decryptV000(encryptedEntry)
        }
        throw NoSuchAlgorithmException()
    }

    override fun getAccessToken(): String {
        val response = post(config.STANDARDNOTES_URL + "auth/sign_in",
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