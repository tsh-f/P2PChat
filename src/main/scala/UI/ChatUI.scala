package UI

import Source.MessagesSender
import javafx.application.{Application, Platform}
import javafx.scene.Scene
import javafx.scene.control.{Button, TextArea, TextField}
import javafx.scene.layout.VBox
import javafx.stage.Stage

class ChatUI extends Application {
  private val root: VBox = new VBox()
  private val scene: Scene = new Scene(root, 400, 400)
  private val text: TextArea = new TextArea()
  private val fieldForMessage: TextField = new TextField()
  private val submit: Button = new Button("Enter")
  private val messagesSender = new MessagesSender(submit, fieldForMessage, this)
  text.setPrefSize(300, 300)
  text.setEditable(false)
  root.getChildren.addAll(text, fieldForMessage, submit)


  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Chat")
    primaryStage.setScene(scene)
    primaryStage.show()

    primaryStage.setOnCloseRequest(e => {
      Platform.exit()
      System.exit(0)
    });
  }

  def printMessage(str: String, name: String): Unit = {
    text.appendText(name + ": " + str + "\n")
  }
}

object ChatUI {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ChatUI], args: _*)
  }
}
