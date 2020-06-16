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

import com.google.common.eventbus.EventBus
import javafx.scene.control.CheckBox
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.installmation.FXTest
import org.installmation.InstallmationApplication
import org.installmation.TestConstants
import org.installmation.configuration.Configuration
import org.installmation.configuration.JsonParserFactory
import org.installmation.core.OperatingSystem
import org.installmation.core.RunningAsTestEvent
import org.installmation.io.ApplicationJsonReader
import org.installmation.io.ApplicationJsonWriter
import org.installmation.javafx.test.*
import org.installmation.model.binary.JDK
import org.installmation.service.ProjectCreatedEvent
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File


private val BASE_CONFIG_DIR = File(TestConstants.TEST_TEMP_DIR)

/**
 * Ensure all dialogs are at least shown in button and menu clicks. No check on dialog contents
 */
class InstallProjectTest : FXTest() {

   private val application = InstallmationApplication()
   private val textHelper = TextInputHelper(this)
   private val comboHelper = ComboHelper(this)
   private val listHelper = ListViewHelper(this)
   private val bus = EventBus(InstallProjectTest::class.simpleName!!)

   override fun start(stage: Stage?) {
      BASE_CONFIG_DIR.mkdirs()
      super.start(stage)

      val configuration = Configuration(bus, BASE_CONFIG_DIR)
      application.startApplication(stage!!, configuration, bus)
      bus.post(RunningAsTestEvent())

   }


   @Before
   fun setup() {
   }

   @After
   fun cleanup() {
      BASE_CONFIG_DIR.deleteRecursively()
   }

   @Test
   fun shouldCreateAndSaveNewEmptyProject() {
      val version = "1.1"
      val projectName = "projectEmpty"
      val projectFile = File(BASE_CONFIG_DIR, "$projectName.json")
      val p = simulateExistingSavedProject(projectName, projectFile)
      FXBlock(Runnable {
         bus.post(ProjectCreatedEvent(p))
      }).run()

      val loadedProject = loadCurrentProject(projectFile)

      assertThat(loadedProject.name).isEqualTo(projectName)
      assertThat(loadedProject.installerType).isNullOrEmpty() //OS specific default values
      assertThat(loadedProject.version).isEqualTo(version)
      assertThat(loadedProject.copyright).isNullOrEmpty()
      assertThat(loadedProject.applicationLogo).isNull()
      assertThat(loadedProject.imageBuildDirectory).isNull()
      assertThat(loadedProject.installerDirectory).isNull()
      assertThat(loadedProject.inputDirectory).isNull()
      assertThat(loadedProject.installJDK).isNull()
      assertThat(loadedProject.mainClass).isNullOrEmpty()
      assertThat(loadedProject.mainJar).isNull()
      assertThat(loadedProject.classPath).isEmpty()
      assertThat(loadedProject.customModules).isEmpty()
      assertThat(loadedProject.javaFXLib).isNull()
      assertThat(loadedProject.javaFXMods).isNull()
      assertThat(loadedProject.jpackageJDK).isNull()
      assertThat(loadedProject.applicationLogo).isNull()
   }

