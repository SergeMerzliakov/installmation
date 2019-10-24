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
import org.installmation.model.binary.JDK
import java.io.File

/**
 * JSON format
 * Always loaded from the same location
 */
class Configuration {
   // all mapped by a user defined name or label
   val jdkEntries = mutableMapOf<String, JDK>()
   val javafxLibEntries = mutableMapOf<String, File>()   // each lib dir in FX Directory
   val javafxModuleEntries = mutableMapOf<String, File>()  // each jmods dir in FX Directory

   companion object {
      val log: Logger = LogManager.getLogger(Configuration::class.java)

      /**
       * Full path, relative to base path
       */
      fun configurationFile(basePath: File): File {
         return File(File(basePath, Constant.CONFIG_DIR), Constant.CONFIG_FILE)
      }
   }

}