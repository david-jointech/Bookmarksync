package de.eorlbruder.wallabag_shaarli_connector.core

import de.eorlbruder.wallabag_shaarli_connector.core.utils.MergeUtils
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
        val targetEntry: Entry? = targetEntries.find { MergeUtils.entryEqual(it, entry) }
        if (targetEntry == null) {
            logger.debug("Entry with URL ${entry.url} not found in ${targetConnector.name}" +
                    ", creating new Entry")
            result.add(entry.copy(id = "no_id"))
        } else {
            if (!MergeUtils.equalTags(targetEntry.tags, entry.tags)) {
                logger.debug("Entry with URL ${entry.url} found in ${targetConnector.name}" +
                        ", but tags appear to have changed, updating Entry")
                val tags = HashSet<String>(entry.tags)
                tags.addAll(targetEntry.tags)
                result.add(entry.copy(id = targetEntry.id, tags = entry.tags))
            } else {
                logger.debug("Entry with URL ${entry.url} found in ${targetConnector.name}" +
                        " and unchanged, doing nothing")
            }
        }
    }


}