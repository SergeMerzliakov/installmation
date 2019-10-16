package org.installmation

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

class InstallmationApplication : Application() {

   companion object {
      val log: Logger = LogManager.getLogger(InstallmationApplication::class.java)

      @JvmStatic
      fun main(args: Array<String>) {
         launch(InstallmationApplication::class.java, *args)
      }
   }
   
   override fun start(primaryStage: Stage) {
      log.info("Starting Installmation")
      log.info("Path is ${File(".").absolutePath}")
      val root = FXMLLoader.load<Parent>(javaClass.getResource("/installmation.fxml"))
      primaryStage.title = "Installmation"
      primaryStage.scene = Scene(root)
      primaryStage.show()
   }
}