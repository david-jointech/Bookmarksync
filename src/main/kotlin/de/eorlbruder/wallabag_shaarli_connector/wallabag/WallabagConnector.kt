package de.eorlbruder.wallabag_shaarli_connector.wallabag

import de.eorlbruder.wallabag_shaarli_connector.core.*
import de.eorlbruder.wallabag_shaarli_connector.core.utils.ResponseUtils
import khttp.get
import mu.KLogging
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class WallabagConnector : Connector() {

    companion object : KLogging()

    val config : Sysconfig = Sysconfig()

    init {
        var i = 1
        logger.info("Starting to retrieve All Entries from Wallabag")
        var response = get(getEntriesUrlForPage(i),
                headers = getAuthHeader())
        logger.debug("Processing Page $i with Status Code ${response.statusCode}")
        while (ResponseUtils.isSuccessfulStatusCode(response)) {
            val json = JSONObject(response.text)
            pruneEntries(json)
            response = get(getEntriesUrlForPage(++i), headers = getAuthHeader())
            logger.debug("Processing Page $i with Status Code ${response.statusCode}")
        }
        entries.reverse()
    }


    private fun pruneEntries(entries: JSONObject) {
        val embedded = entries.get("_embedded")
        val items = (embedded as JSONObject).get("items")
        (items as JSONArray).forEach({ addEntry(it as JSONObject) })
    }

    private fun addEntry(it: JSONObject) {
        val title = it.get("title") as String
        val url = it.get("url") as String
        val tags = extractTags(it.get("tags") as JSONArray)
        val id = it.get("id") as Int
        if (it.get("is_starred") == 1) {
            tags.add("Starred")
        }
        entries.add(Entry(title, tags, id.toString(), url = url))
    }

    private fun extractTags(tags: JSONArray): HashSet<String> {
        val result = HashSet<String>()
        tags.forEach({ addTag(result, it as JSONObject) })
        return result
    }

    private fun addTag(list: HashSet<String>, it: JSONObject) {
        val label = it.get("label")
        list.add(label as String)
    }

    override fun getAccessToken(): String {
        val response = get(getAuthorizationUrl())
        if (ResponseUtils.isSuccessfulStatusCode(response)) {
            return response.jsonObject.get(Constants.WALLABAG_ACCESS_TOKEN).toString()
        }
        throw Exception()
    }

    override fun writeEntry(entry: Entry, source: String) = throw NotImplementedError("A write isn't implemented for Wallabag yet")
    private fun getEntriesUrlForPage(i: Int) = config.WALLABAG_URL + Constants.WALLABAG_API_ENDPOINT + Constants.WALLABAG_ENTRIES + Constants.WALLABAG_PAGE_KEY + i

    private fun getAuthorizationUrl() : String {
        var result = config.WALLABAG_URL + Constants.WALLABAG_AUTH_ENDPOINT + Constants.WALLABAG_GRANT_TYPE
        result += Constants.WALLABAG_CLIENT_ID_KEY + config.WALLABAG_CLIENT_ID
        result += Constants.WALLABAG_CLIENT_SECRET_KEY + config.WALLABAG_CLIENT_SECRET
        result += Constants.WALLABAG_USERNAME_KEY + config.WALLABAG_USERNAME
        result += Constants.WALLABAG_PASSWORD_KEY + config.WALLABAG_PASSWORD
        return result
    }

    override val name: String = ConnectorTypes.WALLABAG.value
}