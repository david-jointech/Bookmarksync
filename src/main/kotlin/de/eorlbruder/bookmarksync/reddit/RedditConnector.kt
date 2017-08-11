package de.eorlbruder.bookmarksync.reddit

import de.eorlbruder.bookmarksync.core.Connector
import de.eorlbruder.bookmarksync.core.ConnectorTypes
import de.eorlbruder.bookmarksync.core.Entry
import de.eorlbruder.bookmarksync.core.utils.ResponseUtils
import khttp.get
import khttp.post
import khttp.structures.authorization.BasicAuthorization
import mu.KLogging
import org.json.JSONArray
import org.json.JSONObject

class RedditConnector : Connector() {

    companion object : KLogging()

    init {
        logger.info("Starting to retrieve All Entries from Reddit")
        checkRequiredConfig()
        val headers = HashMap(getAuthHeader())
        headers.put("User-Agent", "Bookmarksync/0.1 by EorlBruder")
        var response = get("${config.REDDIT_OAUTHURL}user/${config.REDDIT_USERNAME}/saved",
                headers = headers)
        while (ResponseUtils.isSuccessfulStatusCode(response)) {
            val responseJson = JSONObject(response.text)
            val allData = responseJson.get("data") as JSONObject
            val after = allData.get("after")
            pruneEntries(allData)
            if (after is String) {
                val params = HashMap<String, String>()
                params.put("after", after)
                params.put("count", entries.size.toString())
                val rateLimitRemaining = response.headers.get("X-Ratelimit-Remaining") as String
                logger.debug("Processing Page $after with Status Code ${response.statusCode}, " +
                        "already fetched ${entries.size} Entries")
                if (rateLimitRemaining.toDouble() < 1) {
                    val secondsTillReset = response.headers.get("X-Ratelimit-Reset")
                    Thread.sleep(Integer.parseInt(secondsTillReset) * 1000L)
                }
                response = get("${config.REDDIT_OAUTHURL}user/${config.REDDIT_USERNAME}/saved",
                        headers = headers, params = params)
            } else {
                break
            }
        }
        entries.reverse()
    }

    fun pruneEntries(json: JSONObject) {
        val dataArray = json.get("children") as JSONArray
        dataArray.forEach {
            val type = (it as JSONObject).get("kind") as String
            val data = it.get("data") as JSONObject
            val id = data.get("id") as String
            val url: String
            val title: String
            val tags = HashSet<String>()
            if (type == "t1") {
                url = (data.get("link_permalink") as String) + id
                title = "Comment to: " + data.get("link_title") as String
                tags.add(data.get("subreddit") as String)
            } else if (type == "t3") {
                url = config.REDDIT_URL + (data.get("permalink") as String).substring(1)
                title = data.get("title") as String
                tags.add(data.get("subreddit") as String)
            } else {
                throw Exception("Unknown Type")
            }
            entries.add(Entry(title, tags, id, url = url))
        }
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

    override fun writeEntry(entry: Entry, source: String) {
        throw NotImplementedError("Using reddit-saved as an archived for other links doesn't seem to " +
                "make that much sense")
    }

    override val name: String = ConnectorTypes.REDDIT.value

    override fun fillRequiredConfig() {
        requiredConfigs.add(config.REDDIT_URL)
        requiredConfigs.add(config.REDDIT_USERNAME)
        requiredConfigs.add(config.REDDIT_PASSWORD)
        requiredConfigs.add(config.REDDIT_OAUTHURL)
        requiredConfigs.add(config.REDDIT_CLIENT_ID)
        requiredConfigs.add(config.REDDIT_CLIENT_SECRET)
    }
}