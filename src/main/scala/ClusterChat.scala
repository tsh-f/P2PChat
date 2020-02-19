import java.util.Scanner

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import com.typesafe.config.{Config, ConfigFactory}

class ClusterChat {
  val scanner = new Scanner(System.in)
  val name: String = scanner.next()
  val ip: Int = scanner.nextInt()
  val config: Config = ConfigFactory.parseString(s"""akka.remote.artery.canonical.hostname = "127.0.0.$ip"""")
    .withFallback(ConfigFactory.load())
  val system: ActorSystem = ActorSystem("Cluster", config)
  val cluster: Cluster = Cluster(system)
  cluster.registerOnMemberUp({
    createActors()
    Thread.sleep(15000)
    publisher ! "Hi all"
    Thread.sleep(10000)
    println(cluster.state.members)
    publisher ! "FCK MY LIFE"
    senderPrivateMessages ! PrivateMessage(s"Hi, it's $name", "dad")
  })

  def createActors(): Unit ={
    val actor = system.actorOf(Props[ChatRoom], ip.toString)
    val publisher = system.actorOf(Props[Publisher], ip.toString + "pub")
    val privateChat = system.actorOf(Props[PrivateChatDestination], name)
    val senderPrivateMessages = system.actorOf(Props[PrivateChatSender])
  }
}

class ChatRoom extends Actor with ActorLogging {
  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("ChatRoom", self)

//  override def preStart(): Unit = {
//    Cluster(context.system).subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent],
//      classOf[UnreachableMember])
//  }
//
//  override def postStop(): Unit = Cluster(context.system).unsubscribe(self)

  override def receive: Receive = {
    case str: String =>
      log.info("Got {}", str)
    case SubscribeAck(Subscribe("ChatRoom", None, self)) =>
      log.info("subscribing")
  }
}

class Publisher extends Actor {
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case msg: String =>
      mediator ! Publish("ChatRoom", msg)
  }
}

object Node1 extends App {
  new ClusterChat
}