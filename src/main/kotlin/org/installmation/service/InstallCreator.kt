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
import org.installmation.image.*
import org.installmation.io.FileFilters
import org.installmation.model.*
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
    fun createImage(prj: InstallProject): GenerateResult {
        checkNotNull(prj.imageBuildDirectory)
        checkNotNull(prj.jpackageJDK)
        checkNotNull(prj.mainJar)
        checkNotNull(prj.javaFXLib?.path)

        configuration.eventBus.post(ClearMessagesEvent())
        progressMessage("Image creation started....")

        val packager = initializeImagePackager(prj)
        val fullCommand = packager.toString()
        log.info("command: $fullCommand")
        progressMessage("command: $fullCommand")
        val processOutput = packager.execute(30)
        for (line in processOutput.output)
            progressMessage(line)

        val result: GenerateResult
        if (!processOutput.hasErrors()) {
            progressMessage("Image ${prj.name + OperatingSystem.imageFileExtension()} created successfully in ${prj.imageBuildDirectory!!.path}")
            result = GenerateResult(true)
        } else {
            progressErrorMessage("Image creation failed with errors:")
            result = GenerateResult(false, processOutput.errors)
            for (line in processOutput.errors) {
                log.error(line)
                progressErrorMessage(line)
            }
        }
        return result
    }

    // TODO error handling
    fun createImageScript(prj: InstallProject): ShellScript {
        val packager = initializeImagePackager(prj)
        val fullCommand = packager.toString()
        val script = ShellScriptFactory.createScript("generate_image")
        script.addLine(fullCommand)
        return script
    }

    fun createInstallerScript(prj: InstallProject): ShellScript {
        checkNotNull(prj.installerDirectory)
        checkNotNull(prj.jpackageJDK)
        checkNotNull(prj.mainJar)
        checkNotNull(prj.javaFXLib?.path)
        checkNotNull(prj.installerType)

        val packager = initializeInstallerPackager(prj)
        val fullCommand = packager.toString()
        val script = ShellScriptFactory.createScript("generate_installer")
        script.addLine(fullCommand)
        return script
    }

    /**
     * Create complete installer
     */
    fun createInstaller(prj: InstallProject): GenerateResult {
        checkNotNull(prj.installerDirectory)
        checkNotNull(prj.jpackageJDK)
        checkNotNull(prj.mainJar)
        checkNotNull(prj.javaFXLib?.path)
        checkNotNull(prj.installerType)

        configuration.eventBus.post(ClearMessagesEvent())
        progressMessage("Installer creation started....")

        // Step 1 create image as well
        val imageResult = createImage(prj)
        if (!imageResult.successful)
            return imageResult

        progressMessage("*** APPLICATION IMAGE CREATED SUCCESSFULLY. STARTING INSTALLER CREATION ***")

        // Step 2 - create installer based on image created in Step 1
        val packager = initializeInstallerPackager(prj)
        val fullCommand = packager.toString()
        log.info("command: $fullCommand")
        progressMessage("command: $fullCommand")
        val processOutput = packager.execute(30)
        for (line in processOutput.output)
            progressMessage(line)

        val result: GenerateResult
        if (!processOutput.hasErrors()) {
            progressMessage("Installer creation completed successfully in ${prj.installerDirectory!!.path}")
            result = GenerateResult(true)
        } else {
            progressMessage("Installer creation failed with errors:")
            result = GenerateResult(false, processOutput.errors)
            for (line in processOutput.errors) {
                log.error(line)
                progressErrorMessage(line)
            }
        }
        return result
    }

    /**
     * Create Installer
     */
    private fun initializeInstallerPackager(prj: InstallProject): JPackageExecutable {
        deleteDirectories(prj.installerDirectory)
        prj.installerDirectory?.mkdirs()

        val packager = JPackageExecutable(prj.jpackageJDK!!)
        packager.parameters.addArgument(packager.createInstallerParameter(prj.installerType!!))
        packager.parameters.addArgument(packager.createDestinationParameter(prj.installerDirectory!!.path))
        packager.parameters.addArgument(ValueArgument("--app-version", prj.version ?: "1.0"))
        packager.parameters.addArgument(ValueArgument("--copyright", prj.copyright ?: "Copyright 2019"))
        packager.parameters.addArgument(ValueArgument("-n", prj.name))
        packager.parameters.addArgument(packager.createInstallerAppImageParameter(prj.name!!, prj.imageBuildDirectory!!.path))
        return packager
    }

    /**
     * This is called by installer creation process and returns the command output
     */
    private fun initializeImagePackager(prj: InstallProject): JPackageExecutable {
        checkNotNull(prj.inputDirectory)
        checkNotNull(prj.mainJar)
        checkNotNull(prj.mainClass)

        // STEP 1 - make sure lib/ and main jar in imageContentDirectory
        createImageContent(prj)

        // Step 2 - Generate Image in imageBuildDirectory
        progressMessage("Deleting old image content....")
        deleteDirectories(prj.imageBuildDirectory)
        prj.imageBuildDirectory!!.mkdir()

        val packager = JPackageExecutable(prj.jpackageJDK!!)
        packager.parameters.addArgument(packager.createImageParameter())
        packager.parameters.addArgument(ValueArgument("-i", prj.inputDirectory!!.path))
        packager.parameters.addArgument(ValueArgument("--app-version", prj.version ?: "1.0"))
        packager.parameters.addArgument(ValueArgument("--copyright", prj.copyright ?: "Copyright 2019"))
        val logo = createApplicationIcon(prj.applicationLogo, prj.inputDirectory!!)
        if (logo != null)
            packager.parameters.addArgument(ValueArgument("--icon", logo.path))
        packager.parameters.addArgument(packager.createDestinationParameter(prj.imageBuildDirectory!!.path))
        packager.parameters.addArgument(ValueArgument("-n", prj.name))

        if (prj.javaFXMods?.path?.path != null) {
            packager.parameters.addArgument(ValueArgument("--module-path", prj.javaFXMods?.path?.path))
        }

        // check if modular application
        val modules = generateModuleDependencies(prj)
        if (modules.isNotEmpty())
            packager.parameters.addArgument(ValueArgument("--add-modules", modules))

        packager.parameters.addArgument(ValueArgument("--main-jar", prj.mainJar?.name))
        packager.parameters.addArgument(packager.createMainClassParameter(prj.mainClass!!))
        return packager
    }

    private fun createApplicationIcon(rawImage: File?, destination: File): File? {
        if (rawImage == null)
            return null

        return when (OperatingSystem.os()) {
            OperatingSystem.Type.OSX -> createOSXApplicationIcon(rawImage, destination)
            OperatingSystem.Type.Windows -> createWindowsApplicationIcon(rawImage, destination)
            OperatingSystem.Type.Linux -> createLinuxApplicationIcon(rawImage, destination)
        }
    }

    private fun createOSXApplicationIcon(rawImage: File, destination: File): File {
        if (rawImage.extension == ImageTool.ImageType.Icns.value)
            return rawImage.copyTo(File(destination, rawImage.name), true)
        val processor: ImageProcessor = OSXImageProcessor()
        return processor.createApplicationLogo(rawImage, destination)
    }

    private fun createWindowsApplicationIcon(rawImage: File, destination: File): File {
        if (rawImage.extension == ImageTool.ImageType.Ico.value)
            return rawImage.copyTo(File(destination, rawImage.name), true)
        val processor: ImageProcessor = WindowsImageProcessor()
        return processor.createApplicationLogo(rawImage, destination)
    }

    private fun createLinuxApplicationIcon(rawImage: File, destination: File): File {
        // TODO CHECK for image type like other OS
        val processor: ImageProcessor = LinuxImageProcessor()
        return processor.createApplicationLogo(rawImage, destination)
    }

    /**
     * Run jdeps tool to get a list of JDK modules used by the target application
     */
    private fun generateModuleDependencies(prj: InstallProject): String {
        if (prj.classPath.isEmpty())
            return ""

        checkNotNull(prj.jpackageJDK)
        checkNotNull(prj.javaFXLib)
        checkNotNull(prj.mainJar)

        val classPathString = CollectionUtils.toPathList(prj.classPath.map { it.path })
        val jdeps = JDepsExecutable(prj.jpackageJDK!!)
        val mm = ModuleDependenciesGenerator(jdeps, classPathString, prj.javaFXLib?.path!!, prj.mainJar?.path!!)

        // combine modules discovered plus custom modules specified by user
        val total = mutableSetOf<String>()
        total.addAll(prj.customModules)
        total.addAll(mm.generate())
        return total.joinToString(",")
    }

    private fun createImageContent(prj: InstallProject) {
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

    private fun deleteDirectories(d: File?) {
        if (d == null)
            return
        val res = d.deleteRecursively()
        log.debug("deleted ${d.path} - $res")
    }

    private fun progressMessage(m: String) {
        configuration.eventBus.post(UserMessageEvent(m))
    }

    private fun progressErrorMessage(m: String) {
        configuration.eventBus.post(UserMessageEvent(m, true))
    }
}