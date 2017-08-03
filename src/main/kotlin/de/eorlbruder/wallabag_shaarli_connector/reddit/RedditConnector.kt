package de.eorlbruder.wallabag_shaarli_connector.reddit

import de.eorlbruder.wallabag_shaarli_connector.core.Connector
import de.eorlbruder.wallabag_shaarli_connector.core.Entry
import de.eorlbruder.wallabag_shaarli_connector.core.Sysconfig
import khttp.get
import khttp.post
import khttp.structures.authorization.BasicAuthorization
import mu.KLogging
import org.json.JSONObject

class RedditConnector(val isSource: Boolean) : Connector {

    companion object : KLogging()

    val config: Sysconfig = Sysconfig()

    override fun getAllEntries(): List<Entry> {
        val headers = HashMap(getAuthHeader())
        headers.put("User-Agent", "WallabagShaarliConnector/0.1 by EorlBruder")
        val response = get("${config.REDDIT_OAUTHURL}user/${config.REDDIT_USERNAME}/saved",
                headers = headers)
        logger.debug(response.text)
        return ArrayList<Entry>()
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getName(): String = "Reddit"
}