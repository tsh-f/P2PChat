package Chat.UI

import Chat.Source.MessagesSender
import javafx.application.{Application, Platform}
import javafx.scene.Scene
import javafx.scene.control.{Button, TextArea, TextField}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.VBox
import javafx.stage.Stage

class ChatUI extends Application {
  private val root: VBox = new VBox()
  private val scene: Scene = new Scene(root, 400, 350)
  private val fieldForMessage: TextField = new TextField()
  private val submit: Button = new Button("Enter")
  private val text: TextArea = new TextArea()
  private val messagesSender = new MessagesSender(submit, fieldForMessage, text)
  text.setPrefSize(300, 300)
  text.setEditable(false)
  text.setWrapText(true)

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

  fieldForMessage.setOnKeyPressed((e: KeyEvent) => {
    if (e.getCode.equals(KeyCode.ENTER))
      submit.fire()
  })
}

object ChatUI {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ChatUI], args: _*)
  }
}
