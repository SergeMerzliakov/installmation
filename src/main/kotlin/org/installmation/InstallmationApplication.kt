/*
 * Copyright 2019 Serge Merzliakov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.installmation

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
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
import org.installmation.configuration.JsonParserFactory
import org.installmation.configuration.UserHistory
import org.installmation.controller.InstallmationController
import org.installmation.io.ApplicationJsonReader
import org.installmation.service.*


class InstallmationApplication : Application() {

   companion object {
      val log: Logger = LogManager.getLogger(InstallmationApplication::class.java)
      const val WINDOW_TITLE = "Installmation"
      
      @JvmStatic
      fun main(args: Array<String>) {
         launch(InstallmationApplication::class.java, *args)
      }
   }

   private lateinit var applicationStage: Stage
   private val eventBus = EventBus("installmationApp")
   
   override fun start(primaryStage: Stage) {
      val configuration = loadConfiguration(eventBus)
      startApplication(primaryStage, configuration, eventBus)
   }

   /**
    * Integration Test friendly entry point. Need to setup Stage
    */
   fun startApplication(primaryStage: Stage, configuration: Configuration, eventBus: EventBus) {
      applicationStage = primaryStage
      try {
         log.info("Starting Installmation from configuration root: ${configuration.baseDirectory.path}")
         eventBus.register(this)

         val projectService = ProjectService(configuration)
         val workspace = loadWorkspace(configuration, projectService)
         val controller = InstallmationController(configuration, UserHistory(), workspace, projectService)

         val loader = FXMLLoader(javaClass.getResource("/fxml/installmation.fxml"))
         loader.setController(controller)
         val root = loader.load<Pane>()
         setupEventHandlers(primaryStage, workspace, configuration)
         primaryStage.title = WINDOW_TITLE
         primaryStage.scene = Scene(root)
         primaryStage.show()
         fireStartupEvents(workspace)
      } catch (e: Throwable) {
         errorDialog("Fatal Startup Error", e.message ?: e.toString())
         log.error("Fatal startup error - aborting and shutting down", e)
         primaryStage.close()
      }
   }

   private fun setupEventHandlers(stage: Stage, workspace: Workspace, configuration: Configuration) {
      stage.setOnCloseRequest {
         workspace.save()
         configuration.save()
         log.info("Installmation Application has shutdown")
      }
   }

   /**
    * After UI setup fire events to update UI from model
    */
   private fun fireStartupEvents(workspace: Workspace) {
      if (workspace.currentProject != null)
         eventBus.post(ProjectLoadedEvent(workspace.currentProject!!))
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

   private fun loadWorkspace(configuration: Configuration, projectService: ProjectService): Workspace {
      log.debug("Started loading workspace...")
      val location = Workspace.workspaceFile(configuration.baseDirectory)
      if (location.exists()) {
         log.debug("Workspace file found. Loading from ${location.canonicalPath}")

         val reader = ApplicationJsonReader<Workspace>(Workspace::class, location, JsonParserFactory.workspaceParser(configuration))
         val workspace = reader.load()
         workspace.configuration = configuration
         workspace.projectService = projectService
         log.info("Loaded workspace successfully from ${location.canonicalPath}")
         return workspace
      } else {
         log.info("No workspace file found - may be first startup. Creating default workspace")
         return Workspace(configuration, projectService)
      }
   }

   private fun loadConfiguration(eventBus:EventBus): Configuration {
      log.debug("Started loading configuration...")
      val location = Configuration.configurationFile()
      if (location.exists()) {
         log.debug("Configuration file found. Loading from ${location.canonicalPath}")

         val reader = ApplicationJsonReader<Configuration>(Configuration::class, location, JsonParserFactory.configurationParser())
         val configuration = reader.load()
         log.info("Loaded configuration successfully from ${location.canonicalPath}")
         configuration.initEventBus(eventBus)
         return configuration
      } else {
         log.info("No configuration file found - may be first startup. Creating default configuration")
         return Configuration(eventBus)
      }
   }

   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectCreated(e: ProjectCreatedEvent) {
      applicationStage.title = "${WINDOW_TITLE} - ${e.project.name}"
   }


   @Subscribe
   fun handleProjectLoaded(e: ProjectLoadedEvent) {
      applicationStage.title = "${WINDOW_TITLE} - ${e.project.name}"
   }

   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      applicationStage.title = WINDOW_TITLE
   }
}

