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

package org.installmation.service

import org.assertj.core.api.Assertions.assertThat
import org.installmation.configuration.Configuration
import org.installmation.model.InstallProject
import org.junit.After
import org.junit.Test
import java.io.File

class ProjectServiceTest {

   private val baseDirectory = File("testproject")
   private val config = Configuration(baseDirectory)
   
   @After
   fun cleanup() {
      baseDirectory.deleteRecursively()
   }

   @Test
   fun shouldSaveProject() {
      val p = InstallProject()
      p.name = "project1"
      p.version = "1.0"
      p.modulePath = File("/some/dir")
      
      // save
      val service = ProjectService(config)
      service.saveProject(p)
      
      assertThat(p.projectFile(baseDirectory)).exists()
      assertThat(p.projectFile(baseDirectory).readText()).isNotEmpty()
   }

   @Test
   fun shouldLoadProject() {
      val p = InstallProject()
      p.name = "project2"
      p.version = "2.0"
      p.modulePath = File("/other/dir")

      // save
      val service = ProjectService(config)
      service.saveProject(p)

      // load and check 
      val loaded = service.loadProject(p.name!!)
      assertThat(loaded).isEqualToComparingFieldByField(p)
   }
}