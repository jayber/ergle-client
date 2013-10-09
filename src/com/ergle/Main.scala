package com.ergle

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

object Main {
  def main(args: Array[String]) {
    Application.launch(classOf[Main], args: _*)
  }
}

class Main extends Application {

  def start(primaryStage: Stage) {
    val root: Parent = FXMLLoader.load(getClass.getResource("client.fxml"))
    primaryStage.getIcons.add(new Image("/ergleIcon.png"))
    primaryStage.setTitle("ergle")
    val scene: Scene = new Scene(root)
    primaryStage.setScene(scene)
    scene.getStylesheets.add("com/ergle/main.css")
    primaryStage.show()
  }
}