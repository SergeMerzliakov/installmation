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
import javafx.scene.image.ImageView
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.HISTORY_LOGO
import org.installmation.configuration.UserHistory
import org.installmation.core.ApplicationStartCompleteEvent
import org.installmation.core.OperatingSystem
import org.installmation.image.ImageTool
import org.installmation.javafx.EventUtils
import org.installmation.javafx.FileFieldUtils
import org.installmation.model.InstallProject
import org.installmation.service.*
import org.installmation.ui.dialog.ErrorDialog
import org.installmation.ui.dialog.InstallmationExtensionFilters
import org.installmation.ui.dialog.openFileDialog
import java.io.File

class GeneralInfoController(configuration: Configuration,
                            private val userHistory: UserHistory,
                            private val workspace: Workspace) {

   companion object {
      val log: Logger = LogManager.getLogger(GeneralInfoController::class.java)
   }

   // workaround/hack because we cannot easily disable change listeners when
   // updating UI from model
   private var updatingFromModel = false
   
   @FXML private lateinit var projectNameField: TextField
   @FXML private lateinit var applicationVersionField: TextField
   @FXML private lateinit var copyrightField: TextField
   @FXML private lateinit var vendorField: TextField
   @FXML private lateinit var installerTypeCombo: ComboBox<String>
   //path to simple image file like png or jpeg, or os specific like ico or icns file
   @FXML private lateinit var logoPathField: TextField
   @FXML private lateinit var logoView: ImageView

   init {
      configuration.eventBus.register(this)
   }

   @FXML
   fun initialize() {
      installerTypeCombo.items = FXCollections.observableList(OperatingSystem.installerType())

      // project name is special - we need to update model as soon as user is finished with it
      // so that project saves get the latest value
      EventUtils.focusLostHandler(projectNameField) { 
         workspace.currentProject?.name = projectNameField.text
      }
   }

   @FXML
   fun updateProject() {
      if (!updatingFromModel) {
         workspace.save()
         updateLogoPreview(logoPathField.text)
      }
   }

   @FXML
   fun chooseLogo() {
      val result = openFileDialog(logoView.scene.window as Stage, "Choose Application Logo Image", userHistory.getFile(HISTORY_LOGO), InstallmationExtensionFilters.logoImageFilter())
      if (result.ok) {
         userHistory.set(HISTORY_LOGO, result.data!!.parentFile)
         logoPathField.text = result.data.path
         updateLogoPreview(logoPathField.text)
      }
   }

   // show logo chosen on screen
   private fun updateLogoPreview(path: String?) {
      try {
         if (path.isNullOrEmpty()) {
            logoView.image = null
            return
         }
         val imagePath = File(path.trim())
         if (ImageTool.isValidImageFile(imagePath))
            logoView.image = ImageTool.createImage(imagePath)
      } catch (e: Exception) {
         log.error("Error Loading Application Logo", e)
         ErrorDialog.showAndWait("Error Loading Application Logo", e.toString())
      }
   }

   /**
    * setting updatingFromModel to true prevents UI changes from
    * update model, which will call this method - endless loop.
    * Ideally, we disable change listeners on controls when we run
    * this method, but that is messier.
    */
   private fun updateUIFromProject(proj: InstallProject) {
      updatingFromModel = true
      projectNameField.text = proj.name
      applicationVersionField.text = proj.version
      copyrightField.text = proj.copyright
      vendorField.text = proj.vendor
      logoPathField.text = proj.applicationLogo?.path
      updateLogoPreview(proj.applicationLogo?.path)
      installerTypeCombo.selectionModel.select(proj.installerType)
      updatingFromModel = false
   }

   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleApplicationStartCompleteEvent(e: ApplicationStartCompleteEvent) {
      EventUtils.selectionChangedHandler(installerTypeCombo) { updateProject() }
   }

   @Subscribe
   fun handleProjectCreated(e: ProjectCreatedEvent) {
      projectNameField.text = e.project.name
      installerTypeCombo.selectionModel.select(0)
   }

   @Subscribe
   fun handleProjectBeginSave(e: ProjectBeginSaveEvent) {
      e.project.name = projectNameField.text
      e.project.version = applicationVersionField.text ?: "1.0"
      e.project.copyright = copyrightField.text
      e.project.installerType = installerTypeCombo.selectionModel.selectedItem
      e.project.applicationLogo = FileFieldUtils.getPath(logoPathField)
      e.project.vendor = vendorField.text
   }

   @Subscribe
   fun handleProjectUpdated(e: ProjectUpdatedEvent) {
      updateUIFromProject(e.project)
   }

   @Subscribe
   fun handleProjectLoaded(e: ProjectLoadedEvent) {
      updateUIFromProject(e.project)
   }

   @Subscribe
   fun handleProjectClosed(e: ProjectClosedEvent) {
      projectNameField.text = null
      copyrightField.text = null
      applicationVersionField.text = null
      logoPathField.text = null
      logoView.image = null
      vendorField.text = null
      //do not clear installer type for now
   }

}




