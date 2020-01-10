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

import org.installmation.configuration.Configuration
import org.installmation.javafx.StageUtils
import org.installmation.model.InstallProject
import org.installmation.service.ProjectUpdatedEvent
import org.installmation.ui.dialog.HelpDialog
import org.installmation.ui.dialog.SingleValueDialog

/**
 * Cross Controller validator which may require user input via dialog - so does
 * not belong inside a service or model class
 */
object Validator {

   /**
    * Cannot save a project without a name, so ask user if no name exists
    * and return true only if we have a valid name
    */
   fun ensureProjectName(proj: InstallProject, configuration: Configuration): Boolean {
      if (!proj.hasValidName()) {
         val projectName = getStringValue("Project Name", "Project Name", "myproject")
         if (projectName != null) {
            proj.name = projectName
            configuration.eventBus.post(ProjectUpdatedEvent(proj)) // update UI
            return true
         } else {
            HelpDialog.showAndWait("Project Save Cancelled", "No project name selected. Cannot save a project without a valid name")
            return false
         }
      }
      return true
   }

   /**
    * from user via dialog
    */
   private fun getStringValue(dialogTitle: String, fieldName: String, defaultValue: String): String? {
      val d = SingleValueDialog(StageUtils.primaryStage(), dialogTitle, fieldName, defaultValue)
      val result = d.showAndWait()
      if (result.ok)
         return result.data
      return null
   }
}