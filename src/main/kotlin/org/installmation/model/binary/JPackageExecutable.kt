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

import org.installmation.core.OperatingSystem
import org.installmation.model.Argument
import org.installmation.model.FlagArgument
import org.installmation.model.ValueArgument
import java.io.File

/**
 * runs JDK jpackage command
 */
class JPackageExecutable(jdk: JDK) : AbstractExecutable(jdk.packageExecutable) {
   
   override val id = "jpackage"

   override fun queryVersion(): String {
      return fetchVersion("--version")
   }
   
   
   fun createImageParameter(): Argument {
      return when(OperatingSystem.os()){
         OperatingSystem.Type.OSX -> ValueArgument("--package-type", "app-image")
         OperatingSystem.Type.Linux -> ValueArgument("--package-type", "app-image")
         OperatingSystem.Type.Windows -> ValueArgument("--package-type", "app-image")
      }
   }

   fun createInstallerParameter(installerType:String): Argument {
      return when(OperatingSystem.os()){
         OperatingSystem.Type.OSX -> ValueArgument("--package-type", installerType)
         OperatingSystem.Type.Linux -> ValueArgument("--package-type", installerType)
         OperatingSystem.Type.Windows -> ValueArgument("--package-type", installerType)
      }
   }

    fun createImageBuildDirectory(buildDir: String, projectName:String):String{
      return when(OperatingSystem.os()){
         OperatingSystem.Type.OSX -> buildDir
         OperatingSystem.Type.Linux -> buildDir
         OperatingSystem.Type.Windows -> File(buildDir, projectName).path
      }
   }

   fun createOutputDirectoryParameter(dir:String): Argument {
      return when(OperatingSystem.os()){
         OperatingSystem.Type.OSX -> ValueArgument("-d", dir)
         OperatingSystem.Type.Linux -> ValueArgument("-d", dir)
         OperatingSystem.Type.Windows -> ValueArgument("-d", dir)
      }
   }
   
   fun createMainClassParameter(mainClass:String):Argument{
      return when(OperatingSystem.os()){
         OperatingSystem.Type.OSX -> ValueArgument("--main-class", mainClass)
         OperatingSystem.Type.Linux -> ValueArgument("--main-class", mainClass)
         OperatingSystem.Type.Windows -> ValueArgument("--main-class", mainClass)
      }
   }
}