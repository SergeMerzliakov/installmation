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

package org.installmation.model.binary

import com.google.gson.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.core.OperatingSystem
import java.io.File
import java.lang.reflect.Type
import kotlin.random.Random

/**
 * Custom serializer
 */
class JDKSerializer : JsonSerializer<JDK>, JsonDeserializer<JDK> {

   companion object {
      const val JDK_OS = "operating-system"
      const val JDK_PATH = "jdk-path"
      const val JDK_NAME = "jdk-name"

      val log: Logger = LogManager.getLogger(JDKSerializer::class.java)
   }

   override fun serialize(jdk: JDK?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
      val json = JsonObject()
      json.addProperty(JDK_NAME, jdk?.name)
      json.addProperty(JDK_PATH, jdk?.path?.absolutePath)
      when (jdk) {
         is MacJDK -> json.addProperty(JDK_OS, OperatingSystem.Type.OSX.name)
         is WindowsJDK -> json.addProperty(JDK_OS, OperatingSystem.Type.Windows.name)
         is LinuxJDK -> json.addProperty(JDK_OS, OperatingSystem.Type.Linux.name)
      }

      return json
   }

   override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): JDK {
      val obj = json?.asJsonObject
      var jdkName = obj?.get(JDK_NAME)?.asJsonPrimitive?.asString
      if (jdkName.isNullOrBlank()) {
         jdkName = "JDK-" + Random.nextInt(0, 9999)
         log.warn("Deserialized JDK had no name. Not fatal, but random name ${jdkName} assigned.")
      }
      val osString = obj?.get(JDK_OS)?.asJsonPrimitive?.asString
      val os: OperatingSystem.Type;
      if (osString.isNullOrBlank()) {
         os = OperatingSystem.os()
         log.warn("Deserialized JDK had no operating system. Not fatal, but operating system determined dynamically")
      } else
         os = OperatingSystem.Type.valueOf(osString)

      val jdkPath = obj?.get(JDK_PATH)?.asJsonPrimitive?.asString
      return when (os) {
         OperatingSystem.Type.OSX -> MacJDK(jdkName, File(jdkPath))
         OperatingSystem.Type.Windows -> WindowsJDK(jdkName, File(jdkPath))
         OperatingSystem.Type.Linux -> LinuxJDK(jdkName, File(jdkPath))
      }
   }
}