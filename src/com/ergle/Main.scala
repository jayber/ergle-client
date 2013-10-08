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
    val root: Parent = FXMLLoader.load(getClass.getResource("sample.fxml"))
    primaryStage.getIcons.add(new Image("/ergleIcon.png"))
    primaryStage.setTitle("ergle")
    primaryStage.setScene(new Scene(root))
    primaryStage.show()
  }
}