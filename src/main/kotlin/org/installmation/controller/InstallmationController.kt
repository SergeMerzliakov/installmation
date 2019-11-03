package org.installmation.controller

import com.google.common.eventbus.Subscribe
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.io.ApplicationJsonWriter
import org.installmation.model.JsonParserFactory
import org.installmation.model.Workspace
import org.installmation.model.binary.JDK
import org.installmation.model.binary.OperatingSystem
import org.installmation.service.*
import org.installmation.ui.dialog.BinaryArtefactDialog
import org.installmation.ui.dialog.MutablePair
import org.installmation.ui.dialog.SingleValueDialog
import java.io.File

class InstallmationController(private val configuration: Configuration,
                              private val workspace: Workspace,
                              private val projectService: ProjectService) {
   
   companion object {
      val log: Logger = LogManager.getLogger(InstallmationController::class.java)
   }

   @FXML private lateinit var applicationMenuBar: MenuBar
   @FXML private lateinit var newProjectMenuItem: MenuItem
   @FXML private lateinit var openProjectMenuItem: MenuItem
   @FXML private lateinit var projectNameField: TextField
   @FXML private lateinit var applicationVersionField: TextField
   @FXML private lateinit var copyrightField: TextField
   @FXML private lateinit var jpackageComboBox: ComboBox<JDK>
   @FXML private lateinit var configureJPackageButton: Button
   @FXML private lateinit var javafxComboBox: ComboBox<File>
   @FXML private lateinit var configureJFXButton: Button
   @FXML private lateinit var mainJarField: TextField
   @FXML private lateinit var mainClassField: TextField
   @FXML private lateinit var classPathListView: ListView<File>
   @FXML private lateinit var modulePathListView: ListView<File>
   @FXML private lateinit var shutdownMenu: Menu

   //model
   private val jpackageLocations: ObservableList<JDK> = FXCollections.observableArrayList<JDK>()
   private val javafxLocations: ObservableList<File> = FXCollections.observableArrayList<File>()
   
   init {
      configuration.eventBus.register(this)
   }
   
   @FXML
   fun initialize() {
      if (OperatingSystem.os() == OperatingSystem.Type.OSX) {
         shutdownMenu.isDisable = true
         shutdownMenu.isVisible = false
      }
      fillConfiguredBinaries()
   }

   /**
    * JDKs and drop down lists
    */
   private fun fillConfiguredBinaries() {
      jpackageLocations.addAll(configuration.jdkEntries.values)
      jpackageComboBox.items = jpackageLocations.sorted()
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
      val sd = SingleValueDialog(applicationStage(), "Project Name", "Choose Project Name", "myProject")
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
   fun saveProject() {
      val current = workspace.currentProject
      if (current != null) {
         projectService.saveProject(current)
         configuration.eventBus.post(ProjectSavedEvent(current))
      }
   }

   @FXML
   fun configureJPackageBinaries() {
      val pairs = configuration.jdkEntries.values.map { MutablePair(it.name, it.path) }
      val dialog = BinaryArtefactDialog(applicationStage(), "JPackager JDKs", pairs)
      val result = dialog.showAndWait()
      if (result.ok) {
         val jpackageName = result.data?.first
         val jdk = jpackageComboBox.items.find { it.name == jpackageName }
         jpackageComboBox.selectionModel.select(jdk)
      }
   }

   @FXML
   fun configureJavaFxBinaries() {
   }

   private fun applicationStage(): Stage {
      return mainJarField.scene.window as Stage
   }
   
   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectCreated(e: ProjectCreatedEvent) {
      projectNameField.text = e.project.name
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

}




