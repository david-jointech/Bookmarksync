package de.eorlbruder.bookmarksync.core

import de.eorlbruder.bookmarksync.core.utils.MergeUtils
import mu.KLogging

class EntryMerger(sourceConnector: Connector, private val targetConnector: Connector) {

    companion object : KLogging()

    val sourceEntries: Set<Entry>
    val targetEntries: Set<Entry>

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
        val targetEntry: Entry? = targetEntries.find { MergeUtils.entryEqual(sourceEntry, it) }
        if (targetEntry == null) {
            logger.debug("Entry with URL ${sourceEntry.url} not found in ${targetConnector.name}" +
                    ", creating new Entry")
            result.add(sourceEntry.copy(id = "no_id"))
        } else {
            var changed = false
            val tags = HashSet<String>(targetEntry.tags)
            var description = targetEntry.description
            var title = targetEntry.title
            if (MergeUtils.haveTagsChanged(sourceEntry.tags, targetEntry.tags)) {
                logger.debug("Entry with URL ${sourceEntry.url} found in ${targetConnector.name}" +
                        ", but Tags appears to have changed, updating Entry")
                logger.debug(sourceEntry.tags.toString())
                logger.debug(targetEntry.tags.toString())
                changed = true
                tags.addAll(sourceEntry.tags)
            }
            if (MergeUtils.hasTitleChanged(sourceEntry.title, targetEntry.title)) {
                logger.debug("Entry with URL ${sourceEntry.url} found in ${targetConnector.name}" +
                        ", but Title appears to have changed, updating Entry")
                logger.debug(sourceEntry.title)
                logger.debug(targetEntry.title)
                changed = true
                title = sourceEntry.title
            }
            if (MergeUtils.hasDescriptionChanged(sourceEntry.description, targetEntry.description)) {
                logger.debug("Entry with URL ${sourceEntry.url} found in ${targetConnector.name}" +
                        ", but Description appears to have changed, updating Entry")
                logger.debug(sourceEntry.description)
                logger.debug(targetEntry.description)
                changed = true
                description = sourceEntry.description
            }
            if (!changed) {
                logger.debug("Entry with URL ${sourceEntry.url} found in ${targetConnector.name}" +
                        " and unchanged, doing nothing")
            } else {
                result.add(sourceEntry.copy(id = targetEntry.id, tags = tags, title = title,
                        description = description))
            }
        }
    }


}