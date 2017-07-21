package twitter

import data.User
import net.HttpUtils
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TwitterAPI {
    private val apiKey = "bVGE7lvasH8CgKwZo8CwwpSId"
    private val apiSecret = "BQ9GL8jb3QU1CxUD4GuJjhawLMUzbCOvYCiKeCKRAwB8VTGR46"
    private val callbackUrl = "https://twitter.com/pl2groupj2"
    private val requestUrl = "https://api.twitter.com/oauth/request_token"
    private val authorizeUrl = "https://api.twitter.com/oauth/authorize"
    private val accessUrl = "https://api.twitter.com/oauth/access_token"
    private val params = sortedMapOf(
            "oauth_consumer_key" to encode(apiKey),
            "oauth_signature_method" to encode("HMAC-SHA1"),
            "oauth_timestamp" to encode((System.currentTimeMillis() / 1000).toString()),
            "oauth_nonce" to encode(System.nanoTime().toString()),
            "oauth_version" to encode("1.0"))

    fun getAuthorizeUrl(): String {
        params.put("oauth_signature", encode(makeSignature(requestUrl, "")))
        val header = params.map { (key, value) -> "$key=$value" }.joinToString(separator = ",")
        val requestToken = HttpUtils.test(requestUrl, header).split("&")
        val oauth_token = requestToken[0].split("=")[1]
        println("requestToken = $requestToken")
        return "$authorizeUrl?oauth_token=$oauth_token"
    }

    fun getUser(url: String): User? {
        // 認証または拒否された場合。callbackのURLに合わせて変える必要あり
        if (url.startsWith("$callbackUrl?oauth_token=")) {
            val authorizeToken = url.substringAfter("$callbackUrl?").split("&")
            val oauth_token = authorizeToken[0].split("=")[1]
            val oauth_verifier = authorizeToken[1].split("=")[1]
            println("authorizeToken = $authorizeToken")
            params.apply {
                put("oauth_token", oauth_token)
                put("oauth_verifier", oauth_verifier)
                put("oauth_signature", encode(makeSignature(accessUrl, "")))
            }

            val header = params.map { (key, value) -> "$key=$value" }.joinToString(separator = ",")
            val accessToken = HttpUtils.test(accessUrl, header).split("&")
            val access_token = accessToken[0].split("=")[1]
            val access_token_secret = accessToken[1].split("=")[1]
            println(accessToken)

            return User(access_token, access_token_secret, userId = accessToken[2].split("=")[1], screenName = accessToken[3].split("=")[1])
        }
        else return null
    }

    fun tweet(status: String) {

    }

    private fun makeSignature(url: String, oauth_token_secret: String): String {
        val requestParams = params.map { (key, value) -> "$key=$value" }.joinToString(separator = "&")
        val signatureKey = "${encode(apiSecret)}&${encode(oauth_token_secret)}"
        val signatureData = "${encode("POST")}&${encode(url)}&${encode(requestParams)}"
        val signingKey = SecretKeySpec(signatureKey.toByteArray(), "HmacSHA1")
        val rawHmac  = Mac.getInstance(signingKey.algorithm).run {
            init(signingKey)
            doFinal(signatureData.toByteArray())
        }
        return Base64.getEncoder().encodeToString(rawHmac)
    }

    private fun encode(s: String): String {
        return URLEncoder.encode(s, "UTF-8")
    }
}
