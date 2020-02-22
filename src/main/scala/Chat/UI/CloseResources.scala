package Chat.UI

import java.io.PrintWriter

import javafx.application.Platform
import javafx.stage.Stage

class CloseResources(chatUI: ChatUI, chatLog: PrintWriter) {
  val stage: Stage = chatUI.getStage

  stage.setOnCloseRequest(e => {
    chatLog.close()
    Platform.exit()
    System.exit(0)
  })

}
