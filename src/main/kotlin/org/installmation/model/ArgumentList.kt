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

class ArgumentList {
   private val arguments = mutableMapOf<String, Argument>()


   fun clear() = arguments.clear()

   /**
    * overwrites previous value
    */
   fun addArgument(arg: Argument) {
      arguments[arg.name] = arg
   }

   /**
    * overwrites previous value if not empty
    */
   fun addArgument(arg: ValueArgument) {
      if (arg.value.isNotEmpty())
         arguments[arg.name] = arg
   }

   /**
    * for use in shell script execution
    * each flag and its value are distinct item in the list
    */
   fun toCommand(): List<String> {
      val command = mutableListOf<String>()

      for (a in arguments) {
         command.addAll(a.value.toCommand())
      }
      return command
   }

   /**
    * for use in shell script execution
    * each flag and its value are distinct item in the list
    */
   fun toShellCommand(): List<String> {
      val command = mutableListOf<String>()

      for (a in arguments) {
         command.addAll(a.value.toShellCommand())
      }
      return command
   }
}