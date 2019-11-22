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
import org.installmation.io.BadFileException
import java.io.File
import java.lang.reflect.Type

open class FileArtefact(val name: String, var source: File, var destination: File? = null) : InstallArtefact {
   
   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is FileArtefact) return false

      if (name != other.name) return false
      if (source != other.source) return false
      if (destination != other.destination) return false

      return true
   }

   override fun hashCode(): Int {
      var result = name.hashCode()
      result = 31 * result + source.hashCode()
      result = 31 * result + (destination?.hashCode() ?: 0)
      return result
   }
}

/**
 * This serializes FileArtefacts and subclasses. This may not be ideal but saves on a lot of
 * cutting and pasting. Looking for a better way..
 */
class FileArtefactSerializer : JsonSerializer<FileArtefact>, JsonDeserializer<FileArtefact> {

   companion object {
      const val NAME = "name"
      const val SOURCE_LOCATION = "source"
      const val DESTINATION_LOCATION = "destination"
      const val ARTEFACT_TYPE = "type"
   }

   override fun serialize(artefact: FileArtefact?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
      val json = JsonObject()
      json.addProperty(NAME, artefact?.name)
      json.addProperty(SOURCE_LOCATION, artefact?.source?.path)
      json.addProperty(DESTINATION_LOCATION, artefact?.destination?.path)
      json.addProperty(ARTEFACT_TYPE, artefact?.javaClass?.name)
      return json
   }

   override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): FileArtefact {
      val obj = json?.asJsonObject
      val name = obj?.get(NAME)?.asJsonPrimitive?.asString
      val source = obj?.get(SOURCE_LOCATION)?.asJsonPrimitive?.asString
      val destination = obj?.get(DESTINATION_LOCATION)?.asJsonPrimitive?.asString
      var destinationFile: File? = null
      if (destination != null)
         destinationFile = File(destination)

      val type = obj?.get(ARTEFACT_TYPE)?.asJsonPrimitive?.asString
      return when (type) {
         FileArtefact::class.java.name -> FileArtefact(name!!, File(source!!), destinationFile)
         DirectoryArtefact::class.java.name -> DirectoryArtefact(name!!, File(source!!), destinationFile)
         else -> throw BadFileException("Unknown install artefact type '$type'")
      }
   }
}