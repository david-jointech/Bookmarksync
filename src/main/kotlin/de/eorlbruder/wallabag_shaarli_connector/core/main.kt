package de.eorlbruder.wallabag_shaarli_connector.core

import de.eorlbruder.wallabag_shaarli_connector.shaarli.ShaarliConnector
import de.eorlbruder.wallabag_shaarli_connector.standardnotes.StandardnotesConnector

fun main(args: Array<String>) {
//    val wallabag: Connector = WallabagConnector()
    val shaarli: Connector = ShaarliConnector()
//    val reddit: Connector = RedditConnector()
    val standardnotes: Connector = StandardnotesConnector()
//    Syncer(wallabag, shaarli).sync()
    Syncer(standardnotes, shaarli).sync()
}
