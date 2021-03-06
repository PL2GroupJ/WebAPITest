package net

import javafx.scene.image.Image
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object HttpUtils {
    fun doGet(urlString: String): String {
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

    fun doGetImage(urlString: String): Image? {
        val url = URL(urlString)
        val con = when (url.protocol) {
            "http" -> url.openConnection() as HttpURLConnection
            "https" -> url.openConnection() as HttpsURLConnection
            else -> {
                IllegalArgumentException("プロトコルにはhttpまたはhttpsを指定してください")
                return null
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
            return null
        }

        return Image(con.inputStream)
    }

    fun test(urlString: String, header: String): String {
        val url = URL(urlString)
        val con = when (url.protocol) {
            "http" -> url.openConnection() as HttpURLConnection
            "https" -> url.openConnection() as HttpsURLConnection
            else -> {
                IllegalArgumentException("プロトコルにはhttpまたはhttpsを指定してください")
                return ""
            }
        }.apply {
            requestMethod = "POST"
            setRequestProperty("Authorization", "OAuth $header")
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