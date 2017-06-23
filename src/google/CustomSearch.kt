package google

import com.google.gson.Gson
import com.google.gson.JsonObject
import data.SearchResult
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import main.TransitionPane
import net.HttpUtils
import java.net.URL
import java.util.*

class CustomSearch : Initializable, TransitionPane {
    @FXML lateinit var searchField: TextField
    @FXML lateinit var colorCombo: ComboBox<String>
    @FXML lateinit var rightsCombo: ComboBox<String>
    @FXML lateinit var searchButton: Button
    @FXML lateinit var resultArea: TextArea

    // 汚いけどとりあえず保留
    @FXML lateinit var imageView1: ImageView
    @FXML lateinit var imageView2: ImageView
    @FXML lateinit var imageView3: ImageView
    @FXML lateinit var imageView4: ImageView
    @FXML lateinit var imageView5: ImageView
    @FXML lateinit var imageView6: ImageView
    @FXML lateinit var imageView7: ImageView
    @FXML lateinit var imageView8: ImageView
    @FXML lateinit var imageView9: ImageView
    @FXML lateinit var imageView10: ImageView

    private val api: String = "https://www.googleapis.com/customsearch/v1?key=AIzaSyD-ScaQf_PGrtne5ZU4SGdtAqjsdF7uUrs&cx=011887155026765679509:hu43zsi7b_m&searchType=image&lr=lang_ja"
    override lateinit var transition: (Any) -> Unit

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
        // TODO: 通信処理は非同期で書く
        val urlString = api + "&q=${searchField.text}&imgColorType=${colorCombo.selectionModel.selectedItem}&rights=${rightsCombo.selectionModel.selectedItem}"
        val response = HttpUtils.get(urlString)
        resultArea.text = response
        val gson = Gson()
        val result: List<SearchResult> = gson.fromJson(response, JsonObject::class.java)
                .get("items")
                .asJsonArray
                .map { gson.fromJson(it, SearchResult::class.java) }
        val imageViews = arrayOf(imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9, imageView10)
        result.take(10)
                .map { HttpUtils.getImage(it.link) }
                .forEachIndexed { index, image -> imageViews[index].image = image }

        // 連続使用を避けるため無効化。バインドしているためsearchFieldの方を無効化する
        searchField.text = ""
        searchField.isDisable = true
    }
}