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
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.HISTORY_KEYCHAIN
import org.installmation.configuration.UserHistory
import org.installmation.javafx.EventUtils
import org.installmation.javafx.FileFieldUtils
import org.installmation.model.InstallProject
import org.installmation.service.*
import org.installmation.ui.dialog.HelpDialog
import org.installmation.ui.dialog.InstallmationExtensionFilters
import org.installmation.ui.dialog.openFileDialog

private val log: Logger = LogManager.getLogger(OSXController::class.java)

private const val PROPERTY_HELP_APPLE_KEYCHAIN = "help.apple.keychain"
private const val PROPERTY_HELP_APPLE_INSTALL_CERT = "help.apple.cert.installer"
private const val INSTALLER_CERT_PREFIX = "Developer ID Installer: "
private const val APPLICATION_CERT_PREFIX = "Developer ID Application: "
const val TITLE_HELP_SIGN_INSTALL_CERT = "Apple Installer Certificate"
const val TITLE_HELP_SIGN_KEYCHAIN = "Keychain With Apple Certificate"

/**
 * OSX only UI
 */
class OSXController(private val configuration: Configuration,
                    private val userHistory: UserHistory,
                    private val workspace: Workspace) {

   @FXML private lateinit var signKeyUserField: TextField
   @FXML private lateinit var signKeyChainField: TextField
   @FXML private lateinit var signCheckBox: CheckBox

   init {
      configuration.eventBus.register(this)
   }

   @FXML
   fun initialize() {
      EventUtils.focusLostHandler(signKeyUserField){field -> 
         if (field.text.startsWith(INSTALLER_CERT_PREFIX)){
            // strip it off - jpackager fails if its present
            log.info("stripping off Apple Developer Certificate Name Prefix '$INSTALLER_CERT_PREFIX'. JPackager will reject it otherwise.")
            field.text = field.text.replace(INSTALLER_CERT_PREFIX, "", true)
         }
      }
   }

   @FXML
   fun updateProject() {
      workspace.save()
   }

   @FXML
   fun chooseKeychain() {
      val result = openFileDialog(signKeyChainField.scene.window as Stage, "Choose Application Logo Image", userHistory.getFile(HISTORY_KEYCHAIN), InstallmationExtensionFilters.appleKeyChainFilter())
      if (result.ok) {
         userHistory.set(HISTORY_KEYCHAIN, result.data!!.parentFile)
         signKeyChainField.text = result.data.path
      }
   }

   @FXML
   fun helpSignKeychain() {
      HelpDialog.showAndWait(TITLE_HELP_SIGN_KEYCHAIN, configuration.resourceBundle.getString(PROPERTY_HELP_APPLE_KEYCHAIN))
   }

   @FXML
   fun helpSignUser() {
      HelpDialog.showAndWait(TITLE_HELP_SIGN_INSTALL_CERT, configuration.resourceBundle.getString(PROPERTY_HELP_APPLE_INSTALL_CERT))
   }

   /**
    * setting updatingFromModel to true prevents UI changes from
    * update model, which will call this method - endless loop.
    * Ideally, we disable change listeners on controls when we run
    * this method, but that is messier.
    */
   private fun updateUIFromProject(project: InstallProject) {
      signKeyUserField.text = project.appleInstallerCertName
      signKeyChainField.text = project.appleInstallerKeyChain?.path
      signCheckBox.isSelected = project.signInstaller
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
      e.project.appleInstallerCertName = signKeyUserField.text
      e.project.appleInstallerKeyChain = FileFieldUtils.getPath(signKeyChainField)
      e.project.signInstaller = signCheckBox.isSelected
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
      signKeyUserField.text = null
      signKeyChainField.text = null
      signCheckBox.isSelected = false
   }

}




