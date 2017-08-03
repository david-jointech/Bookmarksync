package de.eorlbruder.wallabag_shaarli_connector.core

import mu.KLogging

class Syncer(val sourceConnector: Connector, val targetConnector: Connector) {

    companion object : KLogging()

    fun sync() {
        logger.info("Syncing entries from ${sourceConnector.name} to ${targetConnector.name}")
        EntryMerger(sourceConnector, targetConnector).mergeEntries()
        targetConnector.write()
    }

}