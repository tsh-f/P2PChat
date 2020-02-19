import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.{Button, TextArea, TextField}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage

//case class SendMessage(msg: String)
//case class SetName(name: String)
//case class SetIp(ip: String)

class ChatUI(system:ActorSystem) extends Application with Actor{
  var counter = 0
  val actorOfCluster:ActorRef = system.actorOf(Props[ClusterChat])
  val root = new VBox()
  val scene = new Scene(root, 400,400)
  val text = new TextArea()
  val fieldForMessage = new TextField()
  val submit = new Button("Enter")
  text.setPrefSize(300,300)
  root.getChildren.addAll(text, submit, fieldForMessage)

  val nameHandler: EventHandler[MouseEvent] = {
    @Override
    def handle(e: MouseEvent) {
      actorOfCluster ! Name(fieldForMessage.getText)
    }
  };

  val ipHandler: EventHandler[MouseEvent] = {
    @Override
    def handle(e: MouseEvent) {
      actorOfCluster ! Ip(fieldForMessage.getText)
    }
  };

  val messageHandler: EventHandler[MouseEvent] ={
    @Override
    def handle(e: MouseEvent): Unit ={

    }
  }

  submit.setOnAction(e => {
    counter match {
      case 2 =>

      case 0 =>
        actorOfCluster ! Name(fieldForMessage.getText)
        counter += 1
      case 1 =>
        actorOfCluster ! Ip(fieldForMessage.getText)
        counter += 1
    }
  })

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Chat")
    primaryStage.setScene(scene)
    primaryStage.show()

    submit.addEventFilter(MouseEvent.MOUSE_PRESSED, nameHandler)
  }

  override def receive: Receive = {
    case _ => //ignoring
  }

  def getName() ={
    submit.addEventFilter(MouseEvent.MOUSE_PRESSED, nameHandler)
  }

  def getIp() ={
    submit.addEventFilter(MouseEvent.MOUSE_PRESSED, ipHandler)
  }
}
