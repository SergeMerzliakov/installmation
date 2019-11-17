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
import javafx.scene.control.TextField
import javafx.stage.Stage
import javafx.util.StringConverter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.UserHistory
import org.installmation.javafx.ComboUtils
import org.installmation.model.JDKUpdatedEvent
import org.installmation.model.JFXModuleUpdatedEvent
import org.installmation.model.NamedDirectory
import org.installmation.model.Workspace
import org.installmation.model.binary.JDK
import org.installmation.model.binary.JDKFactory
import org.installmation.model.binary.OperatingSystem
import org.installmation.service.ProjectClosedEvent
import org.installmation.service.ProjectCreatedEvent
import org.installmation.service.ProjectService
import org.installmation.ui.dialog.BinaryArtefactDialog


class ExecutableController(private val configuration: Configuration,
                           private val userHistory: UserHistory,
                           private val workspace: Workspace,
                           private val projectService: ProjectService) {

   companion object {
      val log: Logger = LogManager.getLogger(ExecutableController::class.java)
   }

   @FXML private lateinit var mainJarField: TextField
   @FXML private lateinit var mainClassField: TextField

   
   init {
      configuration.eventBus.register(this)
   }

   @FXML
   fun initialize() {
   }

   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectCreated(e: ProjectCreatedEvent) {
   }

   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      mainJarField.text = null
      mainClassField.text = null
   }

}




