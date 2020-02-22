package Chat.Source

import Chat.Chats.{MessageToPublish, PrivateMessage}
import akka.actor.ActorRef
import javafx.scene.control.{Button, TextArea, TextField}

class MessagesSender(submit: Button, field: TextField, text: TextArea) {
  private var name = ""
  private var counter: Int = 0
  private var clusterChat: ClusterChat = _
  private var actors: Array[ActorRef] = Array.empty
  private var publisher: ActorRef = _
  private var senderPrivateMessages: ActorRef = _
  private var str = ""
  private var indexString = 0
  text.setText("Введите ip(x): 127.0.0.x")

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

  def printMessage(str: String, name: String): Unit = {
    text.appendText(name + ": " + str + "\n")
  }

  def createActorSystem(): Unit = {
    clusterChat = new ClusterChat(field.getText())
    field.clear()
    text.setText("Введите имя(eng)")
    counter += 1
  }

  def createActors(): Unit = {
    while (!clusterChat.getCheckMemberStatus) {
      Thread.sleep(1000)
    }
    name = field.getText()
    actors = clusterChat.createActors(name, this)
    publisher = actors(1)
    senderPrivateMessages = actors(3)
    field.clear()
    text.clear()
    counter += 1
  }

  def sendMessages(): Unit ={
    str = field.getText()
    if (str.charAt(0).equals('@')) {
      indexString = str.indexOf(" ")
      senderPrivateMessages ! PrivateMessage(str.substring(indexString, str.length), str.substring(1, indexString), name)
    } else {
      publisher ! MessageToPublish(str, name)
    }
    field.clear()
  }
}
