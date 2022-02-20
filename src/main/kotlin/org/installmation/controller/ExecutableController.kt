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
import javafx.stage.Stage
import org.installmation.configuration.Configuration
import org.installmation.configuration.HISTORY_MAIN_JAR
import org.installmation.configuration.UserHistory
import org.installmation.javafx.getPath
import org.installmation.service.*
import org.installmation.ui.dialog.InstallmationExtensionFilters
import org.installmation.ui.dialog.openFileDialog

class ExecutableController(configuration: Configuration,
                           private val userHistory: UserHistory,
                           private val workspace: Workspace) {

   @FXML private lateinit var mainJarField: TextField
   @FXML private lateinit var jvmArgumentsField: TextField
   @FXML private lateinit var mainClassField: TextField

   init {
      configuration.eventBus.register(this)
   }

   @FXML
   fun initialize() {
   }

   @FXML
   fun chooseMainJar() {
      val result = openFileDialog(mainJarField.scene.window as Stage, "Select Main Application Jar File", userHistory.getFile(HISTORY_MAIN_JAR), InstallmationExtensionFilters.jarFilter())
      if (result.ok) {
         userHistory.set(HISTORY_MAIN_JAR, result.data!!.parentFile)
         mainJarField.text = result.data.path
      }
   }

   @FXML
   fun updateProject() {
      workspace.save()
   }

   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectCreated(e: ProjectCreatedEvent) {
   }
   
   @Subscribe
   fun handleProjectBeginSave(e: ProjectBeginSaveEvent) {
      checkNotNull(e.project)

      e.project.mainJar = getPath(mainJarField)
      e.project.mainClass = mainClassField.text
      e.project.jvmArguments = jvmArgumentsField.text
   }

   @Subscribe
   fun handleProjectLoaded(e: ProjectLoadedEvent) {
      checkNotNull(e.project)
      mainJarField.text = e.project.mainJar?.path
      jvmArgumentsField.text = e.project.jvmArguments
      mainClassField.text = e.project.mainClass
   }

   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      mainJarField.text = null
      mainClassField.text = null
      jvmArgumentsField.text = null
   }
}




