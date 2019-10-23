package org.installmation

import org.assertj.core.api.Assertions.assertThat
import org.installmation.model.binary.MacJDK
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

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

class ConfigurationReadWriteTest {

   private val TEST_DIR = "configtest"
   private lateinit var configDir: File 

   @BeforeEach
   fun setup() {
      configDir = File(TEST_DIR)
      configDir.mkdirs()
   }

   @AfterEach
   fun cleanup() {
      configDir.deleteRecursively()
   }

   @Test
   fun shouldPersistSingleFXLibConfig() {
      val conf = Configuration()
      
      val label = "fx13"
      val fxlibs = "dir/javafx13/libs"
      conf.javafxLibEntries[label] = File(fxlibs)

      // save
      val writer = ConfigurationWriter(configDir)
      writer.save(conf)
      
      val savedFile = Configuration.configurationFile(configDir)
      assertThat(savedFile).exists()
      
      // now read 
      val reader = ConfigurationReader(configDir)
      val loadedConf = reader.load()
      
      assertThat(loadedConf.javafxLibEntries).hasSize(1)
      assertThat(loadedConf.javafxModuleEntries).hasSize(0)
      assertThat(loadedConf.jdkEntries).hasSize(0)
      
      assertThat(loadedConf.javafxLibEntries[label]).isEqualTo(File(fxlibs))
   }

   @Test
   fun shouldPersistMultipleFXLibConfig() {
      val conf = Configuration()

      // FX 12
      val label12 = "fx12"
      val fxlibs12 = "dir/javafx12/libs"
      conf.javafxLibEntries[label12] = File(fxlibs12)

      // FX 13
      val label13 = "fx13"
      val fxlibs13 = "dir/javafx13/libs"
      conf.javafxLibEntries[label13] = File(fxlibs13)

      // save
      val writer = ConfigurationWriter(configDir)
      writer.save(conf)

      val savedFile = Configuration.configurationFile(configDir)
      assertThat(savedFile).exists()

      // now read 
      val reader = ConfigurationReader(configDir)
      val loadedConf = reader.load()

      assertThat(loadedConf.javafxLibEntries).hasSize(2)
      assertThat(loadedConf.javafxModuleEntries).hasSize(0)
      assertThat(loadedConf.jdkEntries).hasSize(0)
      
      assertThat(loadedConf.javafxLibEntries[label12]).isEqualTo(File(fxlibs12))
      assertThat(loadedConf.javafxLibEntries[label13]).isEqualTo(File(fxlibs13))
   }

   @Test
   fun shouldPersistFXModuleConfig() {
      val conf = Configuration()

      val label = "fx13"
      val fxmods = "dir/javafx13/mods"
      conf.javafxModuleEntries[label] = File(fxmods)

      // save
      val writer = ConfigurationWriter(configDir)
      writer.save(conf)

      val savedFile = Configuration.configurationFile(configDir)
      assertThat(savedFile).exists()

      // now read 
      val reader = ConfigurationReader(configDir)
      val loadedConf = reader.load()

      assertThat(loadedConf.javafxLibEntries).hasSize(0)
      assertThat(loadedConf.javafxModuleEntries).hasSize(1)
      assertThat(loadedConf.jdkEntries).hasSize(0)

      assertThat(loadedConf.javafxModuleEntries[label]).isEqualTo(File(fxmods))
   }

   @Test
   fun shouldPersistJDKConfig() {
      val conf = Configuration()

      val label = "jpackager-49"
      val macjdk14 = MacJDK(File("dir/java14"))
      conf.jdkEntries[label] = macjdk14

      // save
      val writer = ConfigurationWriter(configDir)
      writer.save(conf)

      val savedFile = Configuration.configurationFile(configDir)
      assertThat(savedFile).exists()

      // now read 
      val reader = ConfigurationReader(configDir)
      val loadedConf = reader.load()

      assertThat(loadedConf.javafxLibEntries).hasSize(0)
      assertThat(loadedConf.javafxModuleEntries).hasSize(0)
      assertThat(loadedConf.jdkEntries).hasSize(1)

      assertThat(loadedConf.jdkEntries[label]).isEqualTo(macjdk14)
   }
}