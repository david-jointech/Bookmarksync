package de.eorlbruder.wallabag_shaarli_connector

import de.eorlbruder.wallabag_shaarli_connector.utils.ResponseUtils

interface Connector {
    abstract fun getAllEntries(): List<Entry>

    fun getAuthHeader(): Map<String, String> = ResponseUtils.getAuthorizationHeaderWithToken(getAccessToken())

    abstract fun getAccessToken(): String

    abstract fun writeAllEntries(entries: List<Entry>)
}