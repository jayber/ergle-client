package ergle.ui

import javafx.scene.layout.{Priority, GridPane}
import javafx.scene.control.{OverrunStyle, Tooltip, Button, Label}
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Insets

class ListViewRow(value: String, action: (String) => Unit, alternateRow: Boolean) extends GridPane {
  setUserData(value)
  val label = new Label(value)
  label.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS)
  val button = new Button
  button.setOnAction(new EventHandler[ActionEvent] {
    def handle(p1: ActionEvent) = {
      action(label.getText)
    }
  })
  button.getStyleClass.add("trashButton")
  button.setTooltip(new Tooltip("Click to delete"))

  getChildren.addAll(label, button)
  GridPane.setConstraints(label, 0, 0)
  GridPane.setConstraints(button, 1, 0)
  GridPane.setHgrow(label, Priority.ALWAYS)
  GridPane.setHgrow(button, Priority.NEVER)

  setPadding(new Insets(3))
  getStyleClass.add("pathRow")
  if (alternateRow) {
    getStyleClass.add("alternateRow")
  }
}
