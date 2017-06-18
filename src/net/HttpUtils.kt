package net

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object HttpUtils {
    fun get(urlString: String): String {
        val url = URL(urlString)
        val con = when (url.protocol) {
            "http" -> url.openConnection() as HttpURLConnection
            "https" -> url.openConnection() as HttpsURLConnection
            else -> {
                IllegalArgumentException("プロトコルにはhttpまたはhttpsを指定してください")
                return ""
            }
        }.apply {
            requestMethod = "GET"
            instanceFollowRedirects = false
            readTimeout = 10000
            connectTimeout = 20000
            connect()
        }

        if (con.responseCode != HttpURLConnection.HTTP_OK) {
            println("通信失敗:${con.responseCode}")
            return ""
        }

        val input = BufferedReader(InputStreamReader(con.inputStream))
        return input.readText()
    }
}