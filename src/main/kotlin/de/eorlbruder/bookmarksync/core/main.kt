package de.eorlbruder.bookmarksync.core

import de.eorlbruder.bookmarksync.shaarli.ShaarliConnector

fun main(args: Array<String>) {
    sync()
    // If there are sync-errors you can use the delete-Function to delete all Entries from one
    // Connector
//    delete()
}

fun sync() {
    Syncer().sync()
}

fun delete() {
    val shaarli: ShaarliConnector = ShaarliConnector()
    shaarli.deleteAllEntriesWithTag("Standardnotes")
}