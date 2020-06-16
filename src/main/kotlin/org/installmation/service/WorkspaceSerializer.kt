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

import com.google.gson.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.configuration.JsonParserFactory
import org.installmation.configuration.UserHistory
import org.installmation.io.ApplicationJsonReader
import org.installmation.model.InstallProject
import org.installmation.ui.dialog.ErrorDialog
import java.io.File
import java.lang.reflect.Type


private const val USER_HISTORY = "user-history"
const val CURRENT_PROJECT_PATH = "current-project-path"
private val log: Logger = LogManager.getLogger(WorkspaceSerializer::class.java)

/**
 * Custom serializer, ensuring current project is DESERed properly
 */
class WorkspaceSerializer(val configuration: Configuration) : JsonSerializer<Workspace>, JsonDeserializer<Workspace> {

   override fun serialize(workspace: Workspace?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
      val json = JsonObject()
      json.addProperty(CURRENT_PROJECT_PATH, workspace?.currentProject?.projectFile(configuration.baseDirectory)?.canonicalPath)
      json.add(USER_HISTORY, context?.serialize(workspace?.userHistory))
      return json
   }

   override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Workspace {
      val obj = json?.asJsonObject

      // user history first
      var userHistory: UserHistory?
      try {
         userHistory = context?.deserialize<UserHistory>(obj?.get(USER_HISTORY), UserHistory::class.java)
         if (userHistory == null)
            userHistory = UserHistory()
      }
      catch (e: java.lang.Exception) {
         log.error("Error loading User History from file", e)
         userHistory = UserHistory()
      }

      val ws = Workspace(userHistory!!, this.configuration)

      //load current project if present
      val path = obj?.get(CURRENT_PROJECT_PATH)?.asJsonPrimitive?.asString
      if (path != null) {
         val p = deserializeProject(path)
         if (p != null)
            ws.setCurrentProject(p)
      }
      return ws
   }

   private fun deserializeProject(projectFile: String): InstallProject? {
      try {
         val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, File(projectFile), JsonParserFactory.configurationParser())
         return reader.load()
      } catch (e: Exception) {
         log.error("Error loading current project from json", e)
         ErrorDialog.showAndWait("Error Loading Project", e.toString())
      }
      return null
   }
}