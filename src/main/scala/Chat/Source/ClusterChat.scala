package Chat.Source

import Chat.Chats._
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.Cluster
import com.typesafe.config.{Config, ConfigFactory}

class ClusterChat(ip: String) {
  private val system: ActorSystem = createActorSystem
  private val cluster: Cluster = Cluster(system)
  private var check: Boolean = false
  private var name = ""
  private var actors: Array[ActorRef] = Array.empty

  cluster.registerOnMemberUp({
    check = true
  })

  def createActors(name: String, messagesSender: MessagesSender): Array[ActorRef] = {
    this.name = name
    actors = Array(
      system.actorOf(Props(classOf[ChatRoom], messagesSender), ip.toString),
      system.actorOf(Props[Publisher], ip.toString + "pub"),
      system.actorOf(Props(classOf[PrivateChatDestination], messagesSender), name),
      system.actorOf(Props[PrivateChatSender]))
    actors
  }

  private def createActorSystem: ActorSystem = {
    val config: Config = ConfigFactory.parseString(s"""akka.remote.artery.canonical.hostname = "127.0.0.$ip"""")
      .withFallback(ConfigFactory.load())
    val system: ActorSystem = ActorSystem("Cluster", config)
    system
  }

  def getActorSystem: ActorSystem = system

  def getCheckMemberStatus: Boolean = check
}

