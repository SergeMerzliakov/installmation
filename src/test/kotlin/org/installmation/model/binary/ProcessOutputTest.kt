/*
 * Copyright 2020 Serge Merzliakov
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

package org.installmation.model.binary

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ProcessOutputTest {


   @Test
   fun shouldProcessSuccessfulOutput() {
      val output = listOf("hello", "word")
      val processOutput = ProcessOutput(true, output, listOf())
      assertThat(processOutput.errors()).isEmpty()
   }

   @Test
   fun shouldProcessFailedOutput() {
      val output = listOf("hello", "world")
      val errors = emptyList<String>()
      val processOutput = ProcessOutput(false, output, errors)
      assertThat(processOutput.errors()).isEmpty()
   }

   /**
    * For some processes which return no error code, just error
    * output
    */
   @Test
   fun shouldProcessFailedOutputDespiteSuccessFlag() {
      val output = listOf("hello", "world")
      val errors = listOf("error1", "error2")
      val processOutput = ProcessOutput(true, output, errors)
      assertThat(processOutput.errors()).isNotEmpty().hasSize(2)
   }

   @Test
   fun shouldProcessJPackageWarningAsSuccessful() {
      val output = listOf("hello", "world")
      val errors = listOf("WARNING: Using incubator modules: jdk.incubator.jpackage")
      val processOutput = ProcessOutput(true, output, errors)
      assertThat(processOutput.errors()).isEmpty()
   }

   @Test
   fun shouldProcessNullAsSuccessful() {
      val output = listOf("hello")
      val errors = listOf("null")
      val processOutput = ProcessOutput(true, output, errors)
      assertThat(processOutput.errors()).isEmpty()
   }

   @Test
   fun shouldProcessWindowsDefenderAsSuccessful() {
      val output = listOf("hello")
      val errors = listOf("Blah Blah Windows Defender Blah Blah")
      val processOutput = ProcessOutput(true, output, errors)
      assertThat(processOutput.errors()).isEmpty()
   }
}

