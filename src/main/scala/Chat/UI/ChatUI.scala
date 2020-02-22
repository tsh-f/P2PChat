package Chat.UI

import Chat.Source.MessagesSender
import javafx.application.{Application, Platform}
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.{Button, TextArea, TextField}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.FlowPane
import javafx.stage.Stage

class ChatUI extends Application {
  private val root: FlowPane = new FlowPane()
  private val scene: Scene = new Scene(root)
  private val fieldForMessage: TextField = new TextField()
  private val submit: Button = new Button("Enter")
  private val text: TextArea = new TextArea()
  private var stage: Stage = _
  private val messagesSender = new MessagesSender(this)
  root.setAlignment(Pos.CENTER)
  fieldForMessage.setPrefColumnCount(30)
  text.setPrefSize(400, 400)
  text.setEditable(false)
  text.setWrapText(true)

  root.getChildren.addAll(text, fieldForMessage, submit)

  override def start(primaryStage: Stage): Unit = {
    stage = primaryStage
    primaryStage.setTitle("Chat")
    primaryStage.setScene(scene)
    primaryStage.show()

    primaryStage.setOnCloseRequest(e => {
      Platform.exit()
      System.exit(0)
    })
  }

  fieldForMessage.setOnKeyPressed((e: KeyEvent) => {
    if (e.getCode.equals(KeyCode.ENTER))
      submit.fire()
  })

  def getTestArea: TextArea = text
  def getTextField: TextField = fieldForMessage
  def getButton: Button = submit
  def getStage: Stage = stage
}

object ChatUI {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ChatUI], args: _*)
  }
}
