package Chats

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}

class ChatRoom extends Actor with ActorLogging {
  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("Chats.ChatRoom", self)

  override def receive: Receive = {
    case str: String =>
      log.info("Got {}", str)
    case SubscribeAck(Subscribe("Chats.ChatRoom", None, self)) =>
      log.info("subscribing")
  }
}

class Publisher extends Actor {
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case msg: String =>
      mediator ! Publish("Chats.ChatRoom", msg)
  }
}