package ergle.ui

import java.io.File
import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.{Parent, Scene}
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.GridPane
import javafx.stage.{DirectoryChooser, Popup, Stage, StageStyle}

import ergle.{ConfigProvider, Main}


class ClientController {

  @FXML private var stage: Stage = null
  @FXML private var watchedPathsContainer: GridPane = null
  @FXML private var emailField: TextField = null
  @FXML private var saveButton: Button = null

  var watchedPaths: PathsListBox = null

  def initialize() {
    watchedPaths = new PathsListBox(watchedPathsContainer, saveButton)

    emailField.setText(ConfigProvider.config.getString(ConfigProvider.emailKey))
    emailField.textProperty.addListener(new ChangeButton(saveButton))
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

  @FXML def save() {
    ConfigProvider.save(ConfigProvider.emailKey, emailField.getText)
    watchedPaths.save()
    showTick()
  }

  def showTick() {
    val popup = new Popup
    popup.getContent.addAll(new ImageView(new Image("tick.png")))
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
}

class ChangeButton(button: Button) extends ChangeListener[String] {
  override def changed(p1: ObservableValue[_ <: String], p2: String, p3: String) {
    button.setDisable(false)
  }
}