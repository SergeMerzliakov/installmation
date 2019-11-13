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

import org.installmation.model.FlagArgument
import java.io.File

class JPackageExecutable(executable: File) : AbstractExecutable(executable) {
   
   override val id = "jpackage"


   override fun getVersion(): String {
      parameters.addArgument(FlagArgument("--version"))
      val output = execute()
      if (output.isEmpty())
         throw ExecutableException("No version info output from '${executable}'")
      if (output.size == 1)
         return output[0]
      return output[1]
   }
}