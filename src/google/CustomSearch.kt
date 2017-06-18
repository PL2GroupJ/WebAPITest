package google

import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import main.Transition
import main.TransitionPane
import java.net.URL
import java.util.*

class CustomSearch : Initializable, TransitionPane {
    @FXML lateinit var searchField: TextField
    @FXML lateinit var colorCombo: ComboBox<String>
    @FXML lateinit var rightsCombo: ComboBox<String>
    @FXML lateinit var searchButton: Button
    @FXML lateinit var resultArea: TextArea

    private var transition: Transition? = null
    private val api: String = "https://www.googleapis.com/customsearch/v1?key=AIzaSyD-ScaQf_PGrtne5ZU4SGdtAqjsdF7uUrs&cx=011887155026765679509:hu43zsi7b_m&searchType=image&lr=lang_ja"

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // gray推奨 monoだと良い物が出てこない
        colorCombo.items.addAll("color", "gray", "mono")
        // public domainなら問題ないみたい
        // 参考　https://creativecommons.jp/licenses/
        rightsCombo.items.addAll("cc_publicdomain", "cc_attribute", "cc_sharealike", "cc_noncommercial", "cc_nonderived")

        // 複数とのバインドのためにBindings.or()を２回使っている
        searchButton.disableProperty().bind(
                Bindings.or(
                        searchField.textProperty().isEmpty,
                Bindings.or(
                        colorCombo.selectionModel.selectedIndexProperty().isEqualTo(-1),
                        rightsCombo.selectionModel.selectedIndexProperty().isEqualTo(-1)
                ))
        )
    }

    @FXML
    fun onSearch() {
        val urlString = api + "&q=${searchField.text}&imgColorType=${colorCombo.selectionModel.selectedItem}&rights=${rightsCombo.selectionModel.selectedItem}"
        resultArea.text = urlString
    }

    override fun setTransition(transition: Transition) {
        this.transition = transition
    }
}