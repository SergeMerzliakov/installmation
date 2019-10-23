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

package org.installmation

import com.google.gson.*
import org.installmation.model.binary.*
import java.io.File
import java.lang.reflect.Type

/**
 *
 */
class JDKSerializer : JsonSerializer<JDK>, JsonDeserializer<JDK> {

   companion object {
      const val JDK_OS = "operating-system"
      const val JDK_PATH = "jdk-path"
   }

   override fun serialize(jdk: JDK?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
      val json = JsonObject()
      when (jdk) {
         is MacJDK -> json.addProperty(JDK_OS, OperatingSystem.OSX.name)
         is WindowsJDK -> json.addProperty(JDK_OS, OperatingSystem.Windows.name)
         is LinuxJDK -> json.addProperty(JDK_OS, OperatingSystem.Linux.name)
      }
      json.addProperty(JDK_PATH, jdk?.path?.absolutePath)
      return json
   }

   override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): JDK {
      val obj = json?.asJsonObject
      val osString = obj?.get(JDK_OS)?.asJsonPrimitive?.asString
      if (osString.isNullOrBlank())
         throw BadFileException("Unknown operating system value (null or empty) found deserializing JDK object")

      val os = OperatingSystem.valueOf(osString)
      val jdkPath = obj.get(JDK_PATH)?.asJsonPrimitive?.asString
      when (os) {
         OperatingSystem.OSX -> {
            val jdk = MacJDK(File(jdkPath))
            return jdk
         }
         OperatingSystem.Windows -> {
            val jdk = WindowsJDK(File(jdkPath))
            return jdk
         }
         OperatingSystem.Linux -> {
            val jdk = LinuxJDK(File(jdkPath))
            return jdk
         }
         else ->
            throw BadFileException("Error deserializing JDK object") // TODO - better details
      }
   }
}