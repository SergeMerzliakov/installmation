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

package org.installmation.service

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.Constant
import org.installmation.io.ApplicationJsonReader
import org.installmation.io.ApplicationJsonWriter
import org.installmation.model.*
import org.installmation.ui.dialog.ErrorDialog
import org.installmation.ui.dialog.HelpDialog
import org.installmation.ui.dialog.ItemListDialog
import java.io.File

/**
 * Manages Project lifecycle and fires all relevant project events
 */
class ProjectService(val configuration: Configuration) {

   companion object {
      val log: Logger = LogManager.getLogger(ProjectService::class.java)
   }

   fun newProject(name: String): InstallProject {
      val p = InstallProject()
      p.name = name
      return p
   }

   /**
    * Load from file
    */
   fun load(projectName: String): InstallProject {
      val projectFileName = InstallProject.projectFileName(projectName)
      val baseDir = projectBaseDirectory()
      val matches = baseDir.listFiles { pathName -> pathName.name == projectFileName }
      if (matches == null || matches.isEmpty())
         throw LoadDataException("Error loading project '${projectName}' from file. Project not found at ${baseDir.canonicalPath}")

      val file = matches[0]
      try {
         val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, file, JsonParserFactory.configurationParser())
         val project = reader.load()
         log.info("Project ${project.name} loaded successfully")
         configuration.eventBus.post(ProjectLoadedEvent(project))
         return project
      } catch (e: Exception) {
         throw LoadDataException("Error loading project '${projectName}' from file. Deserialization error.", e)
      }
   }

   fun close(p: InstallProject?) {
      if (p == null)
         return
      //save then close
      save(p)
      configuration.eventBus.post(ProjectSavedEvent(p))
      configuration.eventBus.post(ProjectClosedEvent(p))
      log.info("Project '${p.name}' closed")
   }

   /**
    * Fire events for all controllers to add their updates to the current
    * project. Nothing actually done here
    */
   fun save(p: InstallProject) {
      check(p.name != null && p.name!!.isNotEmpty())
      try {
         p.prepareForSave()
         configuration.eventBus.post(ProjectBeginSaveEvent(p))
         configuration.eventBus.post(ProjectSavedEvent(p))
         log.debug("Updated project ${p.name}")
      } catch (e: Exception) {
         throw SaveDataException("Error saving project ${p.name}", e)
      }
   }
   
   /**
    * Write to file
    */
   fun writeToFile(p: InstallProject) {
      check(p.name != null && p.name!!.isNotEmpty())

      try {
         val projectFile = File(projectBaseDirectory(), InstallProject.projectFileName(p.name!!))
         val writer = ApplicationJsonWriter<InstallProject>(projectFile, JsonParserFactory.configurationParser())
         writer.save(p)
         log.debug("Saved project ${p.name} to file")
      } catch (e: Exception) {
         throw SaveDataException("Error writing project ${p.name} to file", e)
      }
   }
   

   /**
    * Generates an image which contains all the parts required for an installer
    */
   fun generateImage(p: InstallProject) {
      try {
         log.info("Generate Image  - Validating configuration")
         val validationResult = p.validateConfiguration()
         if (!validationResult.success) {
            val d = ItemListDialog("Errors", "Issues", validationResult.errors)
            d.showNonModal()
            return
         }

         log.info("Generate Image  - Generating Image")
         val creator = InstallCreator(configuration)
         creator.createImage(p)
         log.info("Generate Image  - Image created successfully")
         HelpDialog.showAndWait("Image Created","Image created at ${p.imageBuildDirectory}")
      } catch (e: Exception) {
         log.info("Generate Image  - Failed with error: ${e.message}", e)
         ErrorDialog.showAndWait("Image Creation Error", e.toString())
      }
   }

   fun generateInstaller(p: InstallProject) {
      try {
         log.info("Generate Installer  - Validating configuration")
         val validationResult = p.validateConfiguration()
         if (!validationResult.success) {
            val d = ItemListDialog("Errors", "Issues", validationResult.errors)
            d.showNonModal()
            return
         }

         log.info("Generate Installer  - Generating Image")
         val creator = InstallCreator(configuration)
         creator.createInstaller(p)
         log.info("Generate Installer  - Image created successfully")
         HelpDialog.showAndWait("Installer Created","Image created at ${p.installerDirectory}")
      } catch (e: Exception) {
         log.info("Generate Installer  - Failed with error: ${e.message}", e)
         ErrorDialog.showAndWait("Installer Creation Error", e.toString())
      }
   }

   private fun projectBaseDirectory(): File {
      return File(configuration.baseDirectory, Constant.PROJECT_DIR)
   }
}