   @Test
   fun shouldCreateAndSaveProjectGeneralInfo() {
      val projectName = "projectGeneralInfo"
      val projectFile = File(BASE_CONFIG_DIR, "$projectName.json")
      val p = simulateExistingSavedProject(projectName, projectFile)
      FXBlock(Runnable {
         bus.post(ProjectCreatedEvent(p))
      }).run()

      val version = "1"
      val copyrightMessage = "copyright"
      val vendor = "acme muscle pills"
      val logoImagePath = File("image", "logo.png").path //cross platform

      // set single tab properties and save to disk
      clickOn(FXID.TAB_INFO)
      textHelper.writeText(FXID.TEXT_PROJECT_NAME, projectName)
      textHelper.writeText(FXID.TEXT_APP_VERSION, version)
      textHelper.writeText(FXID.TEXT_COPYRIGHT, copyrightMessage)
      textHelper.writeText(FXID.TEXT_LOGO_PATH, logoImagePath)
      textHelper.writeText(FXID.TEXT_VENDOR, vendor)
      val installer = comboHelper.selectByIndex<String>(FXID.COMBO_INSTALLER_TYPE, 0)
      saveCurrentProject()

      val loadedProject = loadCurrentProject(projectFile)

      assertThat(loadedProject.name).isEqualTo(projectName)
      assertThat(loadedProject.installerType).isEqualTo(installer) //OS specific default values
      assertThat(loadedProject.version).isEqualTo(version)
      assertThat(loadedProject.vendor).isEqualTo(vendor)
      assertThat(loadedProject.copyright).isEqualTo(copyrightMessage)
      assertThat(loadedProject.applicationLogo?.path).isEqualTo(logoImagePath)

      assertThat(loadedProject.imageBuildDirectory).isNull()
      assertThat(loadedProject.installerDirectory).isNull()
      assertThat(loadedProject.inputDirectory).isNull()
      assertThat(loadedProject.installJDK).isNull()
      assertThat(loadedProject.mainClass).isNullOrEmpty()
      assertThat(loadedProject.mainJar).isNull()
      assertThat(loadedProject.classPath).isEmpty()
      assertThat(loadedProject.customModules).isEmpty()
      assertThat(loadedProject.javaFXLib).isNull()
      assertThat(loadedProject.javaFXMods).isNull()
      assertThat(loadedProject.jpackageJDK).isNull()
   }

   @Test
   fun shouldCreateAndSaveOSXInfo() {
      if (OperatingSystem.os() == OperatingSystem.Type.OSX) {
         val installerCert = "acme"
         val keychain = "/dir/my.keychain-db"
         val projectName = "projectOSX"
         val projectFile = File(BASE_CONFIG_DIR, "$projectName.json")

         val p = simulateExistingSavedProject(projectName, projectFile)

         FXBlock(Runnable {
            bus.post(ProjectCreatedEvent(p))
         }).run()

         clickOn(FXID.TAB_OSX)
         textHelper.writeText(FXID.TEXT_OSX_SIGN_INSTALL_CERT, installerCert)
         textHelper.writeText(FXID.TEXT_OSX_SIGN_KEYCHAIN, keychain)
         val signInstaller = lookup(FXID.CHECKBOX_OSX_SIGN).query<CheckBox>()
         signInstaller.isSelected = true

         saveCurrentProject()

         val loadedProject = loadCurrentProject(projectFile)

         assertThat(loadedProject.signInstaller).isTrue()
         assertThat(loadedProject.appleInstallerCertName).isEqualTo(installerCert)
         assertThat(loadedProject.appleInstallerKeyChain?.path).isEqualTo(keychain)
      }
   }

   @Test
   fun shouldCreateAndSaveProjectInstallLocation() {
      val projectName = "projectLocation"
      val inputDir = "input"
      val imageBuildDir = "image"
      val installerDir = "installer"
      val projectFile = File(BASE_CONFIG_DIR, "$projectName.json")

      val p = simulateExistingSavedProject(projectName, projectFile)

      FXBlock(Runnable {
         bus.post(ProjectCreatedEvent(p))
      }).run()

      // set single tab properties and save to disk
      clickOn(FXID.TAB_INSTALL)
      textHelper.writeText(FXID.TEXT_INPUT_DIR, inputDir)
      textHelper.writeText(FXID.TEXT_IMAGE_BUILD_DIR, imageBuildDir)
      textHelper.writeText(FXID.TEXT_INSTALLER_DIR, installerDir)
      saveCurrentProject()

      val loadedProject = loadCurrentProject(projectFile)

      assertThat(loadedProject.name).isEqualTo(projectName)
      assertThat(loadedProject.installerType).isNotEmpty() //OS specific default values
      assertThat(loadedProject.version).isNullOrEmpty()
      assertThat(loadedProject.copyright).isNullOrEmpty()
      assertThat(loadedProject.applicationLogo).isNull()

      assertThat(loadedProject.inputDirectory?.path).isEqualTo(inputDir)
      assertThat(loadedProject.imageBuildDirectory?.path).isEqualTo(imageBuildDir)
      assertThat(loadedProject.installerDirectory?.path).isEqualTo(installerDir)
      assertThat(loadedProject.installJDK).isNull()
      assertThat(loadedProject.mainClass).isNullOrEmpty()
      assertThat(loadedProject.mainJar).isNull()
      assertThat(loadedProject.classPath).isEmpty()
      assertThat(loadedProject.customModules).isEmpty()
      assertThat(loadedProject.javaFXLib).isNull()
      assertThat(loadedProject.javaFXMods).isNull()
      assertThat(loadedProject.jpackageJDK).isNull()
   }

