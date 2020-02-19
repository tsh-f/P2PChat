package Chats

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Put, Send}

case class PrivateMessage(msg: String, name: String)

class PrivateChatDestination extends Actor with ActorLogging{
  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Put(self)
  override def receive: Receive = {
    case msg: String =>
      log.info("Got {}", msg)
  }
}

class PrivateChatSender extends Actor with ActorLogging{
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case PrivateMessage(msg, name) =>
      mediator ! Send(path = s"/user/$name", msg, localAffinity = true)
  }
}
