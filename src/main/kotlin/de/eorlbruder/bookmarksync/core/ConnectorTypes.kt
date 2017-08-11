package de.eorlbruder.bookmarksync.core

import de.eorlbruder.bookmarksync.reddit.RedditConnector
import de.eorlbruder.bookmarksync.shaarli.ShaarliConnector
import de.eorlbruder.bookmarksync.standardnotes.StandardnotesConnector
import de.eorlbruder.bookmarksync.wallabag.WallabagConnector
import mu.KLogging

enum class ConnectorTypes(val value: String) {
    WALLABAG("Wallabag"),
    SHAARLI("Shaarli"),
    REDDIT("Reddit"),
    STANDARDNOTES("Standardnotes");

    companion object : KLogging() {

        fun fromString(name: String): Connector {
            when (name) {
                WALLABAG.value -> {
                    return WallabagConnector()
                }
                SHAARLI.value -> {
                    return ShaarliConnector()
                }
                STANDARDNOTES.value -> {
                    return StandardnotesConnector()
                }
                REDDIT.value -> {
                    return RedditConnector()
                }
            }
            throw Exception("No Connector for name $name found")
        }
    }
}