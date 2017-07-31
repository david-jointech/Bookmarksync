package de.eorlbruder.wallabag_shaarli_connector

import com.natpryce.konfig.*

class Sysconfig {

    object wallabag : PropertyGroup() {
        val url by stringType
        val username by stringType
        val password by stringType
        val clientid by stringType
        val clientsecret by stringType
    }

    object shaarli : PropertyGroup() {
        val url by stringType
        val secret by stringType
    }


    private val config = ConfigurationProperties.fromResource("sysconfig.properties")

    val WALLABAG_URL = config[wallabag.url]
    val WALLABAG_USERNAME = config[wallabag.username]
    val WALLABAG_PASSWORD = config[wallabag.password]
    val WALLABAG_CLIENTID = config[wallabag.clientid]
    val WALLABAG_CLIENTSECRET = config[wallabag.clientsecret]

    val SHAARLI_URL = config[shaarli.url]
    val SHAARLI_SECRET = config[shaarli.secret]
}
