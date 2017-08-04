package de.eorlbruder.wallabag_shaarli_connector.core.utils

import de.eorlbruder.wallabag_shaarli_connector.core.ConnectorTypes
import de.eorlbruder.wallabag_shaarli_connector.core.Entry
import mu.KLogging

class MergeUtils {

    companion object : KLogging() {

        val connectorNames: List<String> = ConnectorTypes.values().map { it.value }

        fun containsAllTags(sourceTags: Set<String>, targetTags: Set<String>): Boolean {
            return targetTags.containsAll(sourceTags)
        }

        fun entryEqual(entry0: Entry, entry1: Entry): Boolean {
            if (entry0.url == "" || entry1.url == "") {
                return noteEqual(entry0, entry1)
            }
            return equalUrls(entry0.url, entry1.url)
        }

        private fun noteEqual(entry0: Entry, entry1: Entry): Boolean {
            return false //TODO this is just a stub
        }

        private fun equalUrls(url0: String, url1: String): Boolean {
            val url0_ = escapeUrl(url0)
            val url1_ = escapeUrl(url1)
            return url0_ == url1_
        }

        private fun escapeUrl(url: String): String {
            var url_ = url.replace("&amp;", "&")
            url_ = url_.replace("\\?xtor=.*".toRegex(), "")
            url_ = url_.replace("\\&xtor=.*".toRegex(), "")
            url_ = url_.replace("\\?utm_source=.*".toRegex(), "")
            url_ = url_.replace("\\&utm_source=.*".toRegex(), "")
            if (!url_.endsWith("/")) {
                url_ += "/"
            }
            return url_
        }
    }
}