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


package org.installmation.io

import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.io.FileReader
import kotlin.reflect.KClass

/**
 * Load any application related json data from file
 * the classOfT parameter is a hack because we cannot determine Java class from a generic type,
 * thanks to JVM type erasure.
 */
class ApplicationJsonReader<T>(private val classOfT: KClass<*>, private val file: File, private val parser: Gson) {
   
   companion object {
      val log: Logger = LogManager.getLogger(ApplicationJsonReader::class.java)
   }

   fun load(): T {
      if (!file.exists())
         throw InstallationException("File not found at '${file.canonicalPath}'. May have been deleted or renamed.")

      log.debug("Loading file from: ${file.canonicalPath}")
      val reader = FileReader(file)
      try {
         return parser.fromJson<T>(reader, classOfT.java)
      } catch (e: Exception) {
         throw BadFileException("File '${file.canonicalPath}' could not be read. Problem parsing JSON.", e)
      }
   }
}