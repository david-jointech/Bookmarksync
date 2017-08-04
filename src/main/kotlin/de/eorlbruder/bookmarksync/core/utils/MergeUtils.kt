package de.eorlbruder.bookmarksync.core.utils

import de.eorlbruder.bookmarksync.core.ConnectorTypes
import de.eorlbruder.bookmarksync.core.Entry
import mu.KLogging
import org.apache.commons.text.StringEscapeUtils

class MergeUtils {

    companion object : KLogging() {

        val connectorNames: List<String> = ConnectorTypes.values().map { it.value }

        fun haveTagsChanged(sourceTags: Set<String>, targetTags: Set<String>): Boolean {
            return !targetTags.containsAll(sourceTags)
        }

        fun hasTitleChanged(sourceTitle: String, targetTitle: String): Boolean {
            val unescapedSourceTitle = StringEscapeUtils.unescapeHtml4(sourceTitle)
            val unescapedTargetTitle = StringEscapeUtils.unescapeHtml4(targetTitle)
            return unescapedSourceTitle != unescapedTargetTitle
        }

        fun hasDescriptionChanged(sourceDescription: String, targetDescription: String): Boolean {
            // If the Source has an Empty Description we don't want to override anything
            if (sourceDescription == "")
                return false
            val unescapedSourceDescription = StringEscapeUtils.unescapeHtml4(sourceDescription)
            val unescapedTargetDescription = StringEscapeUtils.unescapeHtml4(targetDescription)
            return unescapedSourceDescription != unescapedTargetDescription
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
            var url_ = StringEscapeUtils.unescapeHtml4(url)
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