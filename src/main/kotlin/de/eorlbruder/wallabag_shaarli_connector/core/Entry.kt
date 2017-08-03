package de.eorlbruder.wallabag_shaarli_connector.core

data class Entry(val title: String, var tags: Set<String>, val id: String, val url: String = "",
                 val description: String = "")