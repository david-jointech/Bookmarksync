package de.eorlbruder.wallabag_shaarli_connector.core

import de.eorlbruder.wallabag_shaarli_connector.reddit.RedditConnector
import de.eorlbruder.wallabag_shaarli_connector.shaarli.ShaarliConnector
import de.eorlbruder.wallabag_shaarli_connector.standardnotes.StandardnotesConnector
import de.eorlbruder.wallabag_shaarli_connector.wallabag.WallabagConnector

fun main(args: Array<String>) {
    sync()
    // If there are sync-errors you can use the delete-Function to delete all Entries from one
    // Connector
//    delete()
}

fun sync() {
    val targetConnector: Connector = ShaarliConnector()
    val sourceConnectors = ArrayList<Connector>()
    sourceConnectors.add(WallabagConnector())
    sourceConnectors.add(RedditConnector())
    sourceConnectors.add(StandardnotesConnector())
    Syncer(sourceConnectors, targetConnector).sync()
}

fun delete() {
    val shaarli: ShaarliConnector = ShaarliConnector()
    shaarli.deleteAllEntriesWithTag("Standardnotes")
}