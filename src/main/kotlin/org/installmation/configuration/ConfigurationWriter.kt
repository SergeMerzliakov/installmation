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

package org.installmation.configuration

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.nio.charset.StandardCharsets

class ConfigurationWriter(installPath: File) : ConfigurationProcessor(installPath) {

   companion object {
      val log: Logger = LogManager.getLogger(ConfigurationWriter::class.java)
   }

   fun save(config: Configuration) {
      val configFile = Configuration.configurationFile(installPath)
      if (!configFile.parentFile.exists()) {
         // first time - may not exist and that's OK
         configFile.parentFile.mkdirs()
         log.info("Creating configuration directory '${configFile.parentFile.canonicalPath}' for first time")
      }

      log.debug("Write configuration to: ${configFile.canonicalPath}")

      val gson = createGson()
      try {
         val jsonContents = gson.toJson(config)
         configFile.writeText(jsonContents, StandardCharsets.UTF_8)
      } catch (e: Exception) {
         throw RuntimeException("Configuration file '${configFile.canonicalPath}' could not be saved. Problem converting to JSON.")
      }
   }
}