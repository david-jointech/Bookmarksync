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
    val shaarli: Connector = ShaarliConnector()
    val wallabag: Connector = WallabagConnector()
    val reddit: Connector = RedditConnector()
    val standardnotes: Connector = StandardnotesConnector()
    Syncer(wallabag, shaarli).sync()
    Syncer(standardnotes, shaarli).sync()
    Syncer(reddit, shaarli).sync()
}

fun delete() {
    val shaarli: ShaarliConnector = ShaarliConnector()
    shaarli.deleteAllEntriesWithTag("Standardnotes")
}