   @Test
   fun shouldCreateAndSaveProjectJavaBinaries() {
      val projectName = "projectBinaries"
      val projectFile = File(BASE_CONFIG_DIR, "$projectName.json")
      val jpackageJDK = TestJDKFactory.create("package-jdk")
      val installJDK = TestJDKFactory.create("install-jdk")
      val moduleDir = NamedDirectory("module1", File("module1"))
      val jmodDir = NamedDirectory("jmod1", File("jmod1"))

      val p = simulateExistingSavedProject(projectName, projectFile)

      FXBlock(Runnable {
         bus.post(ProjectCreatedEvent(p))
      }).run()

      comboHelper.populateItems(FXID.COMBO_JPACKAGE, jpackageJDK)
      comboHelper.populateItems(FXID.COMBO_INSTALL_JDK, installJDK)
      comboHelper.populateItems(FXID.COMBO_MODULE_LIB, moduleDir)
      comboHelper.populateItems(FXID.COMBO_MODULE_JMOD, jmodDir)

      // set single tab properties and save to disk
      clickOn(FXID.TAB_BINARIES)
      comboHelper.selectByIndex<JDK>(FXID.COMBO_JPACKAGE, 0)
      comboHelper.selectByIndex<JDK>(FXID.COMBO_INSTALL_JDK, 0)
      comboHelper.selectByIndex<NamedDirectory>(FXID.COMBO_MODULE_LIB, 0)
      comboHelper.selectByIndex<NamedDirectory>(FXID.COMBO_MODULE_JMOD, 0)

      saveCurrentProject()

      val loadedProject = loadCurrentProject(projectFile)

      assertThat(loadedProject.name).isEqualTo(projectName)
      assertThat(loadedProject.installerType).isNotEmpty() //OS specific default values
      assertThat(loadedProject.version).isNullOrEmpty()
      assertThat(loadedProject.copyright).isNullOrEmpty()
      assertThat(loadedProject.applicationLogo).isNull()

      assertThat(loadedProject.installJDK).isEqualTo(installJDK)
      assertThat(loadedProject.jpackageJDK).isEqualTo(jpackageJDK)
      assertThat(loadedProject.javaFXLib).isEqualTo(moduleDir)
      assertThat(loadedProject.javaFXMods).isEqualTo(jmodDir)
      assertThat(loadedProject.customModules).isEmpty()

      assertThat(loadedProject.inputDirectory).isNull()
      assertThat(loadedProject.imageBuildDirectory).isNull()
      assertThat(loadedProject.installerDirectory).isNull()
      assertThat(loadedProject.mainClass).isNullOrEmpty()
      assertThat(loadedProject.mainJar).isNull()
      assertThat(loadedProject.classPath).isEmpty()
   }

