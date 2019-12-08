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
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.core.OperatingSystem
import org.installmation.service.*


class GeneralInfoController(configuration: Configuration, private val workspace: Workspace) {

   companion object {
      val log: Logger = LogManager.getLogger(GeneralInfoController::class.java)
   }

   @FXML private lateinit var projectNameField: TextField
   @FXML private lateinit var applicationVersionField: TextField
   @FXML private lateinit var copyrightField: TextField
   @FXML private lateinit var installerTypeCombo: ComboBox<String>

   init {
      configuration.eventBus.register(this)
   }

   @FXML
   fun initialize() {
      installerTypeCombo.items = FXCollections.observableList(OperatingSystem.installerType())
   }

   @FXML
   fun updateProject() {
      workspace.saveProject()
   }
   
   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectCreated(e: ProjectCreatedEvent) {
      projectNameField.text = e.project.name
      installerTypeCombo.selectionModel.select(0)
   }

   @Subscribe
   fun handleProjectBeginSave(e: ProjectBeginSaveEvent) {
      checkNotNull(e.project)
      e.project.name = projectNameField.text
      e.project.version = applicationVersionField.text ?: "1.0-SNAPSHOT"
      e.project.copyright = copyrightField.text
      e.project.installerType = installerTypeCombo.selectionModel.selectedItem
   }

   @Subscribe
   fun handleProjectLoaded(e: ProjectLoadedEvent) {
      checkNotNull(e.project)
      projectNameField.text = e.project.name
      applicationVersionField.text = e.project.version
      copyrightField.text = e.project.copyright
      installerTypeCombo.selectionModel.select(e.project.installerType)
   }
   
   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      projectNameField.text = null
      copyrightField.text = null
      applicationVersionField.text = null
      //do not clear installer type for now
   }
}




