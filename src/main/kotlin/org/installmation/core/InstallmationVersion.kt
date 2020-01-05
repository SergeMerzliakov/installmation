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
package org.installmation.core

import com.google.common.io.Resources
import java.io.StringReader
import java.util.*

object InstallmationVersion {

   private val majorVersion: String
   private val minorVersion: String
   private val build: String

   init {
      val data = Properties()
      val propData = Resources.getResource("version.properties").readText()
      data.load(StringReader(propData))
      majorVersion = data.getProperty("majorVersion")
      minorVersion = data.getProperty("minorVersion")
      build = data.getProperty("buildNumber")
   }

   fun version(): String {
      return "Installmation v$majorVersion.$minorVersion.$build"
   }
}