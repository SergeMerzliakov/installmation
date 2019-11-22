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
import javafx.scene.control.Tooltip
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.UserHistory
import org.installmation.model.Workspace
import org.installmation.service.ProjectClosedEvent
import org.installmation.service.ProjectLoadedEvent
import org.installmation.service.ProjectService
import org.installmation.ui.dialog.ChooseDirectoryDialog
import org.installmation.ui.dialog.HelpDialog


class LocationController(private val configuration: Configuration,
                         private val userHistory: UserHistory,
                         private val workspace: Workspace,
                         private val projectService: ProjectService) {

   companion object {
      val log: Logger = LogManager.getLogger(LocationController::class.java)
      const val PROPERTY_HELP_INPUT_DIR = "help.input.directory"
      const val PROPERTY_HELP_IMAGE_BUILD_DIR = "help.image.build.directory"
      const val PROPERTY_HELP_INSTALLER_DIR = "help.installer.directory"
   }

   @FXML lateinit var inputDirectoryText: TextField
   @FXML lateinit var installerDirectoryText: TextField
   @FXML lateinit var imageBuildDirectoryText: TextField

   @FXML lateinit var imgBuildDirTooltip1: Tooltip
   @FXML lateinit var imgBuildDirTooltip2: Tooltip
   @FXML lateinit var inputDirTooltip1: Tooltip
   @FXML lateinit var inputDirTooltip2: Tooltip
   @FXML lateinit var installerDirTooltip1: Tooltip
   @FXML lateinit var installerDirTooltip2: Tooltip

   init {
      configuration.eventBus.register(this)
   }

   @FXML
   fun initialize() {
      initializeTooltips()
   }

   private fun initializeTooltips() {
      inputDirTooltip1.text = configuration.resourceBundle.getString(PROPERTY_HELP_INPUT_DIR)
      inputDirTooltip2.text = inputDirTooltip1.text
      imgBuildDirTooltip1.text = configuration.resourceBundle.getString(PROPERTY_HELP_IMAGE_BUILD_DIR)
      imgBuildDirTooltip2.text = imgBuildDirTooltip1.text
      installerDirTooltip1.text = configuration.resourceBundle.getString(PROPERTY_HELP_INSTALLER_DIR)
      installerDirTooltip2.text = installerDirTooltip1.text
   }

   @FXML
   fun chooseInputDirectory() {
      val result = ChooseDirectoryDialog.showAndWait(inputDirectoryText.scene.window as Stage, "Select an Input Directory", userHistory)
      if (result.ok) {
         inputDirectoryText.text = result.data!!.path
      }
   }

   @FXML
   fun chooseImageBuildDirectory() {
      val result = ChooseDirectoryDialog.showAndWait(inputDirectoryText.scene.window as Stage, "Select an Image Build Directory", userHistory)
      if (result.ok) {
         imageBuildDirectoryText.text = result.data!!.path
      }
   }

   @FXML
   fun chooseInstallerDirectory() {
      val result = ChooseDirectoryDialog.showAndWait(inputDirectoryText.scene.window as Stage, "Select an Installer Directory", userHistory)
      if (result.ok) {
         installerDirectoryText.text = result.data!!.path
      }
   }

   @FXML
   fun helpInputDirectory() {
      HelpDialog.showAndWait("Input Directory", configuration.resourceBundle.getString(PROPERTY_HELP_INPUT_DIR))
   }

   @FXML
   fun helpImageBuildDirectory() {
      HelpDialog.showAndWait("Image Build Directory", configuration.resourceBundle.getString(PROPERTY_HELP_IMAGE_BUILD_DIR))
   }

   @FXML
   fun helpInstallerDirectory() {
      HelpDialog.showAndWait("Installer Directory", configuration.resourceBundle.getString(PROPERTY_HELP_INSTALLER_DIR))
   }
   
   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectLoaded(e: ProjectLoadedEvent) {

   }
   
   
   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      inputDirectoryText.text = null
      installerDirectoryText.text = null
      imageBuildDirectoryText.text = null
   }
}




