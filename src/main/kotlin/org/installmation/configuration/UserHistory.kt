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


package org.installmation.configuration

import org.apache.logging.log4j.LogManager
import java.io.File


const val HISTORY_ARTEFACT = "artefactPath"
const val HISTORY_CLASSPATH = "classpathPath"
const val HISTORY_IMAGE = "imagePath"
const val HISTORY_INPUT = "inputPath"
const val HISTORY_INSTALLER = "installerPath"
const val HISTORY_KEYCHAIN = "keychainPath"
const val HISTORY_LOGO = "logoPath"
const val HISTORY_MAIN_JAR = "mainJarPath"
const val HISTORY_MODULEPATH = "modulePath"
const val HISTORY_PROJECT = "projectPath"
const val HISTORY_SCRIPT = "scriptPath"

private val log = LogManager.getLogger(UserHistory::class.java);

/**
 * Stores users selection history for convenience
 */
class UserHistory {

   private val items = mutableMapOf<String, String>()


   fun set(item: String, value: String) {
      if (item.trim().isEmpty() || value.trim().isEmpty()) {
         log.warn("Cannot add empty item to history")
         return
      }

      items[item] = value
   }

   fun set(item: String, file: File) {
      if (item.trim().isEmpty()) {
         log.warn("Cannot add empty item to history")
         return
      }

      items[item] = file.absolutePath
   }

   fun get(item: String): String? {
      return items[item]
   }

   fun getFile(item: String, default: File? = null): File {
      if (!items.contains(item)) {
         return default ?: Constant.DEFAULT_BASE_DIR
      }
      return File(items[item])
   }

   override fun equals(rhs: Any?): Boolean {
      if (this === rhs) return true
      if (javaClass != rhs?.javaClass) return false

      rhs as UserHistory

      if (items != rhs.items) return false

      return true
   }

   override fun hashCode(): Int {
      return items.hashCode()
   }

}