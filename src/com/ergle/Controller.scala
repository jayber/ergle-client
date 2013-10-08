package com.ergle

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.{ListCell, ListView}
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File
import javafx.util.Callback

class Controller {

  def handleTrashAction(path: String): Unit = {
    content.remove(path)
  }

  def initialize() {
    content = loadWatchedDirectories
    watchedPaths.setItems(content)
    watchedPaths.setCellFactory(functionToCallBack(view => {
      val cell: ListCell[String] = new ListCell[String]
      val listViewCell: ListViewCell = new ListViewCell(handleTrashAction)
      cell.setGraphic(listViewCell)
      listViewCell.getTextProperty.bind(cell.itemProperty())
      listViewCell.buttonVisibleProperty.bind(cell.emptyProperty().not())
      cell
    }))
  }

  def functionToCallBack(function: (ListView[String] => ListCell[String])) = {
    new Callback[ListView[String], ListCell[String]] {
      def call(p1: ListView[String]): ListCell[String] = function(p1)
    }
  }

  private def loadWatchedDirectories: ObservableList[String] = {
    FXCollections.observableArrayList()
  }

  @FXML private def browse() {
    val directoryChooser: DirectoryChooser = new DirectoryChooser
    directoryChooser.setTitle("Browse directories")
    val selectedDirectory: File = directoryChooser.showDialog(stage)
    if (selectedDirectory != null) {
      if (selectedDirectory.isDirectory) {
        content.add(selectedDirectory.getAbsolutePath)
      }
    }
    else {
      showWarning()
    }
  }

  def showWarning() {
    val dialog: Stage = new Stage
    dialog.setTitle("Warning")
    dialog.initStyle(StageStyle.UTILITY)
    val loader: FXMLLoader = new FXMLLoader
    loader.setLocation(getClass.getResource("dialog.fxml"))
    val parent: Parent = loader.load.asInstanceOf[Parent]
    val controller: DialogController = loader.getController.asInstanceOf[DialogController]
    controller.setStage(dialog)
    dialog.setScene(new Scene(parent))
    dialog.show()
  }

  @FXML private var stage: Stage = null
  @FXML private var watchedPaths: ListView[String] = null
  private var content: ObservableList[String] = null
}