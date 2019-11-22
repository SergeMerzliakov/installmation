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
import javafx.scene.control.TextField
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.UserHistory
import org.installmation.model.Workspace
import org.installmation.service.ProjectClosedEvent
import org.installmation.service.ProjectCreatedEvent
import org.installmation.service.ProjectLoadedEvent
import org.installmation.service.ProjectService


class GeneralInfoController(private val configuration: Configuration,
                            private val userHistory: UserHistory,
                            private val workspace: Workspace,
                            private val projectService: ProjectService) {

   companion object {
      val log: Logger = LogManager.getLogger(GeneralInfoController::class.java)
   }

   @FXML private lateinit var projectNameField: TextField
   @FXML private lateinit var applicationVersionField: TextField
   @FXML private lateinit var copyrightField: TextField

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
      projectNameField.text = e.project.name
   }

   @Subscribe
   fun handleProjectLoaded(e: ProjectLoadedEvent) {

   }
   
   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      projectNameField.text = null
      copyrightField.text = null
      applicationVersionField.text = null
   }

}




