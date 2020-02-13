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

/**
 * Argument with a value to a command line executable
 * 
 *  short is something like -cp lib/mylib.jar
 *  long is something like --classpath lib/mylib.jar
 */
class ValueArgument(short: String, value: String? = null) : Argument {

   override var name: String = short
   var value: String = value ?: ""

   /**
    * Used in building shell commands
    */
   override fun toCommand(): List<String> {
      return listOf(name, value)
   }

   /**
    * Quote parameters with spaces
    */
   override fun toShellCommand(): List<String> {
      val spaceIdx = value.indexOf(' ')
      if (spaceIdx > 0 && spaceIdx < value.length - 1)
         value = "\"$value\""
      return listOf(name, value)
   }
}
