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
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ListView
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
import org.installmation.core.*
import org.installmation.model.JDKListUpdatedEvent
import org.installmation.model.ModuleJmodUpdatedEvent
import org.installmation.model.ModuleLibUpdatedEvent
import org.installmation.model.NamedDirectory
import org.installmation.service.*
import org.installmation.ui.dialog.*
import java.io.File

/**
 * Main Application controller, with nested controllers
 */
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

    @FXML
    private lateinit var applicationMenuBar: MenuBar
    @FXML
    private lateinit var dependenciesPane: AnchorPane
    @FXML
    private lateinit var locationPane: AnchorPane
    @FXML
    private lateinit var binariesPane: AnchorPane
    @FXML
    private lateinit var generalInfoPane: AnchorPane
    @FXML
    private lateinit var executablePane: AnchorPane
    @FXML
    private lateinit var shutdownMenu: Menu
    @FXML
    private lateinit var messageListView: ListView<String>

    @FXML
    private lateinit var generateScriptTooltip: Tooltip
    @FXML
    private lateinit var generateImageTooltip: Tooltip
    @FXML
    private lateinit var generateInstallerTooltip: Tooltip

    private var dependenciesController = DependenciesController(configuration, userHistory, workspace)

    private var locationController = LocationController(configuration,
            userHistory,
            workspace)

    private var binariesController = BinariesController(configuration, userHistory, workspace)

    private var generalInfoController = GeneralInfoController(configuration, workspace)

    private var executeController = ExecutableController(configuration, userHistory, workspace)

    // models
    private val userMessages: ObservableList<String> = FXCollections.observableArrayList<String>()

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
        messageListView.items = userMessages
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
        configuration.save()
        workspace.save()
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
        val result = ChooseFileDialog.showAndWait(applicationStage(), "Open Project", userHistory, InstallmationExtensionFilters.projectFilter())

        if (result.ok) {
            val p = projectService.load(result.data!!.nameWithoutExtension)
            workspace.setCurrentProject(p)
        }
    }

    @FXML
    fun closeProject() {
        projectService.close(workspace.currentProject)
    }

    @FXML
    fun saveProject() {
        workspace.saveProject()
        workspace.writeToFile()
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
        val result = projectService.generateImage(workspace.currentProject!!)
        if (result.successful)
            HelpDialog.showAndWait("Image Created", "Image created at ${workspace.currentProject!!.imageBuildDirectory}")
        else {
            val d = ItemListDialog("Image Generation Errors", "Issues", result.errors)
            d.showNonModal()
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
        val result = projectService.generateInstaller(workspace.currentProject!!)

        if (result.successful)
            HelpDialog.showAndWait("Installer Created", "Installer created at ${workspace.currentProject!!.installerDirectory}")
        else {
            val d = ItemListDialog("Installer Generation Errors", "Issues", result.errors)
            d.showNonModal()
        }
    }

    /**
     * Generate scripts of creating images and installers for
     * Mac/Linux and Windows. Returns the scripts generated
     */
    @FXML
    fun generateScripts() {
        if (workspace.currentProject == null) {
            HelpDialog.showAndWait("Cannot Generate Scripts", "No project selected, created or loaded. Cannot generate installer scripts.")
            return
        }
        try {
            val scripts = projectService.generateScripts(workspace.currentProject!!)
            if (scripts.result.successful) {
                val result = ChooseDirectoryDialog.showAndWait(applicationStage(), "Script Destination", userHistory)
                if (result.ok) {
                    val imagePath = File(result.data, scripts.imageScript?.fileName)
                    imagePath.parentFile.mkdirs()
                    imagePath.writeText(scripts.imageScript.toString())

                    val installerPath = File(result.data, scripts.installerScript?.fileName)
                    installerPath.parentFile.mkdirs()
                    installerPath.writeText(scripts.installerScript.toString())
                    HelpDialog.showAndWait("Scripts Created", "All scripts created at ${result.data?.path}")
                }
            } else {
                val d = ItemListDialog("Script Generation Errors", "Issues", scripts.result.errors)
                d.showNonModal()
            }
        } catch (e: Exception) {
            log.info("Generate Scripts  - Failed with error: ${e.message}", e)
            ErrorDialog.showAndWait("Scripts Error", e.toString())
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
                configuration.save()
            }
        }
    }

    @FXML
    fun showAllJavaFXJars() {
        val dialog = javaFXJarDialog()
        val result = dialog.showAndWait()
        if (result.ok) {
            // update model
            val updatedModel = dialog.updatedModel()
            if (updatedModel != null) {
                configuration.eventBus.post(ModuleLibUpdatedEvent(updatedModel))
                configuration.save()
            }
        }
    }


    @FXML
    fun showAllJavaFXJmods() {
        val dialog = javaFXJmodDialog()
        val result = dialog.showAndWait()
        if (result.ok) {
            // update model
            val updatedModel = dialog.updatedModel()
            if (updatedModel != null) {
                configuration.eventBus.post(ModuleJmodUpdatedEvent(updatedModel))
                configuration.save()
            }
        }
    }

    /*
    Show Jdeps tool dialog - for generic dependency generation
    */
    @FXML
    fun jdepsDialog() {
        // combined JFX mods with other mods
        val p = workspace.currentProject
        val d: JdepsDialog
        if (p != null) {
            d = JdepsDialog(applicationStage(), configuration.jdkEntries.values, p.javaFXLib?.path, p.mainJar, p.classPath, userHistory)
        } else
            d = JdepsDialog(applicationStage(), configuration.jdkEntries.values, null, null, null, userHistory)
        d.showAndWait()
    }

    private fun jdkDialog(): BinaryArtefactDialog {
        val items = configuration.jdkEntries.values.map { NamedDirectory(it.name, it.path) }
        return BinaryArtefactDialog(applicationStage(), "JPackager JDKs", items, userHistory)
    }

    private fun javaFXJmodDialog(): BinaryArtefactDialog {
        val items = configuration.javafxModuleEntries.values.map { NamedDirectory(it.name, it) }
        return BinaryArtefactDialog(applicationStage(), "JavaFX Module Directories", items, userHistory)
    }

    private fun javaFXJarDialog(): BinaryArtefactDialog {
        val items = configuration.javafxLibEntries.values.map { NamedDirectory(it.name, it) }
        return BinaryArtefactDialog(applicationStage(), "JavaFX Library Directories", items, userHistory)
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

    @Subscribe
    fun handleRunningAsTest(e: RunningAsTestEvent) {
        // only for testing, as TestFX cannot cope with System Menu Bar, as of version 4.0.16-alpha
        applicationMenuBar.isUseSystemMenuBar = false
    }

    @Subscribe
    fun handleUserMessage(e: UserMessageEvent) {
        if (e.isError)
            userMessages.add("${DateUtils.now()} - ERROR - ${e.message}")
        else
            userMessages.add("${DateUtils.now()} - OK - ${e.message}")
    }

    @Subscribe
    fun handleClearMessage(e: ClearMessagesEvent) {
        userMessages.clear()
    }
}




