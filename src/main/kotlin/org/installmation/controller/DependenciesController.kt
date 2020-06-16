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
import javafx.scene.control.ContextMenu
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.HISTORY_CLASSPATH
import org.installmation.configuration.UserHistory
import org.installmation.service.ProjectBeginSaveEvent
import org.installmation.service.ProjectClosedEvent
import org.installmation.service.ProjectLoadedEvent
import org.installmation.service.Workspace
import org.installmation.ui.dialog.HelpDialog
import org.installmation.ui.dialog.SimpleListItemDeleter
import org.installmation.ui.dialog.openDirectoryDialog
import java.io.File


class DependenciesController(private val configuration: Configuration,
                             private val userHistory: UserHistory,
                             private val workspace: Workspace) {

   companion object {
      val log: Logger = LogManager.getLogger(DependenciesController::class.java)
      const val PROPERTY_HELP_EXTRA_MODULES = "help.extra.modules"
      const val TITLE_HELP_EXTRA_MODULE = "Extra Modules"
   }

   @FXML lateinit var classPathListView: ListView<String>
   @FXML lateinit var classpathListContextMenu: ContextMenu
   @FXML private lateinit var moduleListText: TextField

   // model
   private val classpathItems: ObservableList<String> = FXCollections.observableArrayList<String>()

   init {
      configuration.eventBus.register(this)
   }

   @FXML
   fun initialize() {
      classPathListView.items = classpathItems.sorted()

      initializeContextMenus()
   }

   @FXML
   fun addClasspathItem() {
      val result = openDirectoryDialog(classPathListView.scene.window as Stage, "Add Classpath Item", userHistory.getFile(HISTORY_CLASSPATH))
      if (result.ok) {
         val chosenDir = result.data!!
         userHistory.set(HISTORY_CLASSPATH, chosenDir)
         classpathItems.add(chosenDir.path)
         workspace.save()
         log.debug("Added ${result.data.path} to classpath")
      }
   }

   private fun initializeContextMenus() {
      val removeClassPathItem = MenuItem("Remove")
      removeClassPathItem.onAction = SimpleListItemDeleter(classPathListView, classpathItems)
      classpathListContextMenu.items.add(removeClassPathItem)
   }

   @FXML
   fun updateProject() {
      workspace.save()
   }

   @FXML
   fun helpModules() {
      HelpDialog.showAndWait(TITLE_HELP_EXTRA_MODULE, configuration.resourceBundle.getString(PROPERTY_HELP_EXTRA_MODULES))
   }
   
   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectLoaded(e: ProjectLoadedEvent) {
      checkNotNull(e.project)
      classpathItems.clear()

      for (cp in e.project.classPath)
         classpathItems.add(cp.path)

      moduleListText.text = e.project.customModules.joinToString(",")
   }

   @Subscribe
   fun handleProjectBeginSave(e: ProjectBeginSaveEvent) {
      checkNotNull(e.project)
      // do not clear project path collections - other event handlers will be adding things
      // as well
      for (path in classPathListView.items)
         e.project.classPath.add(File(path))

      workspace.currentProject?.customModules?.clear()
      var modules = moduleListText.text.trim().toLowerCase().split(",")
      modules = modules.map { it.trim() }.filter{it.isNotEmpty()}
      for (module in modules)
         workspace.currentProject?.customModules?.add(module)
   }

   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      classpathItems.clear()
   }
}




