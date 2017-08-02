package de.eorlbruder.wallabag_shaarli_connector.core

import mu.KLogging

class Syncer(val sourceConnector: Connector, val targetConnector: Connector) {

    companion object : KLogging()

    fun sync() {
        logger.info("Syncing entries from ${targetConnector.getName()} to ${sourceConnector.getName()}")
        val sourceEntries = sourceConnector.getAllEntries()
        val targetEntries = targetConnector.getAllEntries()
        val entries = EntryMerger(sourceEntries, targetEntries).mergeEntries()
        targetConnector.writeAllEntries(entries)
    }

}