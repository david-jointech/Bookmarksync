package de.eorlbruder.wallabag_shaarli_connector.core

import mu.KLogging

class Syncer(val sourceConnectors: List<Connector>, val targetConnector: Connector) {

    companion object : KLogging()

    fun sync() {
        sourceConnectors.forEach {
            logger.info("Syncing entries from ${it.name} to ${targetConnector.name}")
            EntryMerger(it, targetConnector).mergeEntries()
            targetConnector.write(it.name)
        }
    }

}