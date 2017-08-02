package de.eorlbruder.wallabag_shaarli_connector.core

data class Entry(val title: String, var tags: Set<String>, val url: String = "", val id: Int = -1,
                 val description: String = "", var deleted: Boolean = false)