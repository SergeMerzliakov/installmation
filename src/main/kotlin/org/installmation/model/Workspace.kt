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

package org.installmation.model

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Constant
import java.io.File

/**
 * Stores history of projects worked on, and has its own
 * persistent file. If this is cannot be loaded or saved,
 * all that is lost is a list of projects.
 *
 * Not a concept that is visible to the user, so they will
 * never 'see' workspaces
 */
class Workspace {

   companion object {
      val log: Logger = LogManager.getLogger(Workspace::class.java)

      /**
       * Full path, relative to base path
       */
      fun workspaceFile(baseDirectory: File = File(Constant.USER_HOME_DIR, Constant.APP_DIR)): File {
         return File(File(baseDirectory, Constant.WORKSPACE_DIR), Constant.WORKSPACE_FILE)
      }
   }

   var currentProject: InstallProject? = null
      private set
   
   // project name -> location on disk
   private var projectHistory = mutableMapOf<String, File>()

   fun setCurrentProject(p: InstallProject) {
      checkNotNull(p.name)
      currentProject = p
      projectHistory[p.name!!] = p.projectFile()
      log.debug("Workspace current project is set to '${p.name}'")
   }
}