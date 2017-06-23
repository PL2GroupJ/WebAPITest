package google

import com.google.gson.Gson
import com.google.gson.JsonObject
import data.SearchResult
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.concurrent.Task
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

    override lateinit var transition: (Any) -> Unit
    private var canSearch: BooleanProperty = SimpleBooleanProperty(true)
    private val apiKey: String = "AIzaSyD-ScaQf_PGrtne5ZU4SGdtAqjsdF7uUrs"
    private val engineId: String = "011887155026765679509:hu43zsi7b_m"
    private val apiUrl: String = "https://www.googleapis.com/customsearch/v1?"

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // gray推奨 monoだと良い物が出てこない
        colorCombo.items.addAll("color", "gray", "mono")
        // public domainなら問題ないみたい
        // 参考　https://creativecommons.jp/licenses/
        rightsCombo.items.addAll("cc_publicdomain", "cc_attribute", "cc_sharealike", "cc_noncommercial", "cc_nonderived")

        // 複数とのバインドのためにBindings.or()をreduceで使用。拡張関数で書きたい
        val bindingTargets = arrayOf(
                searchField.textProperty().isEmpty,
                colorCombo.selectionModel.selectedIndexProperty().isEqualTo(-1),
                rightsCombo.selectionModel.selectedIndexProperty().isEqualTo(-1),
                canSearch.not())
        searchButton.disableProperty().bind(

            bindingTargets.reduce { acc, booleanBinding -> Bindings.or(acc, booleanBinding) }
        )
        searchButton.setOnAction { onSearch() }
    }

    fun onSearch() {
        canSearch.value = false
        Thread(object : Task<Boolean>() {
            override fun call(): Boolean {
                val images = searchImages()
                Platform.runLater {
                    val imageViews = arrayOf(imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9, imageView10)
                    images.forEachIndexed { index, image -> imageViews[index].image = image }
                    canSearch.value = true
                }
                return true
            }
        }).apply {
            isDaemon = true
            start()
        }
    }

    private fun searchImages(): List<Image?> {
        val urlString = apiUrl +
                "key=$apiKey" +
                "&cx=$engineId" +
                "&q=${searchField.text}" +
                "&searchType=image" +
                "&imgColorType=${colorCombo.selectionModel.selectedItem}" +
                "&rights=${rightsCombo.selectionModel.selectedItem}" +
                "&lr=lang_ja"
        val response = HttpUtils.doGet(urlString)
        resultArea.text = response
        val gson = Gson()
        // 取り出したい部分だけをJsonObjectで取得してからGSONでパースする
        val result: List<SearchResult> = gson.fromJson(response, JsonObject::class.java)
                .get("items")
                .asJsonArray
                .map { gson.fromJson(it, SearchResult::class.java) }
        return result.take(10)
                .map { HttpUtils.doGetImage(it.link) }
    }
}