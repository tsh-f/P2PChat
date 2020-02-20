package Source

import akka.actor.ActorRef
import javafx.scene.control.{Button, TextField}

class MessagesSender(submit: Button, field: TextField) {
  private var counter: Int = 0
  private var actors: Array[ActorRef] = Array.empty
  private var publisher: ActorRef = _
  private var senderPrivateMessages: ActorRef = _
  private var str = ""
  private var indexString = 0

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


}
