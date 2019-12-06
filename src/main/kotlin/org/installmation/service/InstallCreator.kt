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
   fun createImage(project: InstallProject) {
      checkNotNull(project.imageBuildDirectory)
      checkNotNull(project.jpackageJDK)
      checkNotNull(project.mainJar)
      checkNotNull(project.javaFXLib?.path)

      configuration.eventBus.post(ClearMessagesEvent())
      progressMessage("Image creation started....")

      val output = doCreateImage(project)
      for (line in output)
         progressMessage(line)

      progressMessage("Image ${project.name + OperatingSystem.imageFileExtension()} created successfully in ${project.imageBuildDirectory!!.path}")
   }


   /**
    * Create complete installer
    */
   fun createInstaller(project: InstallProject) {
      checkNotNull(project.installerDirectory)
      checkNotNull(project.jpackageJDK)
      checkNotNull(project.mainJar)
      checkNotNull(project.javaFXLib?.path)

      configuration.eventBus.post(ClearMessagesEvent())
      progressMessage("Installer creation started....")

      // Step 1 create image as well
      var output = doCreateImage(project)
      for (line in output)
         progressMessage(line)

      progressMessage("*** APPLICATION IMAGE CREATED SUCCESSFULLY. STARTING INSTALLER CREATION ***")
      // Step 2 - create installer based on image created in Step 1

      output = doCreateInstaller(project)
      for (line in output)
         progressMessage(line)

      progressMessage("Installer creation completed successfully in ${project.installerDirectory!!.path}")
   }

   /**
    * Create Installer
    */
   private fun doCreateInstaller(project: InstallProject): List<String> {
      project.installerDirectory?.mkdirs()

      val packager = JPackageExecutable(project.jpackageJDK!!)
      packager.parameters.addArgument(ValueArgument("--package-type", "pkg"))
      packager.parameters.addArgument(ValueArgument("-d", project.installerDirectory!!.path))
      packager.parameters.addArgument(ValueArgument("-n", project.name))
      val appImage = File(project.imageBuildDirectory!!.path, project.name + OperatingSystem.imageFileExtension())
      packager.parameters.addArgument(ValueArgument("--app-image", appImage.path))
      return packager.execute(30)
   }

   /**
    * This is called by installer creation process and returns the command output
    */
   private fun doCreateImage(project: InstallProject): List<String> {
      // STEP 1 - make sure lib/ and main jar in imageContentDirectory
      createImageContent(project)

      // Step 2 - Generate Image in imageBuildDirectory
      progressMessage("Deleting old image content....")
      deleteDirectories(project.imageBuildDirectory!!)
      project.imageBuildDirectory!!.mkdir()

      val packager = JPackageExecutable(project.jpackageJDK!!)
      packager.parameters.addArgument(ValueArgument("--package-type", "app-image"))
      packager.parameters.addArgument(ValueArgument("-i", project.inputDirectory!!.path))
      packager.parameters.addArgument(ValueArgument("-d", project.imageBuildDirectory!!.path))
      packager.parameters.addArgument(ValueArgument("-n", project.name))

      val modulePathString = CollectionUtils.toPathList(project.modulePath.map { it.path })
      packager.parameters.addArgument(ValueArgument("--module-path", modulePathString))

      val modules = generateModuleDependencies(project)
      packager.parameters.addArgument(ValueArgument("--add-modules", modules))

      packager.parameters.addArgument(ValueArgument("--main-jar", project.mainJar?.name))
      packager.parameters.addArgument(ValueArgument("--main-class", project.mainClass))

      val fullCommand = packager.toString()
      log.info("command: $fullCommand")
      progressMessage("command: $fullCommand")
      return packager.execute(30)
   }

   /**
    * Run jdeps tool to get a list of JDK modules used by the target application
    */
   private fun generateModuleDependencies(project: InstallProject): String {
      val classPathString = CollectionUtils.toPathList(project.classPath.map { it.path })
      val jdeps = JDepsExecutable(project.jpackageJDK!!)
      val mm = ModuleDependenciesGenerator(jdeps, classPathString, project.javaFXLib?.path!!.path, project.mainJar?.path!!)
      return mm.generate().joinToString(",")
   }

   private fun createImageContent(p: InstallProject) {
      checkNotNull(p.inputDirectory)
      val destination = p.inputDirectory!!
      progressMessage("Creating all Image Content in ${destination.path}")
      deleteDirectories(destination)
      val libs = File(destination, "lib")
      libs.mkdirs()

      // main jar
      p.mainJar?.copyTo(File(destination, p.mainJar?.name), true)

      // classpath
      for (cp in p.classPath) {
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