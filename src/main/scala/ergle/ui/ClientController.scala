package ergle.ui

import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control._
import javafx.stage.{Popup, DirectoryChooser, Stage, StageStyle}
import java.io.File
import javafx.util.Callback
import javafx.collections.ListChangeListener.Change
import scala.Predef._
import ergle.{Main, ConfigProvider}
import javafx.scene.image.{Image, ImageView}
import javafx.application.Platform

object ClientController {
  def getFunctionAsListChangeListener(function: () => Unit) = {
    new ListChangeListener[String] {
      def onChanged(p1: Change[_ <: String]) = function()
    }
  }
}

class ClientController {

  val watchedPaths: PathsListBox = new PathsListBox()

  def initialize() {
    watchedPathsContainer.setContent(watchedPaths)
    emailField.setText(ConfigProvider.config.getString(ConfigProvider.emailKey))
  }

  def getFunctionAsCallBack(function: (ListView[String] => ListCell[String])) = {
    new Callback[ListView[String], ListCell[String]] {
      def call(p1: ListView[String]): ListCell[String] = function(p1)
    }
  }

  @FXML private def browse() {
    val directoryChooser: DirectoryChooser = new DirectoryChooser
    directoryChooser.setTitle("Browse directories")

    val selectedDirectory: File = directoryChooser.showDialog(stage)
    if (selectedDirectory != null) {
      if (selectedDirectory.isDirectory) {
        watchedPaths.add(selectedDirectory.getAbsolutePath)
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

  @FXML def saveEmail() {
    ConfigProvider.save(ConfigProvider.emailKey, emailField.getText)

    val popup = new Popup
    popup.getContent().addAll(new ImageView(new Image("tick.png")))
    popup.show(Main.mainStage)

    new Thread(new Runnable() {
      @Override def run() {
        Thread.sleep(1000)
        Platform.runLater(new Runnable() {
          @Override
          def run() {
            popup.hide()
          }
        })
      }
    }).start()

  }

  @FXML private var stage: Stage = null
  @FXML private var watchedPathsContainer: ScrollPane = null
  @FXML private var emailField: TextField = null
  @FXML private var emailSaveButton: Button = null
}