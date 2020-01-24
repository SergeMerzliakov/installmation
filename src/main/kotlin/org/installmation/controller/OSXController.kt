/*
 * Copyright 2020 Serge Merzliakov
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
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.UserHistory
import org.installmation.javafx.FileFieldUtils
import org.installmation.model.InstallProject
import org.installmation.service.*

class OSXController(configuration: Configuration,
                    private val userHistory: UserHistory,
                    private val workspace: Workspace) {

   companion object {
      val log: Logger = LogManager.getLogger(OSXController::class.java)
   }

   @FXML private lateinit var packageIdentifierField: TextField
   @FXML private lateinit var packageNameField: TextField
   @FXML private lateinit var signPrefixField: TextField
   @FXML private lateinit var signKeyUserField: TextField
   @FXML private lateinit var signKeyChainField: TextField
   @FXML private lateinit var signCheckBox: CheckBox

   init {
      configuration.eventBus.register(this)
   }

   @FXML
   fun initialize() {

   }

   @FXML
   fun updateProject() {
      workspace.save()
   }

   @FXML
   fun chooseKeychain() {
//      val result = ChooseFileDialog.showAndWait(logoView.scene.window as Stage, "Choose Application Logo Image", userHistory, InstallmationExtensionFilters.logoImageFilter())
//      if (result.ok) {
//         logoPathField.text = result.data?.path
//         updateLogoPreview(logoPathField.text)
//      }
   }

   @FXML
   fun helpSignKeychain() {
   }

   @FXML
   fun helpPackageIdentifier() {
   }

   @FXML
   fun helpPackageName() {
   }

   @FXML
   fun helpSignPrefix() {
   }

   @FXML
   fun helpSignUser() {
   }

   /**
    * setting updatingFromModel to true prevents UI changes from
    * update model, which will call this method - endless loop.
    * Ideally, we disable change listeners on controls when we run
    * this method, but that is messier.
    */
   private fun updateUIFromProject(project: InstallProject) {
      packageIdentifierField.text = project.packageIdentifier
      packageNameField.text = project.packageName
      signPrefixField.text = project.signPrefix
      signKeyUserField.text = project.signKeyUser
      signKeyChainField.text = project.signKeyChain?.path
      signCheckBox.isSelected = project.signPackage
   }

   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectCreated(e: ProjectCreatedEvent) {
      updateUIFromProject(e.project)
   }

   @Subscribe
   fun handleProjectBeginSave(e: ProjectBeginSaveEvent) {
      e.project.packageIdentifier = packageIdentifierField.text
      e.project.packageName = packageNameField.text
      e.project.signPrefix = signPrefixField.text
      e.project.signKeyUser = signKeyUserField.text
      e.project.signKeyChain = FileFieldUtils.getPath(signKeyChainField)
      e.project.signPackage = signCheckBox.isSelected
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
      packageIdentifierField.text = null
      packageNameField.text = null
      signPrefixField.text = null
      signKeyUserField.text = null
      signKeyChainField.text = null
      signCheckBox.isSelected = false
   }

}




