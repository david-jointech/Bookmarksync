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

    private fun mergeEntry(sourceEntry: Entry, result: ArrayList<Entry>) {
        //TODO needs to work well with Notes too...
        val targetEntry: Entry? = targetEntries.find { MergeUtils.entryEqual(sourceEntry, it) }
        if (targetEntry == null) {
            logger.debug("Entry with URL ${sourceEntry.url} not found in ${targetConnector.name}" +
                    ", creating new Entry")
            result.add(sourceEntry.copy(id = "no_id"))
        } else {
            if (MergeUtils.haveTagsChanged(sourceEntry.tags, targetEntry.tags) ||
                    MergeUtils.hasTitleChanged(sourceEntry.title, targetEntry.title) ||
                    MergeUtils.hasDescriptionChanged(sourceEntry.description, targetEntry.description)) {
                logger.debug("Entry with URL ${sourceEntry.url} found in ${targetConnector.name}" +
                        ", but tags appear to have changed, updating Entry")
                val tags = HashSet<String>(sourceEntry.tags)
                tags.addAll(targetEntry.tags)
                result.add(sourceEntry.copy(id = targetEntry.id, tags = tags))
            } else {
                logger.debug("Entry with URL ${sourceEntry.url} found in ${targetConnector.name}" +
                        " and unchanged, doing nothing")
            }
        }
    }


}