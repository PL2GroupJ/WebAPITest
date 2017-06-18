package main

import google.CustomSearch
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class Main : Application(), Transition {
    private lateinit var stage: Stage

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        setPane(CustomSearch::class.java.name)
        stage.show()
    }

    override fun setPane(classPath: String) {
        val className = classPath.substringAfter('.', "")
        val loader = FXMLLoader(Class.forName(classPath).getResource("$className.fxml"))
        val parent = loader.load<Parent>()
        stage.title = className
        stage.scene = Scene(parent)

        val pane = loader.getController<Any>()
        if (pane is TransitionPane) {
            pane.setTransition(this)
        }
    }
}

interface Transition {
    fun setPane(classPath: String)
}

interface TransitionPane {
    fun setTransition(transition: Transition)
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}