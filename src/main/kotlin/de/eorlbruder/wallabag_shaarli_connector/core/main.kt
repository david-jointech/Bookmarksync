package de.eorlbruder.wallabag_shaarli_connector.core

import de.eorlbruder.wallabag_shaarli_connector.reddit.RedditConnector
import de.eorlbruder.wallabag_shaarli_connector.shaarli.ShaarliConnector
import de.eorlbruder.wallabag_shaarli_connector.wallabag.WallabagConnector

fun main(args: Array<String>) {
    val wallabag: Connector = WallabagConnector(true)
    val shaarli: Connector = ShaarliConnector(false)
    val reddit: Connector = RedditConnector(true)
//    Syncer(wallabag, shaarli).sync()
    reddit.getAllEntries()
}
