package com.ergle

import javafx.scene.layout.AnchorPane
import javafx.scene.control.{Button, Label}
import javafx.event.{ActionEvent, EventHandler}

class ListViewCell(action: (String) => Unit) extends AnchorPane {
  val label = new Label
  val button = new Button
  button.setOnAction(new EventHandler[ActionEvent] {
    def handle(p1: ActionEvent) = {
      action(label.getText)
    }
  })
  button.getStylesheets.add("com/ergle/main.css")
  button.getStyleClass.add("trashButton")

  getChildren.addAll(label, button)
  AnchorPane.setLeftAnchor(label, 0)
  AnchorPane.setTopAnchor(label, 0)
  AnchorPane.setRightAnchor(button, 0)
  AnchorPane.setTopAnchor(button, 0)

  def getTextProperty = {
    label.textProperty()
  }

  def buttonVisibleProperty = {
    button.visibleProperty()
  }
}
