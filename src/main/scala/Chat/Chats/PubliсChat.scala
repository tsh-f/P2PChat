package Chat.Chats

import Chat.Source.MessagesSender
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}

class ChatRoom(messagesSender: MessagesSender) extends Actor with ActorLogging {
  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("ChatRoom", self)

  override def receive: Receive = {
    case MessageToPublish(msg, name) =>
      messagesSender.printMessage(msg, name)
  }
}

class Publisher extends Actor {
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case MessageToPublish(msg, name) =>
      mediator ! Publish("ChatRoom", MessageToPublish(msg, name))
  }
}