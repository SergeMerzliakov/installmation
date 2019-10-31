package org.installmation.controller

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.io.ApplicationJsonWriter
import org.installmation.configuration.Configuration
import org.installmation.model.JsonParserFactory
import org.installmation.model.InstallProject
import org.installmation.model.binary.OperatingSystem
import org.installmation.service.ProjectService
import org.installmation.ui.dialog.SingleValueDialog
import java.io.File

class InstallmationController(private val configuration: Configuration, private val projectService: ProjectService) {
   
   companion object {
      val log: Logger = LogManager.getLogger(InstallmationController::class.java)
   }

   @FXML private lateinit var applicationMenuBar: MenuBar
   @FXML private lateinit var newProjectMenuItem: MenuItem
   @FXML private lateinit var openProjectMenuItem: MenuItem
   @FXML private lateinit var projectNameField: TextField
   @FXML private lateinit var applicationVersionField: TextField
   @FXML private lateinit var copyrightField: TextField
   @FXML private lateinit var jpackageChoiceBox: ChoiceBox<File>
   @FXML private lateinit var configureJPackageButton: Button
   @FXML private lateinit var javafxChoiceBox: ChoiceBox<File>
   @FXML private lateinit var configureJFXButton: Button
   @FXML private lateinit var mainJarField: TextField
   @FXML private lateinit var mainClassField: TextField
   @FXML private lateinit var classPathListView: ListView<File>
   @FXML private lateinit var modulePathListView: ListView<File>
   @FXML private lateinit var shutdownMenu: Menu

   @FXML
   fun initialize() {
      if (OperatingSystem.os() == OperatingSystem.Type.OSX) {
         shutdownMenu.isDisable = true
         shutdownMenu.isVisible = false
      }
   }
   
   @FXML
   fun shutdown() {
      // save configuration
      val reader = ApplicationJsonWriter<Configuration>(Configuration.configurationFile(), JsonParserFactory.configurationParser())
      reader.save(configuration)
      
      val stage = mainJarField.scene.window as Stage
      stage.close()
      log.info("Shutting down Installmation Application")
   }

   @FXML
   fun newProject() {
      // get a name first
      val applicationStage = mainJarField.scene.window as Stage
      val sd = SingleValueDialog(applicationStage, "Project Name", "Choose Project Name", "myProject")
      val result = sd.showAndWait()
      if (result.ok) {
         val project = projectService.newProject(result.data)
         log.debug("Created project ${project.name}")
      }
   }

   @FXML
   fun openProject() {
      projectService.openProject("myProject")
   }

   @FXML
   fun saveProject() {
      projectService.saveProject(InstallProject())
   }
}