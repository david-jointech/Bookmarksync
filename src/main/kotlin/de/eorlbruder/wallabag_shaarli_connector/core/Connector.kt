package de.eorlbruder.wallabag_shaarli_connector.core

import de.eorlbruder.wallabag_shaarli_connector.core.utils.ResponseUtils
import de.eorlbruder.wallabag_shaarli_connector.shaarli.ShaarliConnector

abstract class Connector {

    val entries = ArrayList<Entry>()
    var entriesToSync = ArrayList<Entry>()
    abstract val name: String

    fun write(source: String) {
        ShaarliConnector.logger.info("Writing all retrieved and modified Entries to $name")
        entriesToSync.forEach { writeEntry(it, source) }
    }


    protected fun getAuthHeader(): Map<String, String> = ResponseUtils.getAuthorizationHeaderWithToken(getAccessToken())

    protected abstract fun getAccessToken(): String

    protected abstract fun writeEntry(entry: Entry, source: String)

}