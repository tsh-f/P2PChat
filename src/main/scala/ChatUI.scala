import javafx.application.{Application, Platform}
import javafx.scene.Scene
import javafx.scene.control.{Button, TextArea, TextField}
import javafx.scene.layout.VBox
import javafx.stage.Stage

class ChatUI extends Application {
  private var counter: Int = 0
  private val root: VBox = new VBox()
  private val scene: Scene = new Scene(root, 400,400)
  private val text: TextArea = new TextArea()
  private val fieldForMessage: TextField = new TextField()
  private val submit: Button = new Button("Enter")
  private var clusterChat: ClusterChat = _
  text.setPrefSize(300,300)
  root.getChildren.addAll(text, submit, fieldForMessage)

  submit.setOnAction(e => {
    counter match {
      case 2 =>

      case 0 =>
        createActorSystem()
        counter += 1
      case 1 =>
        createActors()
        counter += 1
    }
  })

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Chat")
    primaryStage.setScene(scene)
    primaryStage.show()

    primaryStage.setOnCloseRequest(e => Platform.exit());
  }

  def createActorSystem(): Unit = {
    clusterChat = new ClusterChat(fieldForMessage.getText())
    counter += 1
  }

  def createActors(): Unit = {
    while(!clusterChat.getCheckMemberStatus){
      Thread.sleep(1000)
    }
    clusterChat.createActors(fieldForMessage.getText())
  }
}

object ChatUI{
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ChatUI], args: _*)
  }
}
