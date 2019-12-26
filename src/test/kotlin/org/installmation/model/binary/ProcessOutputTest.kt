package org.installmation.model.binary

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class ProcessOutputTest {


   @Test
   fun shouldProcessSuccessfulOutput() {
      val output = listOf("hello", "word")
      val processOutput = ProcessOutput(output, listOf())
      assertThat(processOutput.hasErrors()).isFalse()
   }

   @Test
   fun shouldProcessJPackageWarningAsSuccessful() {
      val output = listOf("hello", "world")
      val errors = listOf("WARNING: Using incubator modules: jdk.incubator.jpackage")
      val processOutput = ProcessOutput(output, errors)
      assertThat(processOutput.hasErrors()).isFalse()
   }

   @Test
   fun shouldProcessNullAsSuccessful() {
      val output = listOf("hello")
      val errors = listOf("null")
      val processOutput = ProcessOutput(output, errors)
      assertThat(processOutput.hasErrors()).isFalse()
   }


   @Test
   fun shouldProcessWindowsDefenderAsSuccessful() {
      val output = listOf("hello")
      val errors = listOf("Blah Blah Windows Defender Blah Blah")
      val processOutput = ProcessOutput(output, errors)
      assertThat(processOutput.hasErrors()).isFalse()
   }

}

