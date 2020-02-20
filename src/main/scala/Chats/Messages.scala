package Chats

trait Messages
case class MessageToPublish(msg: String, name: String) extends Messages
