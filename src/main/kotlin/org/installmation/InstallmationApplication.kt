package org.installmation

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.controller.InstallmationController
import org.installmation.io.ApplicationJsonReader
import org.installmation.io.ApplicationJsonWriter
import org.installmation.model.JsonParserFactory
import org.installmation.model.Workspace
import org.installmation.service.ProjectService
import java.io.File


class InstallmationApplication : Application() {

   companion object {
      val log: Logger = LogManager.getLogger(InstallmationApplication::class.java)

      @JvmStatic
      fun main(args: Array<String>) {
         launch(InstallmationApplication::class.java, *args)
      }
   }

   private fun setupEventHandlers(stage: Stage, configuration: Configuration, workspace: Workspace) {

      stage.setOnCloseRequest {
         // configuration
         val configWriter = ApplicationJsonWriter<Configuration>(Configuration.configurationFile(), JsonParserFactory.configurationParser())
         configWriter.save(configuration)

         // workspace
         val workspaceWriter = ApplicationJsonWriter<Workspace>(Workspace.workspaceFile(), JsonParserFactory.basicParser())
         workspaceWriter.save(workspace)

         InstallmationController.log.info("Installmation Application has shutdown")
      }
   }
   
   override fun start(primaryStage: Stage) {

      try {
         log.info("Starting Installmation from ${File(".").canonicalPath}")

         // step 1 - Configuration
         val configuration = loadConfiguration()

         // step 2 - Configuration
         val workspace = loadWorkspace()

         // step 3 - Services
         val projectService = ProjectService(configuration)

         // step 4 - Controllers
         val controller = InstallmationController(configuration, projectService)

         // step 5 - Global Event Handlers
         setupEventHandlers(primaryStage, configuration, workspace)

         // step 6 - UI
         val loader = FXMLLoader(javaClass.getResource("/installmation.fxml"))
         loader.setController(controller)
         val root = loader.load<Pane>()
         primaryStage.title = "Installmation"
         primaryStage.scene = Scene(root)
         primaryStage.show()
      } catch (e: Throwable) {
         errorDialog("Fatal Startup Error", e.message ?: e.toString())
         log.error("Fatal startup error - aborting and shutting down", e)
         primaryStage.close()
      }
   }

   /**
    * SHow for all fatal unexpected errors on start
    */
   private fun errorDialog(title: String, msg: String) {
      val textArea = TextArea(msg)
      textArea.isEditable = false
      textArea.isWrapText = true
      val anchorPane = AnchorPane()
      anchorPane.maxWidth = 300.0
      anchorPane.maxHeight = 110.0
      anchorPane.children.add(textArea)
      AnchorPane.setTopAnchor(textArea, 14.0)
      AnchorPane.setBottomAnchor(textArea, 14.0)
      AnchorPane.setLeftAnchor(textArea, 14.0)
      AnchorPane.setRightAnchor(textArea, 14.0)
      val alert = Alert(AlertType.ERROR)
      alert.title = title
      alert.dialogPane.content = anchorPane
      alert.showAndWait()
   }

   private fun loadWorkspace(): Workspace {
      log.debug("Started loading workspace...")
      val location = Workspace.workspaceFile()
      if (location.exists()) {
         log.debug("Workspace file found. Loading from ${location.canonicalPath}")

         val reader = ApplicationJsonReader<Workspace>(Workspace::class, location, JsonParserFactory.basicParser())
         val workspace = reader.load()
         log.info("Loaded workspace successfully from ${location.canonicalPath}")
         return workspace
      } else {
         log.info("No workspace file found - may be first startup. Creating default workspace")
         return Workspace()
      }
   }

   private fun loadConfiguration(): Configuration {
      log.debug("Started loading configuration...")
      val location = Configuration.configurationFile()
      if (location.exists()) {
         log.debug("Configuration file found. Loading from ${location.canonicalPath}")

         val reader = ApplicationJsonReader<Configuration>(Configuration::class, location, JsonParserFactory.configurationParser())
         val configuration = reader.load()
         log.info("Loaded configuration successfully from ${location.canonicalPath}")
         return configuration
      } else {
         log.info("No configuration file found - may be first startup. Creating default configuration")
         return Configuration()
      }
   }
}