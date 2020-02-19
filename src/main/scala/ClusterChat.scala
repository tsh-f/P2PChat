import Chats.{ChatRoom, PrivateChatDestination, PrivateChatSender, PrivateMessage, Publisher}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.cluster.Cluster
import com.typesafe.config.{Config, ConfigFactory}

trait Command
case class Message(msg: String) extends Command
case class Name(msg: String) extends Command
case class Ip(ip:String) extends Command

class ClusterChat extends Actor {
  val systemOfUI: ActorSystem = ActorSystem("UI")
  val chatUI: ActorRef = systemOfUI.actorOf(Props(classOf[ChatUI], systemOfUI))
  var name: String = ""
  var ip: String = ""

  def createActors(system: ActorSystem): Unit ={
    val actor = system.actorOf(Props[ChatRoom], ip.toString)
    val publisher = system.actorOf(Props[Publisher], ip.toString + "pub")
    val privateChat = system.actorOf(Props[PrivateChatDestination], name)
    val senderPrivateMessages = system.actorOf(Props[PrivateChatSender])
    Thread.sleep(15000)
    publisher ! "Hi all"
    Thread.sleep(10000)
    publisher ! "FCK MY LIFE"
    senderPrivateMessages ! PrivateMessage(s"Hi, it's $name", "dad")
  }

  override def receive: Receive = {
    case Name(msg) =>
      name = msg
    case Ip(msg) =>
      ip = msg
      createConnection()
  }

  def createConnection(): Unit ={
    val config: Config = ConfigFactory.parseString(s"""akka.remote.artery.canonical.hostname = "127.0.0.$ip"""")
      .withFallback(ConfigFactory.load())
    val system: ActorSystem = ActorSystem("Cluster", config)
    val cluster: Cluster = Cluster(system)
    cluster.registerOnMemberUp({
      createActors(system)
    })
  }
}

object Node1 extends App {
  new ClusterChat
}