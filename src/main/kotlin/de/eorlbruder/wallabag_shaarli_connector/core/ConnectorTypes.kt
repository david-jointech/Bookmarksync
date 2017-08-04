package de.eorlbruder.wallabag_shaarli_connector.core

enum class ConnectorTypes(val value: String) {
    WALLABAG("Wallabag"),
    SHAARLI("Shaarli"),
    REDDIT("Reddit"),
    STANDARDNOTES("Standardnotes")
}