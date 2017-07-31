package de.eorlbruder.wallabag_shaarli_connector.core

data class Entry(val title: String, val url: String, var tags: Set<String>, val id: Int = -1)