package ergle.ui

import javafx.fxml.FXML
import javafx.stage.Stage

class DialogController {
  @FXML def dismiss {
    stage.close
  }

  def setStage(stage: Stage) {
    this.stage = stage
  }

  @FXML private var stage: Stage = null
}