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