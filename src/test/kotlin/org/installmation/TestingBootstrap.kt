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
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*

/**
 * Ensure we have a JDK and JFX installation in /test-config-NNN.properties files
 * If not fail and prompt the user to provide some
 */
object TestingBootstrap {

   private const val PROPERTY_TEST_JDK = "JDK"
   private const val PROPERTY_TEST_JFX = "JFX"
   private val log: Logger = LogManager.getLogger(TestingBootstrap::class.java)
   var jdk: File? = null
   var javafx: File? = null

   fun checkBinariesInstalled() {
      try {
         val config = getTestConfiguration()
         setupTestJDK(config)
         setupTestJavaFX(config)
      } catch (e: Throwable) {
         throw BootstrapException("Error checking this systems JDK and JavaFX requirements for testing", e)
      }
   }

   private fun setupTestJavaFX(config: Properties) {
      if (config[PROPERTY_TEST_JFX] != null) {
         javafx = File(config.getProperty(PROPERTY_TEST_JFX))
         if (!javafx!!.exists())
            throw FileNotFoundException("JavaFX not found in gradle.properties property $PROPERTY_TEST_JFX. Value found: ${javafx?.path}")
      } else
         throw BootstrapException("JavaFX not configured in gradle.properties property $PROPERTY_TEST_JFX. Set this for Jdeps based unit tests to run.")
   }

   // JDK property ovverides JAVA_HOME or system value
   private fun setupTestJDK(config: Properties) {
      if (config[PROPERTY_TEST_JDK] != null) {
         jdk = File(config.getProperty(PROPERTY_TEST_JDK))
         if (!jdk!!.exists())
            throw FileNotFoundException("JDK not found in gradle.properties property $PROPERTY_TEST_JDK. Value found: ${jdk?.path}")
      } else {
         val jh = System.getProperty("java.home")
         if (jh.isNotEmpty()) {
            jdk = File(jh)
            val v = getJDKMajorVersion()
            if (v >= 11)
               log.info("JDK for test - ${jdk?.path}")
            else
               throw BootstrapException("JAVA_HOME JDK not version 11+. JDK version is $v. Install another JDK 11+ and set gradle.properties $PROPERTY_TEST_JDK to point to that JDK")
         }
      }
   }

   private fun getTestConfiguration(): Properties {
      val configFile = File("local.properties")
      val iss = FileReader(configFile)
      val props = Properties()
      props.load(iss)
      return props
   }

   private fun getJDKMajorVersion(): Int {
      val parts = System.getProperty("java.version").split(".")
      return parts[0].toInt()
   }
}