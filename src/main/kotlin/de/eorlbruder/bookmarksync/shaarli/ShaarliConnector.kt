package de.eorlbruder.bookmarksync.shaarli

import de.eorlbruder.bookmarksync.core.Connector
import de.eorlbruder.bookmarksync.core.ConnectorTypes
import de.eorlbruder.bookmarksync.core.Constants
import de.eorlbruder.bookmarksync.core.Entry
import de.eorlbruder.bookmarksync.core.utils.ResponseUtils
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import khttp.delete
import khttp.get
import khttp.post
import khttp.put
import mu.KLogging
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ShaarliConnector : Connector() {

    companion object : KLogging()

    init {
        var offset = 0
        checkRequiredConfig()
        logger.info("Starting to retrieve All Entries from Shaarli")
        checkRequiredConfig()
        // Todo give params as params and not in url
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
        entries.reverse()
    }

    private fun pruneEntry(entry: JSONObject) {
        val id = entry.get("id") as Int
        val title = entry.get("title") as String
        val description = entry.get("description") as String
        val url = entry.get("url") as String
        val tags = extractTags(entry.get("tags") as JSONArray)
        entries.add(Entry(title, tags, id.toString(), url = url, description = description))
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

    override fun writeEntry(entry: Entry, source: String) {
        val json: JSONObject = jsonObjectFromEntry(entry, source)
        if (entry.id == "no_id") {
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

    private fun jsonObjectFromEntry(entry: Entry, source: String): JSONObject {
        val json: JSONObject = JSONObject()
        json.put("url", entry.url)
        json.put("title", entry.title)
        json.put("description", entry.description)
        val tags: JSONArray = JSONArray()
        tags.put(source)
        entry.tags.forEach({ tags.put(it) })
        json.put("tags", tags)
        json.put("private", true)
        return json
    }

    private fun updateEntry(json: JSONObject, id: String) {
        val response = put(getEntriesUrl() + "/${Integer.parseInt(id)}", headers = getAuthHeader(), json = json)
        logger.debug(response.text)
    }

    override val name: String = ConnectorTypes.SHAARLI.value

    fun deleteAllEntriesWithTag(tag: String) {
        val entriesWithTag = entries.filter { it.tags.contains(tag) }
        logger.debug(entriesWithTag.size.toString())
        entriesWithTag.forEach {
            val response = delete(getEntriesUrl() + "/${Integer.parseInt(it.id)}", headers = getAuthHeader())
            logger.debug("Deleted entry ${it.id} with status code: ${response.statusCode}")
        }
    }

    override fun fillRequiredConfig() {
        requiredConfigs.add(config.SHAARLI_URL)
        requiredConfigs.add(config.SHAARLI_SECRET)
    }
}