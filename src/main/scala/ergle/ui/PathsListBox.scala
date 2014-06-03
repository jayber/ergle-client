package ergle.ui

import javafx.scene.layout.{Priority, VBox}
import javafx.collections.ObservableList
import javafx.scene.Node
import scala.collection.JavaConverters._
import ergle.{TagAndPath, PathsConfig}
import java.io.File
import ergle.synch.FileLister

import scala.collection.mutable

class PathsListBox extends VBox with PathsConfig {

  loadContent()
  VBox.setVgrow(this, Priority.ALWAYS)

  def add(path: String) = {
    val row = createNewRow(TagAndPath(None, path), getChildren.size())
    if (!getChildren.contains(row)) {
      getChildren.add(row)
      purgeTrackingFiles(path)
      save()
    }
  }

  def handleTrashAction(source: ListViewRow): Unit = {
    getChildren.remove(source)
    save()
  }

  def loadContent() {
    val content = read()
    val children: ObservableList[Node] = getChildren
    children.clear()

    for (tagAndPath <- content) {
      children.add(createNewRow(tagAndPath, children.size()))
    }
  }

  def createNewRow(tagAndPath: TagAndPath, size: Int): ListViewRow = {
    new ListViewRow(tagAndPath, handleTrashAction, (size % 2) == 1, save)
  }

  def purgeTrackingFiles(directory: String) {
    val files = new File(directory, FileLister.trackingDirectory).listFiles()
    if (files != null && files.length > 0) {
      files.foreach(_.delete())
    }
  }

  private def save() {
    save(getChildren.asScala.map[String, mutable.Buffer[String]] { item =>
      val row = item.asInstanceOf[ListViewRow]
      (if (row.tag == null) "" else row.tag) + "=" + row.pathLabel.getText
    })
  }
}
