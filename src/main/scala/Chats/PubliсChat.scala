package Chats

import UI.ChatUI
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}

class ChatRoom(chatUI: ChatUI) extends Actor with ActorLogging {
  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("ChatRoom", self)

  override def receive: Receive = {
    case MessageToPublish(msg, name) =>
      chatUI.printMessage(msg, name)
    case SubscribeAck(Subscribe("ChatRoom", None, self)) =>
      log.info("subscribing")
  }
}

class Publisher(chatUI: ChatUI) extends Actor {
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case MessageToPublish(msg, name) =>
      mediator ! Publish("ChatRoom", MessageToPublish(msg, name))
  }
}