package Chat.Source

import java.io.{File, PrintWriter}

import Chat.Chats.{MessageToPublish, PrivateMessage}
import Chat.UI.{ChatUI, CloseResources}
import akka.actor.ActorRef

class MessagesSender(chatUI: ChatUI) {
  private val text = chatUI.getTestArea
  private val field = chatUI.getTextField
  private val submit = chatUI.getButton
  private var name = ""
  private var counter: Int = 0
  private var clusterChat: ClusterChat = _
  private var actors: Array[ActorRef] = Array.empty
  private var publisher: ActorRef = _
  private var senderPrivateMessages: ActorRef = _
  private var chatLog: PrintWriter = _
  private var str = ""
  private var indexString = 0
  text.setText("Введите ip(x): 127.0.0.x")

  submit.setOnAction(e => {
    counter match {
      case 2 =>
        sendMessage()
      case 0 =>
        createActorSystem()
      case 1 =>
        createActors()
    }
  })

  def printMessage(str: String, name: String): Unit = {
    val message = name + ": " + str + "\n"
    text.appendText(message)
    writeMessage(message)
  }

  def writeMessage(message: String): Unit ={
    chatLog.write(message)
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
    chatLog = new PrintWriter(new File(s"${name}_chatLog.txt"))
    val closeResources = new CloseResources(chatUI, chatLog)
    actors = clusterChat.createActors(name, this)
    publisher = actors(1)
    senderPrivateMessages = actors(3)
    field.clear()
    text.clear()
    counter += 1
  }

  def sendMessage(): Unit ={
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