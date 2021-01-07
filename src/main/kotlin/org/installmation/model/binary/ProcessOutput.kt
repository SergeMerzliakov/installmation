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
package org.installmation.model.binary

/**
 * Output and Error Streams. Cannot trust success parameter for CLI commands
 */
class ProcessOutput(private val success: Boolean, val output: List<String> = emptyList(), private val errorOutput: List<String> = emptyList()) {

   /**
    * filter out false positives
    */
   fun errors(): List<String> {
      val falseErrors = Regex("WARNING|null|Windows Defender")
      return errorOutput.filter { !it.contains(falseErrors) }
   }

   /**
    * Some warnings are returned as errors. Sigh
    */
   fun success(): Boolean {
      if (!success || errors().isNotEmpty())
         return false

      return errors().isEmpty()
   }

}
