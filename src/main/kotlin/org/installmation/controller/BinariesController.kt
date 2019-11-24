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
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.stage.Stage
import javafx.util.StringConverter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.UserHistory
import org.installmation.javafx.ComboUtils
import org.installmation.model.JDKListUpdatedEvent
import org.installmation.model.JFXModuleUpdatedEvent
import org.installmation.model.NamedDirectory
import org.installmation.model.Workspace
import org.installmation.model.binary.JDK
import org.installmation.model.binary.JDKFactory
import org.installmation.model.binary.OperatingSystem
import org.installmation.service.ProjectBeginSaveEvent
import org.installmation.service.ProjectClosedEvent
import org.installmation.service.ProjectLoadedEvent
import org.installmation.service.ProjectService
import org.installmation.ui.dialog.BinaryArtefactDialog


class BinariesController(private val configuration: Configuration,
                         private val userHistory: UserHistory,
                         private val workspace: Workspace,
                         private val projectService: ProjectService) {

   companion object {
      val log: Logger = LogManager.getLogger(BinariesController::class.java)
   }

   @FXML private lateinit var jpackageComboBox: ComboBox<JDK>
   @FXML private lateinit var configureJPackageButton: Button
   @FXML private lateinit var javafxComboBox: ComboBox<NamedDirectory>
   @FXML private lateinit var installJDKComboBox: ComboBox<JDK>

   // model loaded from configuration
   private val jpackageJDKItems: ObservableList<JDK> = FXCollections.observableArrayList<JDK>()
   private val installJDKItems: ObservableList<JDK> = FXCollections.observableArrayList<JDK>()
   private val javafxItems: ObservableList<NamedDirectory> = FXCollections.observableArrayList<NamedDirectory>()

   init {
      configuration.eventBus.register(this)
   }

   @FXML
   fun initialize() {
      initializeConfiguredBinaries()
   }

   /**
    * JDKs and drop down lists
    */
   private fun initializeConfiguredBinaries() {
      jpackageJDKItems.addAll(configuration.jdkEntries.values)
      jpackageComboBox.items = jpackageJDKItems.sorted()

      installJDKItems.addAll(configuration.jdkEntries.values)
      installJDKComboBox.items = installJDKItems.sorted()

      javafxItems.addAll(configuration.javafxModuleEntries.entries.map { NamedDirectory(it.key, it.value) })
      javafxComboBox.items = javafxItems.sorted()
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
   fun configureJPackageBinaries() {
      updateJDKList(jpackageComboBox)
   }

   @FXML
   fun configureInstallJDK() {
      updateJDKList(installJDKComboBox)
   }
   
   @FXML
   fun configureJavaFxModules() {
      val dialog = javaFXFDialog()
      val result = dialog.showAndWait()
      if (result.ok) {
         ComboUtils.comboSelect(javafxComboBox, result.data?.name)
         // update model
         val updatedModel = dialog.updatedModel()
         if (updatedModel != null) {
            configuration.eventBus.post(JFXModuleUpdatedEvent(updatedModel))
         }
      }
   }

   /**
    * Update list of known JDKs. Used for both JPackager access, and
    * setting the JDK to add to installer.
    */
   private fun updateJDKList(combo: ComboBox<JDK>){
      val dialog = jdkDialog()
      val result = dialog.showAndWait()
      if (result.ok) {
         ComboUtils.comboSelect(combo, result.data?.name)

         val updatedModel = dialog.updatedModel()
         if (updatedModel != null) {
            configuration.eventBus.post(JDKListUpdatedEvent(updatedModel))
         }
      }
   }
   
   private fun jdkDialog(): BinaryArtefactDialog {
      val items = jpackageJDKItems.map { NamedDirectory(it.name, it.path) }
      return BinaryArtefactDialog(applicationStage(), "JPackager JDKs", items, userHistory)
   }

   private fun javaFXFDialog(): BinaryArtefactDialog {
      val items = javafxItems.map { NamedDirectory(it.name, it.path) }
      return BinaryArtefactDialog(applicationStage(), "JavaFX Module Directories", items, userHistory)
   }

   private fun applicationStage(): Stage {
      return configureJPackageButton.scene.window as Stage
   }

   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectBeginSave(e: ProjectBeginSaveEvent) {
      checkNotNull(e.project)
      e.project.installJDK = installJDKComboBox.selectionModel.selectedItem
      e.project.jpackageJDK = jpackageComboBox.selectionModel.selectedItem
      e.project.modulePath = javafxComboBox.selectionModel.selectedItem
   }


   @Subscribe
   fun handleProjectLoaded(e: ProjectLoadedEvent) {
      checkNotNull(e.project)
      jpackageComboBox.selectionModel.select(e.project.jpackageJDK)
      installJDKComboBox.selectionModel.select(e.project.installJDK)
      javafxComboBox.selectionModel.select(e.project.modulePath)
   }
   
   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      jpackageComboBox.selectionModel.clearSelection()
      installJDKComboBox.selectionModel.clearSelection()
      javafxComboBox.selectionModel.clearSelection()
   }

   /**
    * Clear out and refresh list of JDKs from this controller's model
    * and configuration
    */
   @Subscribe
   fun handleJDKUpdated(e: JDKListUpdatedEvent) {
      jpackageJDKItems.clear()
      installJDKItems.clear()
      configuration.jdkEntries.clear()
      e.updated.map {
         val jdk = JDKFactory.create(OperatingSystem.os(), it.name, it.path)
         jpackageJDKItems.add(jdk)
         installJDKItems.add(jdk)
         configuration.jdkEntries[it.name] = jdk
      }
   }

   /**
    * Clear out and refresh list of FX modules from this controller's model
    * and configuration
    */
   @Subscribe
   fun handleJFXModuleUpdated(e: JFXModuleUpdatedEvent) {
      javafxItems.clear()
      configuration.javafxModuleEntries.clear()
      e.updated.map {
         val nd = NamedDirectory(it.name, it.path)
         javafxItems.add(nd)
         configuration.javafxModuleEntries[it.name] = it.path
      }
   }
}




