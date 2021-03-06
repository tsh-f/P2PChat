package Chat.Chats

trait Messages
case class MessageToPublish(msg: String, name: String) extends Messages
case class PrivateMessage(msg: String, name: String, sender: String) extends Messages
case class PrintPrivateMessage(msg: String, name: String) extends Messages