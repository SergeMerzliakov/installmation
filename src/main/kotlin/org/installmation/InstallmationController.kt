package org.installmation

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class InstallmationController {
   
   companion object {
      val log: Logger = LogManager.getLogger(InstallmationController::class.java)
   }

   @FXML
   private lateinit var closeButton: Button

   @FXML
   fun shutdown() {
      val stage = closeButton.scene.window as Stage
      stage.close()
      log.info("Shutting down Installmation Application")
   }
}