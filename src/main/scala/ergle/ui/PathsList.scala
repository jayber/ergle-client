package ergle.ui

import java.io.File
import javafx.beans.property.SimpleStringProperty
import javafx.collections.{FXCollections, ListChangeListener}
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.HPos
import javafx.scene.control._
import javafx.scene.layout.{GridPane, Priority}

import com.typesafe.scalalogging.slf4j.Logging
import ergle.PathsConfig
import ergle.synch.FileLister

import scala.collection.JavaConverters._

class PathsListBox(pane: GridPane, saveButton: Button) extends PathsConfig with Logging {

  var content = FXCollections.observableArrayList[SynchPath]()

  {
    val loadedContent = read()
    for (item <- loadedContent) {
      val liveItem = new SynchPath(item.shareTo.getOrElse(""), item.tag.getOrElse(""), item.path, saveButton)
      content.add(liveItem)
      createNewRow(liveItem, content.size)
    }

    content.addListener(new ListChangeListener[SynchPath] {
      override def onChanged(p1: ListChangeListener.Change[_ <: SynchPath]) {
        saveButton.setDisable(false)
      }
    })
  }

  def createNewRow(rowValues: SynchPath, row: Int) {
    val cols = 4

    val pathLabel = new Label()
    pathLabel.setText(rowValues.getPath)
    pathLabel.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS)

    val tagControl = new ComboBox[String]()
    val list = FXCollections.observableArrayList[String]()
    rowValues.getTag match {
      case "" =>
      case tagName => list.add(tagName)
    }
    tagControl.setValue(rowValues.getTag)
    rowValues.tag.bind(tagControl.valueProperty())
    tagControl.setItems(list)
    tagControl.setEditable(true)
    tagControl.setMinWidth(150)
    tagControl.setPromptText("enter tag")
    tagControl.setTooltip(new Tooltip("Enter a new or existing name to tag files with, or leave blank"))

    val shareToControl = new TextField()
    shareToControl.setPromptText("enter address")
    shareToControl.setTooltip(new Tooltip("Enter the address of a person or hub, or leave blank"))
    shareToControl.setText(rowValues.getShareTo)
    rowValues.shareTo.bind(shareToControl.textProperty())

    val button = new Button
    button.setOnAction(new EventHandler[ActionEvent] {
      def handle(p1: ActionEvent) = {
        content.remove(rowValues)
        //this is to remove empty rows from the GridPane
        pane.getChildren.remove(cols, pane.getChildren.size())
        var count = 1
        for (row <- content.asScala) {
          createNewRow(row, count)
          count += 1
        }
      }
    })

    button.getStyleClass.add("trashButton")
    button.setTooltip(new Tooltip("Click to delete"))

    pane.addRow(row, pathLabel, tagControl, shareToControl, button)

    GridPane.setHgrow(pathLabel, Priority.SOMETIMES)
    GridPane.setHgrow(tagControl, Priority.SOMETIMES)
    GridPane.setHgrow(shareToControl, Priority.SOMETIMES)
    GridPane.setHgrow(button, Priority.NEVER)

    GridPane.setHalignment(button, HPos.CENTER)
  }

  def add(path: String) = {
    val values = new SynchPath("", "", path, saveButton)

    var found = false
    for (item <- content.asScala) {
      found = values.getPath == item.getPath || found
    }

    if (!found) {
      content.add(values)
      createNewRow(values, content.size)
      purgeTrackingFiles(path)
    }
  }

  def purgeTrackingFiles(directory: String) {
    val files = new File(directory, FileLister.trackingDirectory).listFiles()
    if (files != null && files.length > 0) {
      files.foreach(_.delete())
    }
  }

  def save() {
    save(content.asScala.map { value =>
      value.getPath + "," + value.getTag + "," + value.getShareTo
    })
  }
}

class SynchPath(shareToVal: String, tagVal: String, pathVal: String, button: Button) {
  val tag = new SimpleStringProperty(tagVal)
  tag.addListener(new ChangeButton(button))
  val shareTo = new SimpleStringProperty(shareToVal)
  shareTo.addListener(new ChangeButton(button))
  val path = new SimpleStringProperty(pathVal)
  path.addListener(new ChangeButton(button))

  def getTag = tag.get

  def getPath = path.get

  def getShareTo = shareTo.get
}