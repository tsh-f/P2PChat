package UI

import Chats.{MessageToPublish, PrivateMessage}
import Source.ClusterChat
import akka.actor.ActorRef
import javafx.application.{Application, Platform}
import javafx.concurrent.{Service, Task}
import javafx.scene.Scene
import javafx.scene.control.{Button, TextArea, TextField}
import javafx.scene.layout.VBox
import javafx.stage.Stage

class ChatUI extends Application {
  private var name = ""
  private var counter: Int = 0
  private val root: VBox = new VBox()
  private val scene: Scene = new Scene(root, 400, 400)
  private val text: TextArea = new TextArea()
  private val fieldForMessage: TextField = new TextField()
  private val submit: Button = new Button("Enter")
  private var clusterChat: ClusterChat = _
  private var actors: Array[ActorRef] = Array.empty
  private var publisher: ActorRef = _
  private var senderPrivateMessages: ActorRef = _
  private var str = ""
  private var indexString = 0
  text.setPrefSize(300, 300)
  text.setEditable(false)
  root.getChildren.addAll(text, fieldForMessage, submit)

  submit.setOnAction(e => {
    counter match {
      case 2 =>
        sendMessages()
      case 0 =>
        createActorSystem()
      case 1 =>
        createActors()
    }
  })

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Chat")
    primaryStage.setScene(scene)
    primaryStage.show()

    //    createService()

    primaryStage.setOnCloseRequest(e => {
      Platform.exit()
      System.exit(0)
    });
  }

  def createActorSystem(): Unit = {
    clusterChat = new ClusterChat(fieldForMessage.getText())
    fieldForMessage.clear()
    counter += 1
  }

  def createActors(): Unit = {
    while (!clusterChat.getCheckMemberStatus) {
      Thread.sleep(1000)
    }
    name = fieldForMessage.getText()
    actors = clusterChat.createActors(name, this)
    publisher = actors(1)
    senderPrivateMessages = actors(3)
    fieldForMessage.clear()
    counter += 1
  }

  def createService(): Unit = {
    val service = new Service[Void] {
      override def createTask(): Task[Void] = {
        () => {
          while (true) {
            Thread.sleep(5000)
          }
          null
        }
      }
    }
    service.start()
  }

  def printMessage(str: String, name: String): Unit = {
    text.appendText(name + ": " + str + "\n")
  }

  def sendMessages(): Unit ={
    str = fieldForMessage.getText()
    if (str.charAt(0).equals('@')) {
      indexString = str.indexOf(" ")
      senderPrivateMessages ! PrivateMessage(str.substring(indexString, str.length), name)
    } else {
      publisher ! MessageToPublish(str, name)
    }
    fieldForMessage.clear()
  }
}

object ChatUI {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ChatUI], args: _*)
  }
}
