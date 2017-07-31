package de.eorlbruder.wallabag_shaarli_connector.standardnotes

import de.eorlbruder.wallabag_shaarli_connector.core.Connector
import de.eorlbruder.wallabag_shaarli_connector.core.Entry
import de.eorlbruder.wallabag_shaarli_connector.core.Sysconfig
import mu.KLogging

class StandardnotesConnector(isSource: Boolean) : Connector {

    companion object : KLogging()

    val config : Sysconfig = Sysconfig()

    override fun getAllEntries(): List<Entry> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAccessToken(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeAllEntries(entries: List<Entry>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getName(): String = "Standardnotes"
}