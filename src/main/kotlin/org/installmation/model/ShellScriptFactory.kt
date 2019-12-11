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

import org.installmation.core.OperatingSystem
import java.io.File

object ShellScriptFactory {

   fun createScript(name: String): ShellScript {
      val script = ShellScript()
      script.fileName = name + OperatingSystem.scriptExtension()

      when (OperatingSystem.os()) {
         OperatingSystem.Type.OSX -> script.addLine("#!/bin/bash")
         OperatingSystem.Type.Linux -> script.addLine("#!/bin/bash")
         OperatingSystem.Type.Windows -> script.addLine("rem generate images or installers")
      }
      
      return script
   }
}