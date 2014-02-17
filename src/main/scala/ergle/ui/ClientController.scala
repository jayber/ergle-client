package ergle.ui

import javafx.collections.{ObservableList, ListChangeListener, FXCollections}
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.{ScrollPane, ListCell, ListView}
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File
import javafx.util.Callback
import javafx.collections.ListChangeListener.Change
import scala.Predef._
import scala.collection.JavaConverters._
import ergle.PathsConfig

object ClientController {
  def getFunctionAsListChangeListener(function: () => Unit) = {
    new ListChangeListener[String] {
      def onChanged(p1: Change[_ <: String]) = function()
    }
  }
}

class ClientController extends PathsConfig {

  def initialize() {
    val watchedPaths: PathsListBox = new PathsListBox()
    watchedPaths.content = watchedDirectoriesList
    watchedPathsContainer.setContent(watchedPaths)
  }

  def getFunctionAsCallBack(function: (ListView[String] => ListCell[String])) = {
    new Callback[ListView[String], ListCell[String]] {
      def call(p1: ListView[String]): ListCell[String] = function(p1)
    }
  }

  private def loadWatchedDirectories: ObservableList[String] = {
    val saved = read()
    val values: ObservableList[String] = FXCollections.observableArrayList(saved.asJava)
    values.addListener(ClientController.getFunctionAsListChangeListener(() => {
      save(values.asScala)
    }))
    values
  }

  @FXML private def browse() {
    val directoryChooser: DirectoryChooser = new DirectoryChooser
    directoryChooser.setTitle("Browse directories")
    val selectedDirectory: File = directoryChooser.showDialog(stage)
    if (selectedDirectory != null) {
      if (selectedDirectory.isDirectory) {
        watchedDirectoriesList.add(selectedDirectory.getAbsolutePath)
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
  @FXML private var watchedPathsContainer: ScrollPane = null
  val watchedDirectoriesList: ObservableList[String] = loadWatchedDirectories
}