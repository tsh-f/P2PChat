package Chat.Chats

import Chat.Source.MessagesSender
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Put, Send}

class PrivateChatDestination(messagesSender: MessagesSender) extends Actor with ActorLogging{
  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Put(self)

  override def receive: Receive = {
    case PrintPrivateMessage(msg, name) =>
      messagesSender.printMessage(msg, name)
  }
}

class PrivateChatSender extends Actor with ActorLogging{
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case PrivateMessage(msg, name, sender) =>
      mediator ! Send(path = s"/user/$name", PrintPrivateMessage(msg, sender), localAffinity = true)
  }
}
