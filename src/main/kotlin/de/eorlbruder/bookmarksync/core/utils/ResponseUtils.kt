package de.eorlbruder.bookmarksync.core.utils

import de.eorlbruder.bookmarksync.core.Constants
import khttp.responses.Response
import mu.KLogging

class ResponseUtils {

    companion object : KLogging() {
        fun isSuccessfulStatusCode(response: Response) = response.statusCode in 200..300
        fun getAuthorizationHeaderWithToken(accessToken : String) = mapOf(Constants.AUTHORIZATION to Constants.BEARER + " " + accessToken)
    }
}