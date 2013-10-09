package com.ergle

import javafx.scene.layout.{Priority, GridPane}
import javafx.scene.control.{Tooltip, Button, Label}
import javafx.event.{ActionEvent, EventHandler}

class ListViewCell(action: (String) => Unit) extends GridPane {
  val label = new Label
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

  def getTextProperty = {
    label.textProperty()
  }

  def buttonVisibleProperty = {
    button.visibleProperty()
  }
}
