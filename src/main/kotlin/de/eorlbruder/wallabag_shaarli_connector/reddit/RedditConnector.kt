package de.eorlbruder.wallabag_shaarli_connector.reddit

import de.eorlbruder.wallabag_shaarli_connector.core.Connector
import de.eorlbruder.wallabag_shaarli_connector.core.Entry
import de.eorlbruder.wallabag_shaarli_connector.core.Sysconfig
import khttp.get
import khttp.post
import khttp.structures.authorization.BasicAuthorization
import mu.KLogging
import org.json.JSONArray
import org.json.JSONObject

class RedditConnector(val isSource: Boolean) : Connector {

    companion object : KLogging()

    val config: Sysconfig = Sysconfig()

    override fun getAllEntries(): List<Entry> {
        val headers = HashMap(getAuthHeader())
        headers.put("User-Agent", "WallabagShaarliConnector/0.1 by EorlBruder")
        val response = get("${config.REDDIT_OAUTHURL}user/${config.REDDIT_USERNAME}/saved",
                headers = headers)
        // TODO Listing stuff! https://www.reddit.com/dev/api#listings
        val responseJson = JSONObject(response.text)
        return pruneEntries(responseJson)
    }

    fun pruneEntries(json: JSONObject): List<Entry> {
        val result: List<Entry> = ArrayList<Entry>()
        val allData = json.get("data") as JSONObject
        val dataArray = allData.get("children") as JSONArray
        dataArray.forEach {
            val data = (it as JSONObject).get("data") as JSONObject
            val tags = ArrayList<String>()
            tags.add(getName())
            logger.debug(data.toString())
            // TODO get the specs of the returned data, else this will get really tedious
            val url = data.get("link_permalink") as String
            val title = data.get("link_title") as String
        }
        return result
    }


    override fun getAccessToken(): String {
        val params = HashMap<String, String>()
        params.put("grant_type", "password")
        params.put("username", config.REDDIT_USERNAME)
        params.put("password", config.REDDIT_PASSWORD)
        val auth = BasicAuthorization(config.REDDIT_CLIENT_ID, config.REDDIT_CLIENT_SECRET)
        val headers = mapOf("Content-Type" to "application/x-www-form-urlencoded")
        val response = post("${config.REDDIT_URL}api/v1/access_token",
                data = params, auth = auth, headers = headers)
        val json = JSONObject(response.text)
        return json.get("access_token") as String
    }
    override fun writeAllEntries(entries: List<Entry>) {
        throw NotImplementedError("Using reddit-saved as an archived for other links doesn't seem to " +
                "make that much sense")
    }

    override fun getName(): String = "Reddit"
}