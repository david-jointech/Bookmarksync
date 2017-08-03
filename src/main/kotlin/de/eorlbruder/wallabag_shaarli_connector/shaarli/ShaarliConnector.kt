package de.eorlbruder.wallabag_shaarli_connector.shaarli

import de.eorlbruder.wallabag_shaarli_connector.core.Connector
import de.eorlbruder.wallabag_shaarli_connector.core.Constants
import de.eorlbruder.wallabag_shaarli_connector.core.Entry
import de.eorlbruder.wallabag_shaarli_connector.core.Sysconfig
import de.eorlbruder.wallabag_shaarli_connector.core.utils.ResponseUtils
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import khttp.get
import khttp.post
import khttp.put
import mu.KLogging
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ShaarliConnector(isSource: Boolean) : Connector(isSource) {

    companion object : KLogging()

    val config: Sysconfig = Sysconfig()

    init {
        var offset = 0
        logger.info("Getting all Shaarli Entries imported from Wallabag")
        var response = get(getEntriesUrlForOffset(offset),
                headers = getAuthHeader())
        logger.debug("Processing Page on $offset with Status Code ${response.statusCode}")
        while (ResponseUtils.isSuccessfulStatusCode(response)) {
            val jsonArray = JSONArray(response.text)
            if (jsonArray.length() < 1) {
                break
            }
            jsonArray.forEach({ pruneEntry(it as JSONObject) })
            offset += Constants.SHAARLI_LIMIT
            response = get(getEntriesUrlForOffset(offset), headers = getAuthHeader())
            logger.debug("Processing Page on $offset with Status Code ${response.statusCode}")
        }
    }

    private fun pruneEntry(entry: JSONObject) {
        val id = entry.get("id") as Int
        val title = entry.get("title") as String
        val url = entry.get("url") as String
        val tags = extractTags(entry.get("tags") as JSONArray)
        if (isSource) {
            entries.add(Entry(title, tags, url = url))
        } else {
            entries.add(Entry(title, tags, url = url, id = id))
        }
    }

    private fun extractTags(tags: JSONArray): HashSet<String> {
        val result = HashSet<String>()
        tags.forEach({ result.add(it as String) })
        return result
    }


    private fun getEntriesUrlForOffset(offset: Int): String {
        var result = getEntriesUrl()
        result += Constants.SHAARLI_LIMIT_KEY + Constants.SHAARLI_LIMIT
        result += Constants.SHAARLI_OFFSET_KEY + offset
        result += Constants.SHAARLI_SEARCHTAGS_KEY + Constants.WALLABAG_TAG
        return result
    }

    private fun getEntriesUrl() = config.SHAARLI_URL + Constants.SHAARLI_API_ENDPOINT + Constants.SHAARLI_ENTRIES

    override fun getAccessToken(): String {
        val iat = Date()
        val signingKey = config.SHAARLI_SECRET.toByteArray()
        val token = Jwts.builder()
                .setIssuedAt(iat)
                .setHeaderParam("typ", "JWT")
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact()

        assert(Jwts.parser().setSigningKey(signingKey)
                .parseClaimsJws(token).body.issuedAt == iat)

        return token
    }

    override fun writeEntry(entry: Entry) {
        val json: JSONObject = jsonObjectFromEntry(entry)
        if (entry.id == -1) {
            logger.info("Creating new Entry with URL ${entry.url} in Shaarli")
            createEntry(json)
        } else {
            logger.info("Updating Entry with URL ${entry.url} in Shaarli")
            updateEntry(json, entry.id)
        }
    }

    fun createEntry(json: JSONObject) {
        val response = post(getEntriesUrl(), headers = getAuthHeader(), json = json)
        logger.debug(response.text)
    }

    private fun jsonObjectFromEntry(entry: Entry): JSONObject {
        val json: JSONObject = JSONObject()
        json.put("url", entry.url)
        json.put("title", entry.title)
        json.put("description", "")
        val tags: JSONArray = JSONArray()
        entry.tags.forEach({ tags.put(it) })
        json.put("tags", tags)
        json.put("private", true)
        return json
    }

    private fun updateEntry(json: JSONObject, id: Int) {
        val response = put(getEntriesUrl() + "/$id", headers = getAuthHeader(), json = json)
        logger.debug(response.text)
    }

    override val name: String = "Shaarli"
}