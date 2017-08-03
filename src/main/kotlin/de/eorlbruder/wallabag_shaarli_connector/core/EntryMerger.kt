package de.eorlbruder.wallabag_shaarli_connector.core

import mu.KLogging

class EntryMerger(sourceConnector: Connector, private val targetConnector: Connector) {

    companion object : KLogging()

    val sourceEntries: List<Entry>
    val targetEntries: List<Entry>

    init {
        targetEntries = targetConnector.entries
        sourceEntries = sourceConnector.entries
    }

    fun mergeEntries() {
        val result = ArrayList<Entry>()
        sourceEntries.forEach { mergeEntry(it, result) }
        targetConnector.entriesToSync = result
    }

    private fun mergeEntry(entry: Entry, result: ArrayList<Entry>) {
        //TODO needs to work well with Notes too...
        val targetEntry: Entry? = targetEntries.find {
            (((it.url == "" || entry.url == "") && it.title == entry.title)
                    || equalUrls(it.url, entry.url))
        }
        if (targetEntry == null) {
            logger.debug("Entry with URL ${entry.url} not found in ${targetConnector.name}" +
                    ", creating new Entry")
            result.add(entry)
        } else {
            if (targetEntry.tags != entry.tags) {
                logger.debug("Entry with URL ${entry.url} found in ${targetConnector.name}" +
                        ", but tags appear to have changed, updating Entry")
                result.add(entry.copy(id = targetEntry.id))
            } else {
                logger.debug("Entry with URL ${entry.url} found in ${targetConnector.name}" +
                        " and unchanged, doing nothing")
            }
        }
    }

    fun equalUrls(url0: String, url1: String): Boolean {
        val url0_ = escapeUrl(url0)
        val url1_ = escapeUrl(url1)
        return url0_ == url1_
    }

    private fun escapeUrl(url: String): String {
        var url_ = url.replace("&amp;", "&")
        url_ = url_.replace("\\?xtor=.*".toRegex(),"")
        url_ = url_.replace("\\&xtor=.*".toRegex(),"")
        url_ = url_.replace("\\?utm_source=.*".toRegex(),"")
        url_ = url_.replace("\\&utm_source=.*".toRegex(),"")
        if (!url_.endsWith("/")) {
            url_ += "/"
        }
        return url_
    }
}