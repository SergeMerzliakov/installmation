package org.installmation.controller

import com.google.common.eventbus.Subscribe
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import javafx.util.StringConverter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.UserHistory
import org.installmation.io.ApplicationJsonWriter
import org.installmation.model.JsonParserFactory
import org.installmation.model.NamedDirectory
import org.installmation.model.Workspace
import org.installmation.model.binary.JDK
import org.installmation.model.binary.JDKFactory
import org.installmation.model.binary.OperatingSystem
import org.installmation.service.*
import org.installmation.ui.dialog.AboutDialog
import org.installmation.ui.dialog.BinaryArtefactDialog
import org.installmation.ui.dialog.SingleValueDialog
import java.io.File


class InstallmationController(private val configuration: Configuration,
                              private val userHistory: UserHistory,
                              private val workspace: Workspace,
                              private val projectService: ProjectService) {
   
   companion object {
      val log: Logger = LogManager.getLogger(InstallmationController::class.java)
   }

   @FXML private lateinit var applicationMenuBar: MenuBar
   @FXML private lateinit var dependenciesPane: AnchorPane
   @FXML private lateinit var newProjectMenuItem: MenuItem
   @FXML private lateinit var openProjectMenuItem: MenuItem
   @FXML private lateinit var projectNameField: TextField
   @FXML private lateinit var applicationVersionField: TextField
   @FXML private lateinit var copyrightField: TextField
   @FXML private lateinit var jpackageComboBox: ComboBox<JDK>
   @FXML private lateinit var configureJPackageButton: Button
   @FXML private lateinit var javafxComboBox: ComboBox<NamedDirectory>
   @FXML private lateinit var configureJFXButton: Button
   @FXML private lateinit var mainJarField: TextField
   @FXML private lateinit var mainClassField: TextField
   @FXML private lateinit var classPathListView: ListView<File>
   @FXML private lateinit var modulePathListView: ListView<File>
   @FXML private lateinit var shutdownMenu: Menu


   private var dependenciesController = DependenciesController(configuration,
         userHistory,
         workspace,
         projectService)
   
   //model
   private val jpackageLocations: ObservableList<JDK> = FXCollections.observableArrayList<JDK>()
   private val javafxLocations: ObservableList<NamedDirectory> = FXCollections.observableArrayList<NamedDirectory>()
   
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
      initiializeConfiguredBinaries()
   }

   private fun initializeChildControllers() {
      // load file list UI and insert into it's pane in the application
      val fileListPane = setupChildController("/dependenciesTab.fxml", dependenciesController, dependenciesPane)
   }
   /**
    * JDKs and drop down lists
    */
   private fun initiializeConfiguredBinaries() {
      jpackageLocations.addAll(configuration.jdkEntries.values)
      jpackageComboBox.items = jpackageLocations.sorted()

      javafxLocations.addAll(configuration.javafxModuleEntries.entries.map { NamedDirectory(it.key, it.value) })
      javafxComboBox.items = javafxLocations.sorted()
      javafxComboBox.converter = object : StringConverter<NamedDirectory>() {

         override fun toString(obj: NamedDirectory?): String? {
            return obj?.name
         }

         override fun fromString(name: String): NamedDirectory {
            return javafxComboBox.items.first { it.name == name }
         }
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
      val pairs = configuration.jdkEntries.values.map { NamedDirectory(it.name, it.path) }
      val dialog = BinaryArtefactDialog(applicationStage(), "JPackager JDKs", pairs, userHistory)
      val result = dialog.showAndWait()
      if (result.ok) {
         val jpackageName = result.data?.name
         val jdk = jpackageComboBox.items.find { it.name == jpackageName }
         jpackageComboBox.selectionModel.select(jdk)
         // update jdk entries 
         val updatedModel = dialog.updatedModel()
         if (updatedModel != null) {
            configuration.jdkEntries.clear()
            updatedModel.map { configuration.jdkEntries[it.name] = JDKFactory.create(OperatingSystem.os(), it.name, it.path) }
         }
      }
   }

   @FXML
   fun configureJavaFxModules() {
      val pairs = configuration.javafxModuleEntries.map { NamedDirectory(it.key, it.value) }
      val dialog = BinaryArtefactDialog(applicationStage(), "JavaFX Module Directories", pairs, userHistory)
      val result = dialog.showAndWait()
      if (result.ok) {
         //selected a module and optionally updated list of modules
         val fxModule = result.data?.name
         // update module entries
         val updatedModel = dialog.updatedModel()
         if (updatedModel != null) {
            javafxLocations.clear()
            configuration.javafxModuleEntries.clear()
            updatedModel.map { 
               javafxLocations.add(NamedDirectory(it.name, it.path))
               configuration.javafxModuleEntries[it.name] = it.path
            }
         }
         val mod = javafxComboBox.items.find { it.name == fxModule }
         javafxComboBox.selectionModel.select(mod)
      }
   }

   @FXML
   fun aboutDialog() {
      val dialog = AboutDialog(applicationStage())
      dialog.showAndWait()
   }

   private fun applicationStage(): Stage {
      return mainJarField.scene.window as Stage
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




