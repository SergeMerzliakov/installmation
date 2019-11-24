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

package org.installmation.controller

import com.google.common.eventbus.Subscribe
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.Tooltip
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.UserHistory
import org.installmation.io.ApplicationJsonWriter
import org.installmation.model.*
import org.installmation.model.binary.OperatingSystem
import org.installmation.service.*
import org.installmation.ui.dialog.*


class InstallmationController(private val configuration: Configuration,
                              private val userHistory: UserHistory,
                              private val workspace: Workspace,
                              private val projectService: ProjectService) {
   
   companion object {
      val log: Logger = LogManager.getLogger(InstallmationController::class.java)
      const val PROPERTY_HELP_GENERATE_SCRIPTS = "help.generate.scripts"
      const val PROPERTY_HELP_GENERATE_IMAGE = "help.generate.image"
      const val PROPERTY_HELP_GENERATE_INSTALLER = "help.generate.installer"
   }

   @FXML private lateinit var applicationMenuBar: MenuBar
   @FXML private lateinit var dependenciesPane: AnchorPane
   @FXML private lateinit var locationPane: AnchorPane
   @FXML private lateinit var binariesPane: AnchorPane
   @FXML private lateinit var generalInfoPane: AnchorPane
   @FXML private lateinit var executablePane: AnchorPane
   @FXML private lateinit var shutdownMenu: Menu

   @FXML private lateinit var generateScriptTooltip :Tooltip
   @FXML private lateinit var generateImageTooltip :Tooltip
   @FXML private lateinit var generateInstallerTooltip : Tooltip

   private var dependenciesController = DependenciesController(configuration,
         userHistory,
         workspace,
         projectService)
   
   private var locationController = LocationController(configuration,
         userHistory,
         workspace,
         projectService)

   private var binariesController = BinariesController(configuration,
         userHistory,
         workspace,
         projectService)

   private var generalInfoController = GeneralInfoController(configuration,
         userHistory,
         workspace,
         projectService)

   private var executeController = ExecutableController(configuration,
         userHistory,
         workspace,
         projectService)

   
   init {
      configuration.eventBus.register(this)
   }
   
   @FXML
   fun initialize() {
      if (OperatingSystem.os() == OperatingSystem.Type.OSX) {
         shutdownMenu.isDisable = true
         shutdownMenu.isVisible = false
         applicationMenuBar.useSystemMenuBarProperty().set(true)
      }
      initializeChildControllers()
      initializeTooltips()
   }

   private fun initializeTooltips() {
      generateScriptTooltip.text = configuration.resourceBundle.getString(PROPERTY_HELP_GENERATE_SCRIPTS)
      generateImageTooltip.text = configuration.resourceBundle.getString(PROPERTY_HELP_GENERATE_IMAGE)
      generateInstallerTooltip.text = configuration.resourceBundle.getString(PROPERTY_HELP_GENERATE_INSTALLER)
   }
   
   
   private fun initializeChildControllers() {
      // load file list UI and insert into it's pane in the application
      setupChildController("/fxml/dependenciesTab.fxml", dependenciesController, dependenciesPane)
      setupChildController("/fxml/locationTab.fxml", locationController, locationPane)
      setupChildController("/fxml/binariesTab.fxml", binariesController, binariesPane)
      setupChildController("/fxml/generalInfoTab.fxml", generalInfoController, generalInfoPane)
      setupChildController("/fxml/executableTab.fxml", executeController, executablePane)
   }
   
   @FXML
   fun shutdown() {
      applicationStage().close()
      log.info("Shutting down Installmation Application")
   }

   @FXML
   fun newProject() {
      // get a name first
      val sd = SingleValueDialog(applicationStage(), "Choose Project Name", "Project Name", "myProject")
      val result = sd.showAndWait()
      if (result.ok) {
         val project = projectService.newProject(result.data!!)
         log.debug("Created project ${project.name}")
         workspace.setCurrentProject(project)
         configuration.eventBus.post(ProjectCreatedEvent(project))
      }
   }

   @FXML
   fun openProject() {
      val p = projectService.loadProject("myProject")
      configuration.eventBus.post(ProjectLoadedEvent(p))
   }

   @FXML
   fun closeProject() {
      val current = workspace.currentProject
      if (current != null) {
         //save then close
         projectService.saveProject(current)
         configuration.eventBus.post(ProjectSavedEvent(current))
         configuration.eventBus.post(ProjectClosedEvent(current))
      }
   }
   
   @FXML
   fun saveProject() {
      val current = workspace.currentProject
      if (current != null) {
         configuration.eventBus.post(ProjectBeginSaveEvent(current))
         projectService.saveProject(current)
         configuration.eventBus.post(ProjectSavedEvent(current))

         // save workspace as well
         // workspace
         val workspaceWriter = ApplicationJsonWriter<Workspace>(Workspace.workspaceFile(), JsonParserFactory.configurationParser())
         workspaceWriter.save(workspace)
      } else {
         // TODO - prompt to create project possibly
      }

   }

   @FXML
   fun aboutDialog() {
      val dialog = AboutDialog(applicationStage())
      dialog.showAndWait()
   }

   /**
    * Generate in a single directory all the artefacts required for the install, but NOT
    * the final installer
    */
   @FXML
   fun generateImage() {
      if (workspace.currentProject == null) {
         HelpDialog.showAndWait("Cannot Generate Image", "No project selected, created or loaded. Cannot generate an image.")
         return
      }

      try {
         log.info("Generate Image  - Validating configuration")
         val validationResult = workspace.currentProject?.validateConfiguration()
         if (validationResult?.success == false) {
            val errorDialog = ItemListDialog(applicationStage(), "Errors", "Issues", validationResult.errors)
            errorDialog.showNonModal()
            return
         }

         log.info("Generate Image  - Generating Image")

         log.info("Generate Image  - Image created successfully")
      } catch (e: Exception) {
         log.info("Generate Image  - Failed with error: ${e.message}", e)
      }
   }

   /*
     Will generate full installer file, creating an image as well
    */
   @FXML
   fun generateInstaller() {
      if (workspace.currentProject == null) {
         HelpDialog.showAndWait("Cannot Generate Installer", "No project selected, created or loaded. Cannot generate an installer.")
         return
      }

      try {
         log.info("Generate Installer  - Validating configuration")


         log.info("Generate Installer  - Generating Image")

         log.info("Generate Installer  - Installer created successfully")
      } catch (e: Exception) {
         log.info("Generate Installer  - Failed with error: ${e.message}", e)
      }
   }

   /**
    * Generate scripts of creating images and installers for
    * Mac/Linux and Windows
    */
   @FXML
   fun generateScripts() {
      if (workspace.currentProject == null) {
         HelpDialog.showAndWait("Cannot Generate Scripts", "No project selected, created or loaded. Cannot generate installer scripts.")
         return
      }
      try {

         log.info("Generate Scripts  - Validating configuration")
         //workspace.currentProject.

         log.info("Generate Scripts  - Generating Scripts")

         log.info("Generate Scripts  - Scripts created successfully")
      } catch (e: Exception) {
         log.info("Generate Scripts  - Failed with error: ${e.message}", e)
      }
   }

   @FXML
   fun showAllJDK() {
      val dialog = jdkDialog()
      val result = dialog.showAndWait()
      if (result.ok) {
         // update jdk entries 
         val updatedModel = dialog.updatedModel()
         if (updatedModel != null) {
            configuration.eventBus.post(JDKListUpdatedEvent(updatedModel))
         }
      }
   }

   @FXML
   fun showAllJavaFX() {
      val dialog = javaFXFDialog()
      val result = dialog.showAndWait()
      if (result.ok) {
         // update model
         val updatedModel = dialog.updatedModel()
         if (updatedModel != null) {
            configuration.eventBus.post(JFXModuleUpdatedEvent(updatedModel))
         }
      }
   }

   private fun jdkDialog(): BinaryArtefactDialog {
      val items = configuration.jdkEntries.values.map { NamedDirectory(it.name, it.path) }
      return BinaryArtefactDialog(applicationStage(), "JPackager JDKs", items, userHistory)
   }

   private fun javaFXFDialog(): BinaryArtefactDialog {
      val items = configuration.javafxModuleEntries.values.map { NamedDirectory(it.name, it) }
      return BinaryArtefactDialog(applicationStage(), "JavaFX Module Directories", items, userHistory)
   }
   
   private fun applicationStage(): Stage {
      return applicationMenuBar.scene.window as Stage
   }

   private fun setupChildController(fxmlPath: String, controller: Any, parent: Pane) {
      log.debug("Loading child controller from $fxmlPath")
      val loader = FXMLLoader(javaClass.getResource(fxmlPath))
      loader.setController(controller)
      val pane = loader.load<Pane>()
      AnchorPane.setTopAnchor(pane, 0.0)
      AnchorPane.setLeftAnchor(pane, 0.0)
      AnchorPane.setBottomAnchor(pane, 0.0)
      AnchorPane.setRightAnchor(pane, 0.0)
      parent.children.add(pane)
      log.debug("Child controller initialized successfully - $fxmlPath")
   }
   
   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectCreated(e: ProjectCreatedEvent) {
   }

   @Subscribe
   fun handleProjectLoaded(e: ProjectLoadedEvent) {
      
   }
   
   @Subscribe
   fun handleProjectUpdated(e: ProjectUpdatedEvent) {
   }

   @Subscribe
   fun handleProjectDeleted(e: ProjectDeletedEvent) {
   }

   @Subscribe
   fun handleProjectSaved(e: ProjectSavedEvent) {
   }

   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      workspace.closeCurrentProject()
   }

}




