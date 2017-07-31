package de.eorlbruder.wallabag_shaarli_connector

class Constants {

    companion object {
        val WALLABAG_API_ENDPOINT = "api/"
        val WALLABAG_ENTRIES = "entries.json"
        val WALLABAG_PAGE_KEY = "?page="
        val WALLABAG_AUTH_ENDPOINT = "oauth/v2/token"
        val WALLABAG_GRANT_TYPE = "?grant_type=password"
        val WALLABAG_CLIENT_ID_KEY = "&client_id="
        val WALLABAG_CLIENT_SECRET_KEY = "&client_secret="
        val WALLABAG_USERNAME_KEY = "&username="
        val WALLABAG_PASSWORD_KEY = "&password="
        val WALLABAG_ACCESS_TOKEN = "access_token"

        val WALLABAG_TAG = "Wallabag"

        val SHAARLI_API_ENDPOINT = "api/v1/"
        val SHAARLI_ENTRIES = "links"
        val SHAARLI_LIMIT_KEY = "?limit="
        val SHAARLI_OFFSET_KEY = "&offset="
        val SHAARLI_SEARCHTAGS_KEY = "&searchtags="
        val SHAARLI_LIMIT = 30

        val AUTHORIZATION = "Authorization"
        val BEARER = "Bearer"
    }
}