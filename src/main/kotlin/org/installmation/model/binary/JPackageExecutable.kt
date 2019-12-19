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
      return ValueArgument("--package-type", "app-image")
   }

   fun createInstallerParameter(installerType:String): Argument {
      return ValueArgument("--package-type", installerType)
   }

    fun createImageBuildDirectory(buildDir: String, projectName:String):String{
      return when(OperatingSystem.os()){
         OperatingSystem.Type.OSX -> buildDir
         OperatingSystem.Type.Linux -> buildDir
         OperatingSystem.Type.Windows -> File(buildDir, projectName).path
      }
    }

   fun createDestinationParameter(dir: String): Argument {
      return ValueArgument("-d", dir)
   }

   /**
    * Creating installers has different os-specific values for the --app-image parameter
    */
   fun createInstallerAppImageParameter(projectName: String, imageBuildDir: String): Argument {
      return when (OperatingSystem.os()) {
         OperatingSystem.Type.OSX, OperatingSystem.Type.Linux -> {
            val imageDir = createImageBuildDirectory(imageBuildDir, projectName)
            val appImage = File(imageDir, projectName + OperatingSystem.imageFileExtension())
            ValueArgument("--app-image", appImage.path)
         }
         OperatingSystem.Type.Windows -> {
            val imageDir = createImageBuildDirectory(imageBuildDir, projectName)
            ValueArgument("--app-image", imageDir)
         }
      }
   }

   fun createMainClassParameter(mainClass: String): Argument {
      return ValueArgument("--main-class", mainClass)
   }
}