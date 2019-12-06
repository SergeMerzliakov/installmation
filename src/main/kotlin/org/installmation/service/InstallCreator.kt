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
package org.installmation.service

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.Configuration
import org.installmation.core.ClearMessagesEvent
import org.installmation.core.CollectionUtils
import org.installmation.core.UserMessageEvent
import org.installmation.model.InstallProject
import org.installmation.model.ValueArgument
import org.installmation.model.binary.JDepsExecutable
import org.installmation.model.binary.JPackageExecutable
import org.installmation.model.binary.ModuleDependenciesGenerator
import java.io.File

/**
 * Generates images and installers
 */
class InstallCreator(private val configuration: Configuration, private val project: InstallProject) {

   companion object {
      val log: Logger = LogManager.getLogger(InstallCreator::class.java)
   }

   /**
    * rm -rf ../../image-build     ==> temporary dir
   rm -rf ../../image-input     ==> temporary dir. put artefacts here
   cd ../..
   gradlew imageJar             ==> action create main jar file
   gradlew lib                  ==> copy dependencies here
   cd installer/mac
   export JPACKAGE=/Users/foo/tools/jpackage49/Contents/Home/bin/jpackage
   $JPACKAGE
   --package-type app-image
   -d ../../image-build
   -i ../../image-input
   -n demo1
   --module-path /Library/Java/javafx/13.0/jmods
   --add-modules java.base,javafx.controls,javafx.fxml,javafx.graphics
   --main-jar javafx-kotlin-demo-1.0.0.jar
   --main-class org.epistatic.kotlindemo.DemoApp
    */
   fun createImage() {
      checkNotNull(project.imageBuildDirectory)
      checkNotNull(project.jpackageJDK)
      checkNotNull(project.mainJar)

      configuration.eventBus.post(ClearMessagesEvent())
      progressMessage("Image creation started....")

      // STEP 1 - make sure lib/ and main jar in imageContentDirectory
      deleteDirectories(project.imageContentDirectory!!)
      
      // Step 2 - Generate Image in imageBuildDirectory
      deleteDirectories(project.imageBuildDirectory!!)
      project.imageBuildDirectory!!.mkdir()

      val packager = JPackageExecutable(project.jpackageJDK!!)
      packager.parameters.addArgument(ValueArgument("--package-type", "app-image"))
      packager.parameters.addArgument(ValueArgument("-i", project.imageContentDirectory!!.path))
      packager.parameters.addArgument(ValueArgument("-d", project.imageBuildDirectory!!.path))
      packager.parameters.addArgument(ValueArgument("-n", project.name))

      val modulePathString = CollectionUtils.toPathList(project.modulePath.map { it.path })
      val classPathString = CollectionUtils.toPathList(project.classPath.map { it.path })
      val jdeps = JDepsExecutable(project.jpackageJDK!!)
      val mm = ModuleDependenciesGenerator(jdeps, classPathString, modulePathString, project.mainJar?.path!!)
      val modules = mm.generate().joinToString()
      packager.parameters.addArgument(ValueArgument("--add-modules", modules))
      packager.parameters.addArgument(ValueArgument("--module-path", modulePathString))
      packager.parameters.addArgument(ValueArgument("--main-jar", project.mainJar?.name))
      packager.parameters.addArgument(ValueArgument("--main-class", project.mainClass))

      // log entire command for debugging
      val fullCommand = packager.toString()
      log.info("command: $fullCommand")
      progressMessage("command: $fullCommand")
      
      // pick a suitably long timeout, but don't wait forever
      val output = packager.execute(100)
      for (line in output)
         progressMessage(line)
      progressMessage("Image creation completed successfully in ${project.imageBuildDirectory!!.path}")
   }


   fun createInstaller() {
      configuration.eventBus.post(ClearMessagesEvent())
      progressMessage("Installer creation started....")
      // TODO
      progressMessage("Installer creation completed successfully")
   }


   private fun deleteDirectories(d: File) {
      progressMessage("Deleting old directories....")
      d.deleteRecursively()
   }

   private fun progressMessage(m: String) {
      configuration.eventBus.post(UserMessageEvent(m))
   }
}