package Source

import Chats._
import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.typesafe.config.{Config, ConfigFactory}

class ClusterChat(ip: String) {
  private val system: ActorSystem = createConnection
  private val cluster: Cluster = Cluster(system)
  private var check: Boolean = false
  private var name = ""

  cluster.registerOnMemberUp({
    check = true
  })

  def createActors(name: String): Unit = {
    this.name = name
    val actor = system.actorOf(Props[ChatRoom], ip.toString)
    val publisher = system.actorOf(Props[Publisher], ip.toString + "pub")
    val privateChat = system.actorOf(Props[PrivateChatDestination], name)
    val senderPrivateMessages = system.actorOf(Props[PrivateChatSender])
    Thread.sleep(1000)
    publisher ! MessageToPublish("Hi all", name)
    Thread.sleep(5000)
    publisher ! "FCK MY LIFE"
    senderPrivateMessages ! PrivateMessage(s"Hi, it's $name", "dad")
  }

  private def createConnection: ActorSystem = {
    val config: Config = ConfigFactory.parseString(s"""akka.remote.artery.canonical.hostname = "127.0.0.$ip"""")
      .withFallback(ConfigFactory.load())
    val system: ActorSystem = ActorSystem("Cluster", config)
    system
  }

  def getActorSystem: ActorSystem = system

  def getCheckMemberStatus: Boolean = check
}
