package de.eorlbruder.bookmarksync.core

import de.eorlbruder.bookmarksync.reddit.RedditConnector
import de.eorlbruder.bookmarksync.shaarli.ShaarliConnector
import de.eorlbruder.bookmarksync.standardnotes.StandardnotesConnector
import de.eorlbruder.bookmarksync.wallabag.WallabagConnector

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