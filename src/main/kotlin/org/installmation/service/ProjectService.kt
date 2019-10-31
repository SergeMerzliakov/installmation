/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 **/
package org.installmation.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.model.InstallProject
import org.installmation.model.LoadDataException
import org.installmation.model.SaveDataException
import java.io.File
import java.io.FileReader

/**
 * Manages Project lifecycle
 */
class ProjectService(val configuration: Configuration) {

   companion object {
      const val PROJECT_DIR = "projects"
      val log: Logger = LogManager.getLogger(ProjectService::class.java)
   }

   fun newProject(name: String): InstallProject {
      val p = InstallProject()
      p.name = name
      return p
   }

   fun openProject(projectName: String): InstallProject {
      val projectFileName = createProjectFileName(projectName)
      val baseDir = projectBaseDirectory()
      val matches = baseDir.listFiles { pathName -> pathName.name == projectFileName }
      if (matches == null || matches.isEmpty())
         throw LoadDataException("Error loading project '${projectName}' from file. Project not found at ${baseDir.canonicalPath}")

      val file = matches[0]
      val gson = Gson()
      try {
         val project = gson.fromJson(FileReader(file), InstallProject::class.java)
         log.info("project ${project.name} loaded successfully")
         return project
      } catch (e: Exception) {
         throw LoadDataException("Error loading project '${projectName}' from file. Deserialization error.", e)
      }
   }

   fun saveProject(p: InstallProject) {
      check(p.name != null && p.name!!.isNotEmpty())

      val gson = GsonBuilder().setPrettyPrinting().create()
      try {
         val data = gson.toJson(p, p::class.java)
         val baseDir = projectBaseDirectory()
         baseDir.mkdirs()
         val projectFile = File(baseDir, createProjectFileName(p.name!!))
         projectFile.writeText(data)
         log.debug("Saved project ${p.name} to file")
      } catch (e: Exception) {
         throw SaveDataException("Error saving project ${p.name} to file", e)
      }
   }

   private fun createProjectFileName(name: String): String {
      return "$name.json"
   }

   private fun projectBaseDirectory(): File {
      return File(configuration.baseDirectory, PROJECT_DIR)
   }
}