package de.eorlbruder.wallabag_shaarli_connector.core

import de.eorlbruder.wallabag_shaarli_connector.reddit.RedditConnector
import de.eorlbruder.wallabag_shaarli_connector.shaarli.ShaarliConnector

fun main(args: Array<String>) {
//    val wallabag: Connector = WallabagConnector()
    val shaarli: Connector = ShaarliConnector()
    val reddit: Connector = RedditConnector()
//    val standardnotes: Connector = StandardnotesConnector()
//    Syncer(wallabag, shaarli).sync()
//    Syncer(standardnotes, shaarli).sync()
    Syncer(reddit, shaarli).sync()
//    val shaarli: ShaarliConnector = ShaarliConnector()
//    shaarli.deleteAllEntriesWithTag("Reddit")
}
