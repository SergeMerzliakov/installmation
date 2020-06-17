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

import com.google.common.eventbus.EventBus
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.io.ApplicationJsonWriter
import org.installmation.model.binary.JDK
import java.io.File
import java.util.*


private val log: Logger = LogManager.getLogger(Configuration::class.java)
/**
 * JSON format
 * Always loaded from the same location
 */
class Configuration(bus: EventBus? = null, val baseDirectory: File = File(Constant.USER_HOME_DIR, Constant.APP_DIR)) {

   companion object {
      /**
       * Full path, relative to base path
       */
      fun configurationFile(baseDirectory: File = File(Constant.USER_HOME_DIR, Constant.APP_DIR)): File {
         return File(File(baseDirectory, Constant.CONFIG_DIR), Constant.CONFIG_FILE)
      }
   }

   // all mapped by a user defined name or label
   val jdkEntries = mutableMapOf<String, JDK>()
   val javafxLibEntries = mutableMapOf<String, File>()   // each lib dir in FX Directory
   val javafxModuleEntries = mutableMapOf<String, File>()  // each jmods dir in FX Directory
   @Transient val resourceBundle:ResourceBundle
   @Transient lateinit var eventBus: EventBus 
   
   init {
      log.trace("Configuration base directory set to $baseDirectory")
      if (bus != null)
         eventBus = bus
      log.trace("Loading messages from resource bundle for default locale ${Locale.getDefault().language}")
      resourceBundle = ResourceBundle.getBundle("i18n/messages")
      log.trace("Configuration setup successfully")
   }

   /**
    * Used after deserialization
    */
   fun initEventBus(bus: EventBus) {
      eventBus = bus
   }

   fun save() {
      val configWriter = ApplicationJsonWriter<Configuration>(configurationFile(), JsonParserFactory.configurationParser())
      configWriter.save(this)
   }
}