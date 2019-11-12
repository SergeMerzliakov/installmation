package org.installmation.controller

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.UserHistory
import org.installmation.model.Workspace
import org.installmation.service.ProjectService
import org.installmation.ui.dialog.SimpleListItemDeleter


class DependenciesController(private val configuration: Configuration,
                             private val userHistory: UserHistory,
                             private val workspace: Workspace,
                             private val projectService: ProjectService) {

   companion object {
      val log: Logger = LogManager.getLogger(DependenciesController::class.java)
   }

   @FXML lateinit var classPathListView: ListView<String>
   @FXML lateinit var moduleListView: ListView<String>
   @FXML lateinit var classpathListContextMenu: ContextMenu
   @FXML lateinit var moduleListContextMenu: ContextMenu

   // model
   private val classpathItems: ObservableList<String> = FXCollections.observableArrayList<String>()
   private val moduleItems: ObservableList<String> = FXCollections.observableArrayList<String>()

   init {
      configuration.eventBus.register(this)
   }

   @FXML
   fun initialize() {
      classPathListView.items = classpathItems.sorted()
      moduleListView.items = moduleItems.sorted()

      initializeContextMenus()
   }

   private fun initializeContextMenus() {
      val removeClassPathItem = MenuItem("Remove")
      removeClassPathItem.onAction = SimpleListItemDeleter(classPathListView, classpathItems)
      classpathListContextMenu.items.add(removeClassPathItem)

      val removeModuleItem = MenuItem("Remove")
      removeModuleItem.onAction = SimpleListItemDeleter(moduleListView, moduleItems)
      moduleListContextMenu.items.add(removeModuleItem)
   }

   @FXML
   fun addClasspathItem() {
      val chooser = DirectoryChooser()
      chooser.title = "Add Classpath Item"
      chooser.initialDirectory = userHistory.lastPath
      val chosen = chooser.showDialog(moduleListView.scene.window as Stage)
      if (chosen != null) {
         userHistory.lastPath = chosen.parentFile
         classpathItems.add(chosen.path)
         log.debug("Added ${chosen.path} to classpath")
      }
   }

   @FXML
   fun addModuleItem() {
      val chooser = DirectoryChooser()
      chooser.title = "Add Module Item"
      chooser.initialDirectory = userHistory.lastPath
      val chosen = chooser.showDialog(moduleListView.scene.window as Stage)
      if (chosen != null) {
         userHistory.lastPath = chosen.parentFile
         moduleItems.add(chosen.path)
         log.debug("Added ${chosen.path} to modules")
      }
   }

   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

}




