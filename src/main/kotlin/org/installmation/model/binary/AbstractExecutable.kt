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

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.model.ArgumentList
import org.installmation.model.FlagArgument
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

private val log: Logger = LogManager.getLogger(AbstractExecutable::class.java)


abstract class AbstractExecutable(val eventBus: EventBus, executable: File) : Executable {

   override val executable: File = executable
   val parameters = ArgumentList()

   init {
      eventBus.register(this)
   }

   /**
    * Batch up the command output before returning
    */
   fun execute(timeoutSeconds: Long = 5L): ProcessOutput {
      val fullCommand = buildCommand()
      val proc = ProcessBuilder().command(fullCommand).start()
      val success = proc.waitFor(timeoutSeconds, TimeUnit.SECONDS)
      return ProcessOutput(success, proc.inputStream.bufferedReader().readLines(), proc.errorStream.bufferedReader().readLines())
   }

   private fun buildCommand(): List<String> {
      val fullCommand = mutableListOf<String>()
      fullCommand.add(executable.path)
      val params = parameters.toCommand()
      fullCommand.addAll(params)
      log.debug("Executing command '$executable' with parameters [${params}]")
      return fullCommand
   }

   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is AbstractExecutable) return false

      if (executable != other.executable) return false

      return true
   }

   /**
    * Do not use version - this is a dynamic funcion
    */
   override fun hashCode(): Int {
      return executable.hashCode()
   }

   /**
    * query via command to executable
    */
   protected fun fetchVersion(versionFlag: String): String {
      parameters.addArgument(FlagArgument(versionFlag))
      val processOutput = execute(3)
      if (processOutput.output.isEmpty())
         throw ExecutableException("No version info output from '${executable}'")
      if (processOutput.output.size == 1)
         return processOutput.output[0]
      return processOutput.output[1]
   }

   override fun toString(): String {
      val buf = StringBuilder()
      buf.append(executable)
      for (p in parameters.toCommand())
         buf.append(' ').append(p)
      return buf.toString()
   }

   fun toShellString(): String {
      val buf = StringBuilder()
      buf.append(executable)
      for (p in parameters.toShellCommand())
         buf.append(' ').append(p)
      return buf.toString()
   }
}