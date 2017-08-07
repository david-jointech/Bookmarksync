package de.eorlbruder.bookmarksync.core

import mu.KLogging

class Syncer {

    companion object : KLogging()

    val targetConnector: Connector
    val sourceConnectors: List<Connector>

    val config: Sysconfig = Sysconfig()


    init {
        targetConnector = ConnectorTypes.fromString(config.TARGET_CONNECTOR)
        sourceConnectors = config.SOURCE_CONNECTORS.map {
            ConnectorTypes.fromString(it)
        }
    }

    fun sync() {
        sourceConnectors.forEach {
            logger.info("Syncing entries from ${it.name} to ${targetConnector.name}")
            EntryMerger(it, targetConnector).mergeEntries()
            targetConnector.write(it.name)
        }
    }

}