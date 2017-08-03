package de.eorlbruder.wallabag_shaarli_connector.core

import de.eorlbruder.wallabag_shaarli_connector.core.utils.ResponseUtils

interface Connector {
    fun getAllEntries(): List<Entry>

    fun getAuthHeader(): Map<String, String> = ResponseUtils.getAuthorizationHeaderWithToken(getAccessToken())

    fun getAccessToken(): String

    fun writeAllEntries(entries: List<Entry>)

    fun getName(): String
}