package de.eorlbruder.wallabag_shaarli_connector.wallabag

import de.eorlbruder.wallabag_shaarli_connector.Connector
import de.eorlbruder.wallabag_shaarli_connector.Entry
import de.eorlbruder.wallabag_shaarli_connector.Constants
import de.eorlbruder.wallabag_shaarli_connector.Sysconfig
import de.eorlbruder.wallabag_shaarli_connector.utils.ResponseUtils
import khttp.get
import mu.KLogging
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class WallabagConnector : Connector {

    companion object : KLogging()

    val config : Sysconfig = Sysconfig()

    override fun getAllEntries(): List<Entry> {
        var i = 1
        logger.info("Starting to retrieve All Entries from Wallabag")
        var response = get(getEntriesUrlForPage(i),
                headers = getAuthHeader())
        val result = ArrayList<Entry>()
        logger.debug("Processing Page $i with Status Code ${response.statusCode}")
        while (ResponseUtils.isSuccessfulStatusCode(response)) {
            val json = JSONObject(response.text)
            result.addAll(pruneEntries(json))
            response = get(getEntriesUrlForPage(++i), headers = getAuthHeader())
            logger.debug("Processing Page $i with Status Code ${response.statusCode}")
        }
        return result
    }


    private fun pruneEntries(entries: JSONObject): ArrayList<Entry> {
        val embedded = entries.get("_embedded")
        val list = ArrayList<Entry>()
        val items = (embedded as JSONObject).get("items")
        (items as JSONArray).forEach({ addEntry(list, it as JSONObject) })
        return list
    }

    private fun addEntry(list: ArrayList<Entry>, it: JSONObject) {
        val title = it.get("title") as String
        val url = it.get("url") as String
        val tags = extractTags(it.get("tags") as JSONArray)
        if (it.get("is_starred") == 1) {
            tags.add("Starred")
        }
        list.add(Entry(title, url, tags))
    }

    private fun extractTags(tags: JSONArray): HashSet<String> {
        val result = HashSet<String>()
        result.add(Constants.WALLABAG_TAG)
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

    override fun writeAllEntries(entries: List<Entry>) = throw NotImplementedError()
    private fun getEntriesUrlForPage(i: Int) = config.WALLABAG_URL + Constants.WALLABAG_API_ENDPOINT + Constants.WALLABAG_ENTRIES + Constants.WALLABAG_PAGE_KEY + i

    private fun getAuthorizationUrl() : String {
        var result = config.WALLABAG_URL + Constants.WALLABAG_AUTH_ENDPOINT + Constants.WALLABAG_GRANT_TYPE
        result += Constants.WALLABAG_CLIENT_ID_KEY + config.WALLABAG_CLIENTID
        result += Constants.WALLABAG_CLIENT_SECRET_KEY + config.WALLABAG_CLIENTSECRET
        result += Constants.WALLABAG_USERNAME_KEY + config.WALLABAG_USERNAME
        result += Constants.WALLABAG_PASSWORD_KEY + config.WALLABAG_PASSWORD
        return result
    }

}