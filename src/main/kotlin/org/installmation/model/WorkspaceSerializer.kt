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

package org.installmation.model

import com.google.gson.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.io.ApplicationJsonReader
import java.io.File
import java.lang.reflect.Type

/**
 * Custom serializer, ensuring current project is DESERed properly
 */
class WorkspaceSerializer : JsonSerializer<Workspace>, JsonDeserializer<Workspace> {

   companion object {
      const val CURRENT_PROJECT_NAME = "current-project-name"
      const val CURRENT_PROJECT_PATH = "current-project-path"
      val log: Logger = LogManager.getLogger(WorkspaceSerializer::class.java)
   }

   override fun serialize(workspace: Workspace?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
      val json = JsonObject()
      json.addProperty(CURRENT_PROJECT_NAME, workspace?.currentProject?.name)
      json.addProperty(CURRENT_PROJECT_PATH, workspace?.currentProject?.projectFile()?.canonicalPath)
      return json
   }

   override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Workspace {
      val obj = json?.asJsonObject
      var name = obj?.get(CURRENT_PROJECT_NAME)?.asJsonPrimitive?.asString
      var path = obj?.get(CURRENT_PROJECT_PATH)?.asJsonPrimitive?.asString
      //load current project completely
      val ws = Workspace()
      if (path != null) {
         val p = deserializeProject(path)
         if (p != null)
            ws.setCurrentProject(p)
      }
      return ws
   }


   /**
    * If this fails it's not TOO bad.
    */
   private fun deserializeProject(projectFile: String): InstallProject? {
      try {
         val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, File(projectFile), JsonParserFactory.configurationParser())
         return reader.load()
      } catch (e: Exception) {
         log.error("Error loading current project from json", e)
         // TODO error dialog
      }
      return null
   }
}