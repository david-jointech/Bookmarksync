package de.eorlbruder.wallabag_shaarli_connector.core

import de.eorlbruder.wallabag_shaarli_connector.shaarli.ShaarliConnector
import de.eorlbruder.wallabag_shaarli_connector.wallabag.WallabagConnector

fun main(args: Array<String>) {
    val wallabag: Connector = WallabagConnector(true)
    val shaarli: Connector = ShaarliConnector(false)
//    val reddit: Connector = RedditConnector(true)
//    val standardnotes: Connector = StandardnotesConnector(true)
    Syncer(wallabag, shaarli).sync()
//    Syncer(standardnotes, shaarli).sync()
}
