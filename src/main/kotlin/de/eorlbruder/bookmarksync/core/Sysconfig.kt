package de.eorlbruder.bookmarksync.core

import com.natpryce.konfig.*
import java.io.File

class Sysconfig {

    val TARGET_CONNECTOR: String
    val SOURCE_CONNECTORS: List<String>

    val WALLABAG_URL: String
    val WALLABAG_USERNAME: String
    val WALLABAG_PASSWORD: String
    val WALLABAG_CLIENT_ID: String
    val WALLABAG_CLIENT_SECRET: String

    val SHAARLI_URL: String
    val SHAARLI_SECRET: String

    val STANDARDNOTES_URL: String
    val STANDARDNOTES_EMAIL: String
    val STANDARDNOTES_SERVER_KEY: String
    val STANDARDNOTES_MASTER_KEY: String
    val STANDARDNOTES_AUTH_KEY: String

    val REDDIT_URL: String
    val REDDIT_OAUTHURL: String
    val REDDIT_USERNAME: String
    val REDDIT_PASSWORD: String
    val REDDIT_CLIENT_ID: String
    val REDDIT_CLIENT_SECRET: String

    val TWITTER_OAUTH_CONSUMER_KEY: String
    val TWITTER_OAUTH_CONSUMER_SECRET: String
    val TWITTER_OAUTH_ACCESSTOKEN: String
    val TWITTER_OAUTH_ACCESSTOKEN_SECRET: String

    val sysconfig = Key("sysconfig", stringType)
    val env = Key("env", booleanType)

    init {
        var config: Configuration = EnvironmentVariables
        val sysconfig: String? = config.getOrNull(bookmarksync.sysconfig)
        if (sysconfig != null) {
            config = ConfigurationProperties.fromFile(
                    File(sysconfig))
        }
        // These are required
        TARGET_CONNECTOR = config[bookmarksync.targetconnector]
        SOURCE_CONNECTORS = config[bookmarksync.sourceconnectors]

        WALLABAG_URL = config.getOrElse(wallabag.url, "")
        WALLABAG_USERNAME = config.getOrElse(wallabag.username, "")
        WALLABAG_PASSWORD = config.getOrElse(wallabag.password, "")
        WALLABAG_CLIENT_ID = config.getOrElse(wallabag.clientid, "")
        WALLABAG_CLIENT_SECRET = config.getOrElse(wallabag.clientsecret, "")

        SHAARLI_URL = config.getOrElse(shaarli.url, "")
        SHAARLI_SECRET = config.getOrElse(shaarli.secret, "")

        STANDARDNOTES_URL = config.getOrElse(standardnotes.url, "")
        STANDARDNOTES_EMAIL = config.getOrElse(standardnotes.email, "")
        STANDARDNOTES_SERVER_KEY = config.getOrElse(standardnotes.serverkey, "")
        STANDARDNOTES_MASTER_KEY = config.getOrElse(standardnotes.masterkey, "")
        STANDARDNOTES_AUTH_KEY = config.getOrElse(standardnotes.authkey, "")

        REDDIT_URL = config.getOrElse(reddit.url, "")
        REDDIT_OAUTHURL = config.getOrElse(reddit.oauthurl, "")
        REDDIT_USERNAME = config.getOrElse(reddit.username, "")
        REDDIT_PASSWORD = config.getOrElse(reddit.password, "")
        REDDIT_CLIENT_ID = config.getOrElse(reddit.clientid, "")
        REDDIT_CLIENT_SECRET = config.getOrElse(reddit.clientsecret, "")

        TWITTER_OAUTH_CONSUMER_KEY = config.getOrElse(twitter.oauthconsumerkey, "")
        TWITTER_OAUTH_CONSUMER_SECRET = config.getOrElse(twitter.oauthconsumersecret, "")
        TWITTER_OAUTH_ACCESSTOKEN = config.getOrElse(twitter.oauthaccesstoken, "")
        TWITTER_OAUTH_ACCESSTOKEN_SECRET = config.getOrElse(twitter.oauthaccesstokensecret, "")
    }


    private fun getConfig(): ConfigurationProperties {
        return ConfigurationProperties.fromResource("conf/sysconfig.properties")
    }

    object bookmarksync : PropertyGroup() {
        val targetconnector by stringType
        val sourceconnectors by listType(stringType)
        val sysconfig by stringType
    }

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

    object twitter : PropertyGroup() {
        val oauthconsumerkey by stringType
        val oauthconsumersecret by stringType
        val oauthaccesstoken by stringType
        val oauthaccesstokensecret by stringType
    }
}
