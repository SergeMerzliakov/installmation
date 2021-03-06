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
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.*
import org.installmation.javafx.StageUtils
import org.installmation.service.ProjectBeginSaveEvent
import org.installmation.service.ProjectClosedEvent
import org.installmation.service.ProjectLoadedEvent
import org.installmation.service.Workspace
import org.installmation.ui.dialog.HelpDialog
import org.installmation.ui.dialog.openDirectoryDialog
import java.io.File

private const val PROPERTY_HELP_INPUT_DIR = "help.input.directory"
private const val PROPERTY_HELP_IMAGE_BUILD_DIR = "help.image.build.directory"
private const val PROPERTY_HELP_INSTALLER_DIR = "help.installer.directory"

const val TITLE_HELP_INPUT_DIR = "Input Directory"
const val TITLE_HELP_IMAGE_BUILD_DIR = "Image Build Directory"
const val TITLE_HELP_INSTALLER_DIR = "Installer Directory"


class LocationController(private val configuration: Configuration,
                         private val userHistory: UserHistory,
                         private val workspace: Workspace) {

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
      val result = openDirectoryDialog(StageUtils.primaryStage(), "Select an Input Directory", userHistory.getFile(HISTORY_INPUT))
      if (result.ok) {
         userHistory.set(HISTORY_INPUT, result.data!!)
         inputDirectoryText.text = result.data.path
      }
   }

   @FXML
   fun chooseImageBuildDirectory() {
      val result = openDirectoryDialog(StageUtils.primaryStage(), "Select an Image Build Directory", userHistory.getFile(HISTORY_IMAGE))
      if (result.ok) {
         userHistory.set(HISTORY_IMAGE, result.data!!)
         imageBuildDirectoryText.text = result.data.path
      }
   }

   @FXML
   fun chooseInstallerDirectory() {
      val result = openDirectoryDialog(StageUtils.primaryStage(), "Select an Installer Directory", userHistory.getFile(HISTORY_INSTALLER))
      if (result.ok) {
         userHistory.set(HISTORY_INSTALLER, result.data!!)
         installerDirectoryText.text = result.data.path
      }
   }

   @FXML
   fun helpInputDirectory() {
      HelpDialog.showAndWait(TITLE_HELP_INPUT_DIR, configuration.resourceBundle.getString(PROPERTY_HELP_INPUT_DIR))
   }

   @FXML
   fun helpImageBuildDirectory() {
      HelpDialog.showAndWait(TITLE_HELP_IMAGE_BUILD_DIR, configuration.resourceBundle.getString(PROPERTY_HELP_IMAGE_BUILD_DIR))
   }

   @FXML
   fun helpInstallerDirectory() {
      HelpDialog.showAndWait(TITLE_HELP_INSTALLER_DIR, configuration.resourceBundle.getString(PROPERTY_HELP_INSTALLER_DIR))
   }

   @FXML
   fun updateProject() {
      workspace.save()
   }

   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectLoaded(e: ProjectLoadedEvent) {
      checkNotNull(e.project)
      inputDirectoryText.text = e.project.inputDirectory?.path
      imageBuildDirectoryText.text = e.project.imageBuildDirectory?.path
      installerDirectoryText.text = e.project.installerDirectory?.path
   }

   @Subscribe
   fun handleProjectBeginSave(e: ProjectBeginSaveEvent) {
      checkNotNull(e.project)

      if (!inputDirectoryText.text.isNullOrEmpty())
         e.project.inputDirectory = File(inputDirectoryText.text)

      if (!imageBuildDirectoryText.text.isNullOrEmpty())
         e.project.imageBuildDirectory = File(imageBuildDirectoryText.text)

      if (!installerDirectoryText.text.isNullOrEmpty())
         e.project.installerDirectory = File(installerDirectoryText.text)
   }

   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      inputDirectoryText.text = null
      installerDirectoryText.text = null
      imageBuildDirectoryText.text = null
   }
}




