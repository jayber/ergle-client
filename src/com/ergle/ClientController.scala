package com.ergle

import javafx.collections.{ObservableList, ListChangeListener, FXCollections}
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.{ListCell, ListView}
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.{PrintWriter, File}
import javafx.util.Callback
import javafx.collections.ListChangeListener.Change
import scala.Predef._
import scala.collection.JavaConverters._
import scala.io.Source

class ClientController {

  def handleTrashAction(path: String): Unit = {
    content.remove(path)
  }

  def initialize() {
    content = loadWatchedDirectories
    watchedPaths.setItems(content)
    watchedPaths.setCellFactory(getFunctionAsCallBack(view => {
      val cell: ListCell[String] = new ListCell[String]
      val listViewCell: ListViewCell = new ListViewCell(handleTrashAction)
      cell.setGraphic(listViewCell)
      listViewCell.getTextProperty.bind(cell.itemProperty())
      listViewCell.buttonVisibleProperty.bind(cell.emptyProperty().not())
      cell
    }))
  }

  def getFunctionAsCallBack(function: (ListView[String] => ListCell[String])) = {
    new Callback[ListView[String], ListCell[String]] {
      def call(p1: ListView[String]): ListCell[String] = function(p1)
    }
  }

  def read() = {
    Source.fromFile(configFile).getLines().toList
  }

  private def loadWatchedDirectories: ObservableList[String] = {
    val saved = read()
    val values: ObservableList[String] = FXCollections.observableArrayList(saved.asJava)
    values.addListener(getFunctionAsListener({
      save
    }))
    values
  }

  private def save() {
    val writer = new PrintWriter(configFile)
    try writer.write(content.asScala.mkString("\r\n"))
    finally writer.close()
  }

  def getFunctionAsListener(function: () => Unit) = {
    new ListChangeListener[String] {
      def onChanged(p1: Change[_ <: String]) = function()
    }
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
  private var configFile = new File("paths.config")
}