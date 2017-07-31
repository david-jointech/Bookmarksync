package de.eorlbruder.wallabag_shaarli_connector

import de.eorlbruder.wallabag_shaarli_connector.shaarli.ShaarliConnector
import de.eorlbruder.wallabag_shaarli_connector.wallabag.WallabagConnector

fun main(args: Array<String>) {
    val wallabag : Connector = WallabagConnector()
    val shaarli : Connector = ShaarliConnector()

    val wallabagEntries = wallabag.getAllEntries()
    val shaarliEntries = shaarli.getAllEntries()
    val entries = EntryMerger(wallabagEntries, shaarliEntries).mergeEntries()
    shaarli.writeAllEntries(entries)
}
