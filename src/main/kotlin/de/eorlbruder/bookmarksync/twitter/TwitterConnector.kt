package de.eorlbruder.bookmarksync.twitter

import de.eorlbruder.bookmarksync.core.Connector
import de.eorlbruder.bookmarksync.core.ConnectorTypes
import de.eorlbruder.bookmarksync.core.Entry
import mu.KLogging
import twitter4j.Paging
import twitter4j.Status
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

class TwitterConnector : Connector() {

    companion object : KLogging()

    init {
        val cb = ConfigurationBuilder()
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(config.TWITTER_OAUTH_CONSUMER_KEY)
                .setOAuthConsumerSecret(config.TWITTER_OAUTH_CONSUMER_SECRET)
                .setOAuthAccessToken(config.TWITTER_OAUTH_ACCESSTOKEN)
                .setOAuthAccessTokenSecret(config.TWITTER_OAUTH_ACCESSTOKEN_SECRET)
        val twitterFactory = TwitterFactory(cb.build())
        val twitter = twitterFactory.instance
        val favorites = twitter.favorites()
        var i = 1
        do {
            val paging = Paging(i, 200)
            logger.debug("Processing Page 1")
            val favoritesList = favorites.getFavorites(paging)
            favoritesList.map { status ->
                entries.add(statusToEntry(status))
            }
            val userTimeline = twitter.getUserTimeline(paging)
            userTimeline.map { status ->
                if (status.isRetweet) {
                    val retweetedStatus = status.retweetedStatus
                    entries.add(statusToEntry(retweetedStatus))
                } else {
                    entries.add(statusToEntry(status))
                }
            }
            i++
        } while (favoritesList.isNotEmpty() && userTimeline.isNotEmpty())
    }

    private fun statusToEntry(status: Status): Entry {
        val tags = status.hashtagEntities.map { value -> value.text }.toHashSet()
        return Entry("Tweet von ${status.user.screenName}", tags, status.id.toString(), url = "https://twitter.com/${status.user.screenName}/status/${status.id}", description = status.text)
    }

    override fun getAccessToken(): String = throw NotImplementedError("We speak with the Twitter API via an external library")
    override fun writeEntry(entry: Entry, source: String) = throw NotImplementedError("A write isn't implemented for Twitter yet")
    override val name: String = ConnectorTypes.TWITTER.value

    override fun fillRequiredConfig() {
        requiredConfigs.add(config.TWITTER_OAUTH_CONSUMER_KEY)
        requiredConfigs.add(config.TWITTER_OAUTH_CONSUMER_SECRET)
        requiredConfigs.add(config.TWITTER_OAUTH_ACCESSTOKEN)
        requiredConfigs.add(config.TWITTER_OAUTH_ACCESSTOKEN_SECRET)
    }


}