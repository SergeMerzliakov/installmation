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

package org.installmation.model

import org.assertj.core.api.Assertions.assertThat
import org.installmation.TestConstants
import org.installmation.configuration.JsonParserFactory
import org.installmation.core.OperatingSystem
import org.installmation.io.ApplicationJsonReader
import org.installmation.io.ApplicationJsonWriter
import org.installmation.model.binary.JDKFactory
import org.junit.AfterClass
import org.junit.Test
import java.io.File

class InstallProjectModelTest {

   companion object {
      val SAVED_FILE = File(TestConstants.TEST_TEMP_DIR, "project.json")
      val PROJECTS_DIR = File(TestConstants.TEST_RESOURCES, "projects")

      @AfterClass
      @JvmStatic
      fun cleanup() {
         SAVED_FILE.parentFile.deleteRecursively()
      }
   }

   @Test
   fun hasValidName() {
      assertThat(InstallProject("project").hasValidName()).isTrue()
   }

   @Test
   fun hasInvalidEmptyName() {
      assertThat(InstallProject("").hasValidName()).isFalse()
   }

   @Test
   fun hasInvalidNullName() {
      assertThat(InstallProject().hasValidName()).isFalse()
   }

   @Test
   fun shouldSerializeMinimalProject() {
      val p1 = InstallProject("project")

      val writer = ApplicationJsonWriter<InstallProject>(SAVED_FILE, JsonParserFactory.configurationParser())
      writer.save(p1)

      val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, SAVED_FILE, JsonParserFactory.configurationParser())
      val p2 = reader.load()

      assertThat(p2).isEqualToComparingFieldByField(p1)
   }


   @Test
   fun shouldSerializeProject() {
      val p1 = InstallProject("project")
      p1.vendor = "acme"
      p1.customModules = mutableSetOf("java.sql", "java.management")
      p1.imageBuildDirectory = File("image")
      p1.installerDirectory = File("installer")
      p1.applicationLogo = File("logo.png")
      p1.jpackageJDK = JDKFactory.create(OperatingSystem.os(), "package49", File("/java11/bin/jpackage"))

      val writer = ApplicationJsonWriter<InstallProject>(SAVED_FILE, JsonParserFactory.configurationParser())
      writer.save(p1)

      val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, SAVED_FILE, JsonParserFactory.configurationParser())
      val p2 = reader.load()

      assertThat(p2).isEqualToComparingFieldByField(p1)
   }

   @Test
   fun shouldLoadVersion1Projects() {
      val projectFile = File(PROJECTS_DIR, "project_v1.json")
      val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, projectFile, JsonParserFactory.configurationParser())
      reader.load()
   }

   @Test
   fun shouldLoadVersion1_1Projects() {
      val projectFile = File(PROJECTS_DIR, "project_v1.1.json")
      val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, projectFile, JsonParserFactory.configurationParser())
      reader.load()
   }
}