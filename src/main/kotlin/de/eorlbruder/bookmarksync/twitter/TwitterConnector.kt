package de.eorlbruder.bookmarksync.twitter

import de.eorlbruder.bookmarksync.core.Connector
import de.eorlbruder.bookmarksync.core.ConnectorTypes
import de.eorlbruder.bookmarksync.core.Entry
import mu.KLogging

class TwitterConnector : Connector() {

    companion object : KLogging()

    init {

    }

    override fun getAccessToken(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeEntry(entry: Entry, source: String) = throw NotImplementedError("A write isn't implemented for Twitter yet")
    override val name: String = ConnectorTypes.TWITTER.value

    override fun fillRequiredConfig() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}