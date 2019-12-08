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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.installmation.model.binary.*
import org.installmation.service.Workspace

/**
 * Functionality shared by configuration readers and writers
 */
object JsonParserFactory {

   /**
    * for configuration files
    */
   fun configurationParser(): Gson {
      val builder = GsonBuilder()
      builder.registerTypeAdapter(JDK::class.java, JDKSerializer())
      builder.registerTypeAdapter(MacJDK::class.java, JDKSerializer())
      builder.registerTypeAdapter(WindowsJDK::class.java, JDKSerializer())
      builder.registerTypeAdapter(LinuxJDK::class.java, JDKSerializer())
      builder.registerTypeAdapter(Workspace::class.java, WorkspaceSerializer())
      return builder.setPrettyPrinting().create()
   }

   /**
    * For POJOs
    */
   fun basicParser(): Gson {
      val builder = GsonBuilder()
      return builder.setPrettyPrinting().create()
   }

}