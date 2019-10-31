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

package org.installmation.io

import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Save any application related json data to file
 */
class ApplicationJsonWriter<T>(private val file: File, private val parser: Gson) {

   companion object {
      val log: Logger = LogManager.getLogger(ApplicationJsonWriter::class.java)
   }

   fun save(data: T) {
      if (!file.parentFile.exists()) {
         // first time - may not exist and that's OK
         file.parentFile.mkdirs()
         log.info("Creating configuration directory '${file.parentFile.canonicalPath}' for first time")
      }

      log.debug("Write configuration to: ${file.canonicalPath}")

      try {
         val jsonContents = parser.toJson(data)
         file.writeText(jsonContents, StandardCharsets.UTF_8)
      } catch (e: Exception) {
         throw RuntimeException("Configuration file '${file.canonicalPath}' could not be saved. Problem converting to JSON.")
      }
   }
}