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

package org.installmation.model

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.TimeUnit

abstract class AbstractExecutable(version: String, private val parameters: ParameterList) : Executable {

   companion object {
      val log: Logger = LogManager.getLogger(AbstractExecutable::class.java)
   }

   override var version = version

   fun execute(timeoutSeconds: Long = 5L): List<String> {
      val fullCommand = mutableListOf<String>()
      fullCommand.add(executable)
      val params = parameters.toCommand()
      fullCommand.addAll(params)
      log.debug("Executing command '$executable' with parameters [${params}]")
      val proc = ProcessBuilder().directory(location).command(fullCommand).start()
      if (timeoutSeconds > -1)
         proc.waitFor(timeoutSeconds, TimeUnit.SECONDS)
      else
         proc.waitFor() //forever

      return proc.inputStream.bufferedReader().readLines()
   }

}