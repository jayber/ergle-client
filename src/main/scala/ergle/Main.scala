package ergle

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.{WindowEvent, Stage}
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import javafx.event.EventHandler


object Main {

  val ctx: AnnotationConfigApplicationContext = new AnnotationConfigApplicationContext()

  def main(args: Array[String]) {
    Application.launch(classOf[Main], args: _*)
  }
}

class Main extends Application {

  def start(primaryStage: Stage) {
    val root: Parent = FXMLLoader.load(getClass.getResource("client.fxml"))
    primaryStage.getIcons.add(new Image("/favicon1.png"))
    primaryStage.getIcons.add(new Image("/favicon2.png"))
    primaryStage.getIcons.add(new Image("/favicon3.png"))
    primaryStage.getIcons.add(new Image("/favicon4.png"))
    primaryStage.setTitle("ergle")
    val scene: Scene = new Scene(root)
    primaryStage.setScene(scene)
    scene.getStylesheets.add("ergle/main.css")

    primaryStage.show()

    primaryStage.setOnCloseRequest(new EventHandler[WindowEvent] {
      def handle(p1: WindowEvent) {
        System.exit(0)
      }
    })

    startFileSynchronisation()
  }

  def startFileSynchronisation() {
    Main.ctx.scan("ergle")
    Main.ctx.refresh()
    Main.ctx.start()

    val syncher: Syncher = Main.ctx.getBean("syncher").asInstanceOf[Syncher]
    syncher.start()
  }
}


