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
import java.io.File

/**
 * Manages Project lifecycle
 */
class ProjectService(val configuration: Configuration) {

   companion object {
      val log: Logger = LogManager.getLogger(ProjectService::class.java)
   }

   fun newProject(name: String): InstallProject {
      val p = InstallProject()
      p.imageStructure = SimpleImageStructure()
      p.name = name
      return p
   }

   fun loadProject(projectName: String): InstallProject {
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
         return project
      } catch (e: Exception) {
         throw LoadDataException("Error loading project '${projectName}' from file. Deserialization error.", e)
      }
   }

   fun saveProject(p: InstallProject) {
      check(p.name != null && p.name!!.isNotEmpty())

      try {
         val projectFile = File(projectBaseDirectory(), InstallProject.projectFileName(p.name!!))
         val writer = ApplicationJsonWriter<InstallProject>(projectFile, JsonParserFactory.configurationParser())
         writer.save(p)
         log.debug("Saved project ${p.name} to file")
      } catch (e: Exception) {
         throw SaveDataException("Error saving project ${p.name} to file", e)
      }
   }

   private fun projectBaseDirectory(): File {
      return File(configuration.baseDirectory, Constant.PROJECT_DIR)
   }
}