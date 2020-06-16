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

package org.installmation.service

import com.google.common.eventbus.EventBus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.installmation.TestConstants
import org.installmation.configuration.Configuration
import org.installmation.model.InstallProject
import org.junit.After
import org.junit.Test
import java.io.File


class ProjectServiceTest {

   private val baseDirectory = File(TestConstants.TEST_TEMP_DIR)
   private val config = Configuration(EventBus(), baseDirectory)

   @After
   fun cleanup() {
      baseDirectory.deleteRecursively()
   }

   @Test
   fun shouldWriteProjectToFile() {
      val p = InstallProject()
      p.name = "project1"
      p.version = "1.0"
      p.customModules = mutableSetOf("java.sql", "java.base")

      val file = File(baseDirectory, "proj.json")
      // save
      val service = ProjectService(config)
      service.save(file, p)

      assertThat(file).exists()
      assertThat(file.readText()).isNotEmpty()
   }

   @Test
   fun shouldLoadProject() {
      val p = InstallProject()
      p.name = "project2"
      p.version = "2.0"
      p.customModules = mutableSetOf("java.sql", "java.base")
      val file = File(baseDirectory, "proj.json")

      // save
      val service = ProjectService(config)
      service.save(file, p)

      // load and check 
      val loaded = service.load(file)
      assertThat(loaded).isEqualToComparingFieldByField(p)
   }

   @Test
   fun shouldNotSaveEmptyProject() {
      val p = InstallProject()
      val service = ProjectService(config)
      assertThatExceptionOfType(IllegalStateException::class.java).isThrownBy { service.collectUpdates(p) }
   }
}