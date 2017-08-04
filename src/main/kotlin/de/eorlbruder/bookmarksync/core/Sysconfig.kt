package de.eorlbruder.bookmarksync.core

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.stringType

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

    object standardnotes : PropertyGroup() {
        val url by stringType
        val email by stringType
        val serverkey by stringType
        val masterkey by stringType
        val authkey by stringType
    }

    object reddit : PropertyGroup() {
        val url by stringType
        val oauthurl by stringType
        val username by stringType
        val password by stringType
        val clientid by stringType
        val clientsecret by stringType
    }

    private val config = ConfigurationProperties.fromResource("sysconfig.properties")

    val WALLABAG_URL = config[wallabag.url]
    val WALLABAG_USERNAME = config[wallabag.username]
    val WALLABAG_PASSWORD = config[wallabag.password]
    val WALLABAG_CLIENT_ID = config[wallabag.clientid]
    val WALLABAG_CLIENT_SECRET = config[wallabag.clientsecret]

    val SHAARLI_URL = config[shaarli.url]
    val SHAARLI_SECRET = config[shaarli.secret]

    val STANDARDNOTES_URL = config[standardnotes.url]
    val STANDARDNOTES_EMAIL = config[standardnotes.email]
    val STANDARDNOTES_SERVER_KEY = config[standardnotes.serverkey]
    val STANDARDNOTES_MASTER_KEY = config[standardnotes.masterkey]
    val STANDARDNOTES_AUTH_KEY = config[standardnotes.authkey]

    val REDDIT_URL = config[reddit.url]
    val REDDIT_OAUTHURL = config[reddit.oauthurl]
    val REDDIT_USERNAME = config[reddit.username]
    val REDDIT_PASSWORD = config[reddit.password]
    val REDDIT_CLIENT_ID = config[reddit.clientid]
    val REDDIT_CLIENT_SECRET = config[reddit.clientsecret]
}
