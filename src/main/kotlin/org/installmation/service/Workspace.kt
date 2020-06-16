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

import com.google.common.eventbus.Subscribe
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.*
import org.installmation.controller.Validator
import org.installmation.io.ApplicationJsonWriter
import org.installmation.model.InstallProject
import java.io.File


private val log: Logger = LogManager.getLogger(Workspace::class.java)

fun workspaceFileName(baseDirectory: File): File {
   return File(File(baseDirectory, Constant.WORKSPACE_DIR), Constant.WORKSPACE_FILE)
}

/**
 * Stores history of projects worked on, and has its own
 * persistent file. If this is cannot be loaded or saved,
 * all that is lost is a list of projects.
 *
 * Not a concept that is visible to the user, so they will
 * never 'see' workspaces
 */
class Workspace(var userHistory: UserHistory,
                @Transient var configuration: Configuration,
                @Transient var projectService: ProjectService ?= null) {

   var currentProject: InstallProject? = null
      private set

   // project name -> location on disk
   val projectHistory = mutableMapOf<String, File>()

   init{
      configuration.eventBus.register(this)
   }

   fun setCurrentProject(p: InstallProject) {
      checkNotNull(p.name, { "Project must have a name before it can be used" })
      currentProject = p
      log.debug("Workspace current project is set to '${p.name}'")
   }

   fun closeCurrentProject() {
      if (currentProject != null) {
         log.debug("Project '${currentProject!!.name}' closed")
         currentProject = null
      }
   }


   /**
    * For files already saved. return false if no previous file saveds
    */
   fun save():Boolean {
      val file = projectHistory[currentProject?.name]

      if (currentProject == null || file == null)
         return false

      if (Validator.ensureProjectName(currentProject!!, configuration)) {
         log.debug("Saving project [${currentProject?.name}] to file")
         projectService?.save(file, currentProject!!)
         val workspaceWriter = ApplicationJsonWriter<Workspace>(workspaceFileName(configuration.baseDirectory), JsonParserFactory.workspaceParser(configuration))
         workspaceWriter.save(this)
         return true
      }
      return false
   }

   /**
    * New location for saved file
    */
   fun saveAs(file: File) {
      if (currentProject == null)
         setCurrentProject(InstallProject())

      val name = currentProject!!.name!!
      if (Validator.ensureProjectName(currentProject!!, configuration)) {
         projectService?.save(file, currentProject!!)
         // save projects history
         projectHistory[name] = file
         val workspaceWriter = ApplicationJsonWriter<Workspace>(workspaceFileName(configuration.baseDirectory), JsonParserFactory.workspaceParser(configuration))
         workspaceWriter.save(this)
      }
   }


   //-------------------------------------------------------
   //  Event Subscribers
   //-------------------------------------------------------

   @Subscribe
   fun handleProjectLoading(e: ProjectLoadingEvent) {
      userHistory.set(HISTORY_PROJECT, e.projectFile.parentFile)
      val p = projectService?.load(e.projectFile)
      if (p != null) {
         setCurrentProject(p)
      }else
         throw ProjectLoadException(e.projectFile)
   }

}