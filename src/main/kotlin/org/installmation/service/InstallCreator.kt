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
import org.installmation.core.OperatingSystem
import org.installmation.core.UserMessageEvent
import org.installmation.io.FileFilters
import org.installmation.model.InstallProject
import org.installmation.model.ValueArgument
import org.installmation.model.binary.JDepsExecutable
import org.installmation.model.binary.JPackageExecutable
import org.installmation.model.binary.ModuleDependenciesGenerator
import java.io.File

/**
 * Generates images and installers
 */
class InstallCreator(private val configuration: Configuration) {

   companion object {
      val log: Logger = LogManager.getLogger(InstallCreator::class.java)
   }

   /**
    * Create image (.exe or .app file), but not the installer
    */
   fun createImage(prj: InstallProject) {
      checkNotNull(prj.imageBuildDirectory)
      checkNotNull(prj.jpackageJDK)
      checkNotNull(prj.mainJar)
      checkNotNull(prj.javaFXLib?.path)

      configuration.eventBus.post(ClearMessagesEvent())
      progressMessage("Image creation started....")

      val output = doCreateImage(prj)
      for (line in output)
         progressMessage(line)

      progressMessage("Image ${prj.name + OperatingSystem.imageFileExtension()} created successfully in ${prj.imageBuildDirectory!!.path}")
   }


   /**
    * Create complete installer
    */
   fun createInstaller(prj: InstallProject) {
      checkNotNull(prj.installerDirectory)
      checkNotNull(prj.jpackageJDK)
      checkNotNull(prj.mainJar)
      checkNotNull(prj.javaFXLib?.path)

      configuration.eventBus.post(ClearMessagesEvent())
      progressMessage("Installer creation started....")

      // Step 1 create image as well
      var output = doCreateImage(prj)
      for (line in output)
         progressMessage(line)

      progressMessage("*** APPLICATION IMAGE CREATED SUCCESSFULLY. STARTING INSTALLER CREATION ***")
      // Step 2 - create installer based on image created in Step 1

      output = doCreateInstaller(prj)
      for (line in output)
         progressMessage(line)

      progressMessage("Installer creation completed successfully in ${prj.installerDirectory!!.path}")
   }

   /**
    * Create Installer
    */
   private fun doCreateInstaller(prj: InstallProject): List<String> {
      prj.installerDirectory?.mkdirs()

      val packager = JPackageExecutable(prj.jpackageJDK!!)
      packager.parameters.addArgument(ValueArgument("--package-type", prj.installerType))
      packager.parameters.addArgument(ValueArgument("-d", prj.installerDirectory!!.path))
      packager.parameters.addArgument(ValueArgument("-n", prj.name))
      val appImage = File(prj.imageBuildDirectory!!.path, prj.name + OperatingSystem.imageFileExtension())
      packager.parameters.addArgument(ValueArgument("--app-image", appImage.path))
      val fullCommand = packager.toString()
      log.info("command: $fullCommand")
      progressMessage("command: $fullCommand")
      return packager.execute(30)
   }

   /**
    * This is called by installer creation process and returns the command output
    */
   private fun doCreateImage(prj: InstallProject): List<String> {
      // STEP 1 - make sure lib/ and main jar in imageContentDirectory
      createImageContent(prj)

      // Step 2 - Generate Image in imageBuildDirectory
      progressMessage("Deleting old image content....")
      deleteDirectories(prj.imageBuildDirectory!!)
      prj.imageBuildDirectory!!.mkdir()

      val packager = JPackageExecutable(prj.jpackageJDK!!)
      packager.parameters.addArgument(ValueArgument("--package-type", "app-image"))
      packager.parameters.addArgument(ValueArgument("-i", prj.inputDirectory!!.path))
      packager.parameters.addArgument(ValueArgument("-d", prj.imageBuildDirectory!!.path))
      packager.parameters.addArgument(ValueArgument("-n", prj.name))

      val modulePathString = CollectionUtils.toPathList(prj.modulePath.map { it.path })
      packager.parameters.addArgument(ValueArgument("--module-path", modulePathString))

      val modules = generateModuleDependencies(prj)
      packager.parameters.addArgument(ValueArgument("--add-modules", modules))

      packager.parameters.addArgument(ValueArgument("--main-jar", prj.mainJar?.name))
      packager.parameters.addArgument(ValueArgument("--main-class", prj.mainClass))

      val fullCommand = packager.toString()
      log.info("command: $fullCommand")
      progressMessage("command: $fullCommand")
      return packager.execute(30)
   }

   /**
    * Run jdeps tool to get a list of JDK modules used by the target application
    */
   private fun generateModuleDependencies(prj: InstallProject): String {
      checkNotNull(prj.jpackageJDK)
      checkNotNull(prj.javaFXLib)
      checkNotNull(prj.mainJar)

      val classPathString = CollectionUtils.toPathList(prj.classPath.map { it.path })
      val jdeps = JDepsExecutable(prj.jpackageJDK!!)
      val mm = ModuleDependenciesGenerator(jdeps, classPathString, prj.javaFXLib?.path!!, prj.mainJar?.path!!)
      return mm.generate().joinToString(",")
   }

   private fun createImageContent(prj: InstallProject) {
      checkNotNull(prj.inputDirectory)
      checkNotNull(prj.mainJar)

      val destination = prj.inputDirectory!!
      progressMessage("Creating all Image Content in ${destination.path}")
      deleteDirectories(destination)
      val libs = File(destination, "lib")
      libs.mkdirs()

      // main jar
      prj.mainJar?.copyTo(File(destination, prj.mainJar!!.name), true)

      // classpath
      for (cp in prj.classPath) {
         val jarFiles = cp.listFiles(FileFilters.jarFileFilter)
         if (jarFiles != null) {
            for (jar in jarFiles)
               jar.copyTo(File(libs, jar.name), true)
         }
      }
   }

   private fun deleteDirectories(d: File) {
      d.deleteRecursively()
   }

   private fun progressMessage(m: String) {
      configuration.eventBus.post(UserMessageEvent(m))
   }
}