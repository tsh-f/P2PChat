package Chats

import UI.ChatUI
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Put, Send}

class PrivateChatDestination(chatUI: ChatUI) extends Actor with ActorLogging{
  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Put(self)
  override def receive: Receive = {
    case PrintPrivateMessage(msg, name) =>
      chatUI.printMessage(msg, name)
  }
}

class PrivateChatSender(chatUI: ChatUI) extends Actor with ActorLogging{
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case PrivateMessage(msg, name, sender) =>
      mediator ! Send(path = s"/user/$name", PrintPrivateMessage(msg, sender), localAffinity = true)
  }
}