   @Test
   fun shouldCreateAndSaveProjectApplicationExecutables() {
      val mainJar = File("usr", "main.jar").path
      val mainClass = "org.Main"
      val projectName = "projectAppExecutables"
      val projectFile = File(BASE_CONFIG_DIR, "$projectName.json")

      val p = simulateExistingSavedProject(projectName, projectFile)

      FXBlock(Runnable {
         bus.post(ProjectCreatedEvent(p))
      }).run()

      // set single tab properties and save to disk
      clickOn(FXID.TAB_EXECUTABLE)
      textHelper.writeText(FXID.TEXT_MAIN_JAR, mainJar)
      textHelper.writeText(FXID.TEXT_MAIN_CLASS, mainClass)

      saveCurrentProject()

      val loadedProject = loadCurrentProject(projectFile)

      assertThat(loadedProject.name).isEqualTo(projectName)
      assertThat(loadedProject.installerType).isNotEmpty() //OS specific default values
      assertThat(loadedProject.version).isNullOrEmpty()
      assertThat(loadedProject.copyright).isNullOrEmpty()
      assertThat(loadedProject.applicationLogo).isNull()
      
      assertThat(loadedProject.mainClass).isEqualTo(mainClass)
      assertThat(loadedProject.mainJar?.path).isEqualTo(mainJar)

      assertThat(loadedProject.installJDK).isNull()
      assertThat(loadedProject.jpackageJDK).isNull()
      assertThat(loadedProject.javaFXLib).isNull()
      assertThat(loadedProject.javaFXMods).isNull()
      assertThat(loadedProject.customModules).isEmpty()

      assertThat(loadedProject.inputDirectory).isNull()
      assertThat(loadedProject.imageBuildDirectory).isNull()
      assertThat(loadedProject.installerDirectory).isNull()
      assertThat(loadedProject.classPath).isEmpty()
   }

   @Test
   fun shouldCreateAndSaveProjectDependencies() {
      val classPathItem = File("usr", "lib").path
      val projectName = "projectAppExecutables"
      val projectFile = File(BASE_CONFIG_DIR, "$projectName.json")

      val p = simulateExistingSavedProject(projectName, projectFile)

      FXBlock(Runnable {
         bus.post(ProjectCreatedEvent(p))
      }).run()

      // set single tab properties and save to disk
      clickOn(FXID.TAB_DEPENDENCIES)
      listHelper.populateItems(FXID.LISTVIEW_CLASSPATH, classPathItem)

      saveCurrentProject()

      val loadedProject = loadCurrentProject(projectFile)

      assertThat(loadedProject.name).isEqualTo(projectName)
      assertThat(loadedProject.installerType).isNotEmpty() //OS specific default values
      assertThat(loadedProject.version).isNullOrEmpty()
      assertThat(loadedProject.copyright).isNullOrEmpty()
      assertThat(loadedProject.applicationLogo).isNull()

      assertThat(loadedProject.classPath).hasSize(1)
      assertThat(loadedProject.classPath).contains(File(classPathItem))

      assertThat(loadedProject.mainClass).isNullOrEmpty()
      assertThat(loadedProject.mainJar).isNull()

      assertThat(loadedProject.installJDK).isNull()
      assertThat(loadedProject.jpackageJDK).isNull()
      assertThat(loadedProject.javaFXLib).isNull()
      assertThat(loadedProject.javaFXMods).isNull()
      assertThat(loadedProject.customModules).isEmpty()

      assertThat(loadedProject.inputDirectory).isNull()
      assertThat(loadedProject.imageBuildDirectory).isNull()
      assertThat(loadedProject.installerDirectory).isNull()
   }

   private fun saveCurrentProject() {
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_SAVE_PROJECT)
   }

   private fun simulateExistingSavedProject(projectName: String, savedProject: File): InstallProject {
      val p = InstallProject(projectName)
      application.workspace.setCurrentProject(p)
      application.workspace.projectHistory[p.name!!] = savedProject
      val writer = ApplicationJsonWriter<InstallProject>(savedProject, JsonParserFactory.configurationParser())
      writer.save(p)
      return p
   }

   private fun loadCurrentProject(savedProject: File): InstallProject {
      assertThat(savedProject).exists()
      val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, savedProject, JsonParserFactory.configurationParser())
      return reader.load()
   }
}