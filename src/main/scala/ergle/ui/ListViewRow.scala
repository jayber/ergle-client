package ergle.ui

import javafx.scene.layout.{Priority, GridPane}
import javafx.scene.control._
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Insets
import javafx.collections.FXCollections
import javafx.beans.value.{ObservableValue, ChangeListener}
import ergle.TagAndPath

class ListViewRow(val tagAndPath: TagAndPath, trashButtonAction: (ListViewRow) => Unit, alternateRow: Boolean, tagNameChangeAction: () => Unit) extends GridPane {
  setUserData(tagAndPath)
  val pathLabel = new Label()
  val tagControl = new ComboBox[String]()
  private val list = FXCollections.observableArrayList[String]()
  tagAndPath.tag match {
    case Some(tagName) => list.add(tagName)
      tagControl.setValue(tagName)
    case None =>
  }
  pathLabel.setText(tagAndPath.path)
  pathLabel.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS)
  tagControl.setItems(list)
  tagControl.setEditable(true)
  tagControl.setMinWidth(150)
  tagControl.setPromptText("enter tag")
  tagControl.setTooltip(new Tooltip("Enter a new or existing name to tag files with, or leave blank"))
  tagControl.valueProperty().addListener(new ChangeListener[String] {
    override def changed(p1: ObservableValue[_ <: String], p2: String, p3: String): Unit = {
      tagNameChangeAction()
    }
  })

  val button = new Button
  button.setOnAction(new EventHandler[ActionEvent] {
    def handle(p1: ActionEvent) = {
      trashButtonAction(ListViewRow.this)
    }
  })
  button.getStyleClass.add("trashButton")
  button.setTooltip(new Tooltip("Click to delete"))

  getChildren.addAll(pathLabel, tagControl, button)
  GridPane.setConstraints(pathLabel, 0, 0)
  GridPane.setConstraints(tagControl, 1, 0)
  GridPane.setConstraints(button, 2, 0)
  GridPane.setHgrow(pathLabel, Priority.SOMETIMES)
  GridPane.setHgrow(tagControl, Priority.SOMETIMES)
  GridPane.setHgrow(button, Priority.NEVER)
  GridPane.setMargin(tagControl, new Insets(0, 0, 0, 10))

  setPadding(new Insets(3))
  getStyleClass.add("pathRow")
  if (alternateRow) {
    getStyleClass.add("alternateRow")
  }

  def tag = tagControl.getValue

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case it: ListViewRow => pathLabel.getText.equals(it.pathLabel.getText)
      case _ => false
    }
  }
}
