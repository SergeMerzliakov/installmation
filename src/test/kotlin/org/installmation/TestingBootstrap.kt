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
package org.installmation

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.core.OperatingSystem
import java.io.File
import java.io.FileReader
import java.util.*

/**
 * Ensure we have a JDK and JFX installation in /test-config-NNN.properties files
 * If not fail and prompt the user to provide some
 */
object TestingBootstrap {

   private const val PROPERTY_TEST_JDK = "test.jdk"
   private const val PROPERTY_TEST_JFX = "test.javafx"
   private val log: Logger = LogManager.getLogger(TestingBootstrap::class.java)
   var jdk:File? = null
   var javafx:File? = null
   
   fun checkBinariesInstalled() {
      try {
         val config = getTestConfiguration()
               ?: throw BootstrapException("Empty test config files created at <repo>/testconfig/test-config-XXX.properties. This needs to be configured first. Fill in all properties as absolute paths before running unit tests")

         // we have a configuration - check it
         if (invalidTestConfiguration(config)) {
            throw BootstrapException("missing binaries (JDK or JavaFX). Check error log for details, as the test-config-XXX.properties may not be configured")
         }
      } catch (e: Throwable) {
         throw BootstrapException("Error checking this systems JDK and JavaFX requirements for testing", e)
      }
   }

   /**
    * On first clone, create empty directories for user to fill in their JDK and JavaFX details.
    * Try and use JDK if JAVA_HOME is configured
    */
   private fun createEmptyConfiguration(config: File) {
      config.parentFile.mkdirs()

      // try use JAVA_HOME to at least get a JDK
      val jh = System.getProperty("java.home")
      if (jh.isNotEmpty()) {
         val homePath = File(jh)
         if (homePath.exists()) {
            val jdkPath = homePath.path.replace("/Contents/Home", "")
            config.writeText("$PROPERTY_TEST_JDK=$jdkPath\n$PROPERTY_TEST_JFX=\n")
         }
      } else
         config.writeText("$PROPERTY_TEST_JDK=\n$PROPERTY_TEST_JFX=\n")
   }

   /*
     We have a configuration, now test that the file paths are valid
    */
   private fun invalidTestConfiguration(config: Properties): Boolean {
      var invalid = false
      jdk = File(config.getProperty(PROPERTY_TEST_JDK))
      if (!jdk!!.exists()) {
         invalid = true
         log.error("JDK not found at location [${jdk?.path}]. Check test-config-XXX.properties file has JDK details in test.jdk")
      }

      javafx = File(config.getProperty(PROPERTY_TEST_JFX))
      if (!javafx!!.exists()) {
         invalid = true
         log.error("JavaFX not found at location [${javafx?.path}]. Check test-config-XXX.properties file has JavaFX details in test.javafx")
      }

      return invalid
   }

   private fun getTestConfiguration(): Properties? {
      val file = when (OperatingSystem.os()) {
         OperatingSystem.Type.OSX -> "testconfig/test-config-osx.properties"
         OperatingSystem.Type.Windows -> "testconfig/test-config-windows.properties"
         OperatingSystem.Type.Linux -> "testconfig/test-config-linux.properties"
      }
      val configFile = File(file)
      if (!configFile.exists()) {
         // first time tests are run
         createEmptyConfiguration(configFile)
         return null
      }

      val iss = FileReader(configFile)
      val props = Properties()
      props.load(iss)
      return props
   }
}