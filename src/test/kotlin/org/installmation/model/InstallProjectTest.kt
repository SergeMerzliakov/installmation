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
import org.installmation.io.ApplicationJsonReader
import org.installmation.io.ApplicationJsonWriter
import org.installmation.model.binary.JDKFactory
import org.installmation.model.binary.JPackageExecutable
import org.installmation.model.binary.OperatingSystem
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

class InstallProjectTest {

   companion object {
      val SAVED_FILE = File("testdata", "project.json")

      @AfterClass
      @JvmStatic
      fun cleanup() {
         SAVED_FILE.parentFile.deleteRecursively()
      }
   }

   @Test
   fun shouldSerializeMinimalProject() {
      val name = "project"

      val p = InstallProject()
      p.name = name

      val writer = ApplicationJsonWriter<InstallProject>(SAVED_FILE, JsonParserFactory.configurationParser())
      writer.save(p)

      val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, SAVED_FILE, JsonParserFactory.configurationParser())
      val p2 = reader.load()

      assertThat(p2.name).isEqualTo(p.name)
   }


   @Test
   fun shouldSerializeProject() {
      val name = "project"
      val version = "1.0"
      val p = InstallProject()
      p.name = name
      p.version = version
      p.modulePath = mutableSetOf(File("module1"))
      p.imageBuildDirectory = File("image")
      p.imageContentDirectory = File("content")
      p.jpackageJDK = JDKFactory.create(OperatingSystem.os(), "package49", File("/java11/bin/jpackage"))
      
      // directory and file artefacts
      p.artefacts["classpathLib"] = DirectoryArtefact("classpathLib", File("/myapp/lib"))
      p.artefacts["mainJar"] = FileArtefact("mainJar", File("/myapp/app.jar"))
      
      val writer = ApplicationJsonWriter<InstallProject>(SAVED_FILE, JsonParserFactory.configurationParser())
      writer.save(p)

      val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, SAVED_FILE, JsonParserFactory.configurationParser())
      val p2 = reader.load()

      assertThat(p2).isEqualToComparingFieldByField(p)
   }

   @Test
   fun shouldCreateImage() {
      // TODO
   }

   //   export JPACKAGE=/Users/foo/tools/jpackage49/Contents/Home/bin/jpackage
   //   $JPACKAGE --package-type pkg 
   //   -d ../../image-build 
   //   --name demoApplication1 
   //   --app-image ../../image-build/demo1.app
   @Test
   fun shouldCreatePackage() {
      //TODO
   }
}