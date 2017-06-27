package twitter

import javafx.fxml.FXML
import javafx.scene.control.PasswordField
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

class Login {
    @FXML private lateinit var userField: TextField
    @FXML private lateinit var passField: PasswordField
    @FXML private lateinit var resultArea: TextArea

    @FXML fun onLogin() {
//        TwitterAPI.getRequestToken()
    }
}