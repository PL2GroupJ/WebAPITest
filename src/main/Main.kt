package main

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import twitter.RESTAPI


class Main : Application() {
    private lateinit var stage: Stage

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        setPane(RESTAPI())
        stage.show()
    }

    fun setPane(controller: Any) {
        val classPath = controller.javaClass.name
        val className = controller.javaClass.simpleName
        val loader = FXMLLoader(Class.forName(classPath).getResource("$className.fxml"))
                        .apply { setController(controller) }
        val parent = loader.load<Parent>()
        stage.title = className
        stage.scene = Scene(parent)

        if (controller is TransitionPane) {
            controller.transition = this::setPane
        }
    }
}

interface TransitionPane {
    var transition: ((Any) -> Unit)
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}