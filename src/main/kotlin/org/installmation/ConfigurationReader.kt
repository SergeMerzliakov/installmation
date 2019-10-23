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

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.io.FileReader

class ConfigurationReader(installPath: File) : ConfigurationProcessor(installPath) {

   companion object {
      val log: Logger = LogManager.getLogger(ConfigurationReader::class.java)
   }

   fun load(): Configuration {
      val configFile = Configuration.configurationFile(installPath)
      if (!configFile.exists())
         throw InstallationException("Configuration file not found at '${configFile.canonicalPath}'. May have been deleted or renamed.")

      log.debug("Loading configuration from: ${configFile.canonicalPath}")
      val reader = FileReader(configFile)
      val gson = createGson()
      try {
         return gson.fromJson<Configuration>(reader, Configuration::class.java)
      } catch (e: Exception) {
         throw BadFileException("Configuration file '${configFile.canonicalPath}' could not be read. Problem parsing JSON.", e)
      }
   }
}