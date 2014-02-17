package ergle.ui

import javafx.scene.layout.{Priority, VBox}
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.Node
import scala.collection.JavaConverters._

class PathsListBox extends VBox {
  var cont: ObservableList[String] = FXCollections.observableArrayList()
  VBox.setVgrow(this, Priority.ALWAYS)

  def handleTrashAction(path: String): Unit = {
    cont.remove(path)
  }

  def content = cont

  def content_=(content: ObservableList[String]) {
    synchContent(content)
    content.addListener(ClientController.getFunctionAsListChangeListener(() => {
      synchContent(content)
    }))
    this.cont = content
  }

  def synchContent(content: ObservableList[String]) {
    val children: ObservableList[Node] = getChildren
    children.clear()

    for (path <- content.asScala) {
      children.add(new ListViewRow(path, handleTrashAction, (children.size() % 2) == 1))
    }
  }
}
