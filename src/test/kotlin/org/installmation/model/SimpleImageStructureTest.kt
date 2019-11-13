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

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class SimpleImageStructureTest {

   companion object {
      const val FILE1 = "file1"
      const val DIR1 = "dir1"
      const val DIR2 = "dir2"
   }

   @Test
   fun shouldAcceptMultipleDirectories() {
      val sim = SimpleImageStructure()
      sim.addDirectory(DIR1)
      sim.addDirectory(DIR2)

      assertThat(sim.getDirectories()).hasSize(2)
   }

   @Test
   fun shouldAcceptMultipleFiles() {
      val sim = SimpleImageStructure()
      sim.addFile("file1")
      sim.addFile("file2")

      assertThat(sim.getFiles()).hasSize(2)
      assertThat(sim.getDirectories()).hasSize(0)
   }

   @Test
   fun shouldTrimNames() {
      val sim = SimpleImageStructure()
      sim.addFile("$FILE1  ")
      sim.addDirectory("  $DIR1")

      assertThat(sim.getFiles()).contains(FILE1)
      assertThat(sim.getDirectories()).contains(File(DIR1))
   }


   @Test
   fun shouldHandleDuplicateDirectory() {
      val sim = SimpleImageStructure()
      val jar = "foo.jar"
      sim.mainJar = jar
      sim.addDirectory(DIR1)
      sim.addDirectory(DIR1)
      sim.addFile(jar)

      assertThat(sim.getDirectories()).hasSize(1)
      assertThat(sim.getDirectories()).contains(File(DIR1))
   }

   @Test
   fun shouldRejectEmptyDirectory() {
      val sim = SimpleImageStructure()
      Assertions.assertThatExceptionOfType(IllegalStateException::class.java).isThrownBy { sim.addDirectory("") }
   }

   @Test
   fun shouldRejectEmptyDirectory2() {
      val sim = SimpleImageStructure()
      Assertions.assertThatExceptionOfType(IllegalStateException::class.java).isThrownBy { sim.addDirectory("   ") }
   }

   @Test
   fun shouldRejectEmptyFile() {
      val sim = SimpleImageStructure()
      Assertions.assertThatExceptionOfType(IllegalStateException::class.java).isThrownBy { sim.addFile("") }
   }

}