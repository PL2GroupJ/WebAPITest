package twitter

import data.User
import javafx.beans.property.ReadOnlyStringProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.web.WebView
import java.net.URL
import java.util.*

class RESTAPI() : Initializable {
    @FXML lateinit var webView: WebView
    private lateinit var location: ReadOnlyStringProperty

    override fun initialize(loc: URL?, resources: ResourceBundle?) {
        webView.engine.load(TwitterAPI.getAuthorizeUrl())
        location = webView.engine.locationProperty()
        location.addListener { _, oldValue, newValue ->
            println("location: $oldValue -> $newValue")
            val user = TwitterAPI.getUser(newValue)
            if (user != null) {
                User.user = user
                print(User.user)
            }
        }
    }
}