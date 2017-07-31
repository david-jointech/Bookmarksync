package de.eorlbruder.wallabag_shaarli_connector.utils

import de.eorlbruder.wallabag_shaarli_connector.core.Constants
import khttp.responses.Response

class ResponseUtils {

    companion object {
        fun isSuccessfulStatusCode(response: Response) = response.statusCode in 200..300
        fun getAuthorizationHeaderWithToken(accessToken : String) = mapOf(Constants.AUTHORIZATION to Constants.BEARER + " " + accessToken)
    }
}