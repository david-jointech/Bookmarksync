package de.eorlbruder.wallabag_shaarli_connector.core.utils

import de.eorlbruder.wallabag_shaarli_connector.core.ConnectorTypes
import de.eorlbruder.wallabag_shaarli_connector.core.Entry
import mu.KLogging

class MergeUtils {

    companion object : KLogging() {

        val connectorNames: List<String> = ConnectorTypes.values().map { it.value }

        fun haveTagsChanged(sourceTags: Set<String>, targetTags: Set<String>): Boolean {
            return !targetTags.containsAll(sourceTags)
        }

        fun hasTitleChanged(sourceTitle: String, targetTitle: String): Boolean {
            return false // TODO implement
        }

        fun hasDescriptionChanged(sourceDescription: String, targetTags: String): Boolean {
            return false // TODO implement
        }

        fun entryEqual(sourceEntry: Entry, targetEntry: Entry): Boolean {
            if (sourceEntry.url == "") {
                return noteEqual(sourceEntry, targetEntry)
            }
            return equalUrls(sourceEntry.url, targetEntry.url)
        }

        private fun noteEqual(sourceEntry: Entry, targetEntry: Entry): Boolean {
            return targetEntry.tags.contains("000_${sourceEntry.id}")
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