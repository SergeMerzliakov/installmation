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
import org.installmation.InstallmationApplication
import org.installmation.TestConstants
import org.installmation.configuration.Configuration
import org.installmation.configuration.Constant
import org.installmation.configuration.JsonParserFactory
import org.installmation.core.OperatingSystem
import org.installmation.core.RunningAsTestEvent
import org.installmation.io.ApplicationJsonReader
import org.installmation.javafx.test.ComboHelper
import org.installmation.javafx.test.FXID
import org.installmation.javafx.test.ListViewHelper
import org.installmation.javafx.test.TextInputHelper
import org.installmation.model.binary.JDK
import org.junit.AfterClass
import org.junit.Test
import org.testfx.framework.junit.ApplicationTest
import java.io.File

/**
 * Ensure all dialogs are at least shown in button and menu clicks. No check on dialog contents
 */
class InstallProjectUITest : ApplicationTest() {

   private val application = InstallmationApplication()
   private val textHelper = TextInputHelper(this)
   private val comboHelper = ComboHelper(this)
   private val listHelper = ListViewHelper(this)

   companion object {
      val BASE_CONFIG_DIR = File(TestConstants.TEST_TEMP_DIR)

      @AfterClass
      @JvmStatic
      fun teardown() {
         BASE_CONFIG_DIR.deleteRecursively()
      }
   }

   override fun start(stage: Stage?) {
      BASE_CONFIG_DIR.mkdirs()
      super.start(stage)
      val bus = EventBus()
      val configuration = Configuration(bus, BASE_CONFIG_DIR)
      application.startApplication(stage!!, configuration, bus)
      bus.post(RunningAsTestEvent())
   }

   @Test
   fun shouldCreateAndSaveNewEmptyProject() {
      val projectName = "project1"
      createProject(projectName)

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isNotEmpty() //OS specific default values
      assertThat(project.version).isNullOrEmpty()
      assertThat(project.copyright).isNullOrEmpty()
      assertThat(project.applicationLogo).isNull()
      assertThat(project.imageBuildDirectory).isNull()
      assertThat(project.installerDirectory).isNull()
      assertThat(project.inputDirectory).isNull()
      assertThat(project.installJDK).isNull()
      assertThat(project.mainClass).isNullOrEmpty()
      assertThat(project.mainJar).isNull()
      assertThat(project.classPath).isEmpty()
      assertThat(project.customModules).isEmpty()
      assertThat(project.javaFXLib).isNull()
      assertThat(project.javaFXMods).isNull()
      assertThat(project.jpackageJDK).isNull()
      assertThat(project.applicationLogo).isNull()
   }

   @Test
   fun shouldCreateAndSaveProjectGeneralInfo() {
      val projectName = "info"
      val originalProjectName = "otherName"
      val version = "1"
      val copyrightMessage = "copyright"
      val vendor = "acme muscle pills"
      val logoImagePath = File("image","logo.png").path //cross platform

      createProject(originalProjectName)

      // set single tab properties and save to disk
      clickOn(FXID.TAB_INFO)
      textHelper.writeText(FXID.TEXT_PROJECT_NAME, projectName)
      textHelper.writeText(FXID.TEXT_APP_VERSION, version)
      textHelper.writeText(FXID.TEXT_COPYRIGHT, copyrightMessage)
      textHelper.writeText(FXID.TEXT_LOGO_PATH, logoImagePath)
      textHelper.writeText(FXID.TEXT_VENDOR, vendor)
      val installer = comboHelper.selectByIndex<String>(FXID.COMBO_INSTALLER_TYPE, 0)
      saveCurrentProject()

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isEqualTo(installer) //OS specific default values
      assertThat(project.version).isEqualTo(version)
      assertThat(project.vendor).isEqualTo(vendor)
      assertThat(project.copyright).isEqualTo(copyrightMessage)
      assertThat(project.applicationLogo?.path).isEqualTo(logoImagePath)

      assertThat(project.imageBuildDirectory).isNull()
      assertThat(project.installerDirectory).isNull()
      assertThat(project.inputDirectory).isNull()
      assertThat(project.installJDK).isNull()
      assertThat(project.mainClass).isNullOrEmpty()
      assertThat(project.mainJar).isNull()
      assertThat(project.classPath).isEmpty()
      assertThat(project.customModules).isEmpty()
      assertThat(project.javaFXLib).isNull()
      assertThat(project.javaFXMods).isNull()
      assertThat(project.jpackageJDK).isNull()
   }

   @Test
   fun shouldCreateAndSaveOSXInfo() {
      if (OperatingSystem.os() == OperatingSystem.Type.OSX) {
         val projectName = "project1"
         val installerCert = "acme"
         val keychain = "/dir/my.keychain-db"

         createProject(projectName)
         
         clickOn(FXID.TAB_OSX)
         textHelper.writeText(FXID.TEXT_OSX_SIGN_USER,installerCert)
         textHelper.writeText(FXID.TEXT_OSX_SIGN_KEYCHAIN, keychain)
         val signInstaller = lookup(FXID.CHECKBOX_OSX_SIGN).query<CheckBox>()
         signInstaller.isSelected = true

         saveCurrentProject()

         val project = loadCurrentProject(projectName)

         assertThat(project.signInstaller).isTrue()
         assertThat(project.appleInstallerCertName).isEqualTo(installerCert)
         assertThat(project.appleInstallerKeyChain?.path).isEqualTo(keychain)
      }
   }

   @Test
   fun shouldCreateAndSaveProjectInstallLocation() {
      val projectName = "locate"
      val inputDir = "input"
      val imageBuildDir = "image"
      val installerDir = "installer"

      createProject(projectName)

      // set single tab properties and save to disk
      clickOn(FXID.TAB_INSTALL)
      textHelper.writeText(FXID.TEXT_INPUT_DIR, inputDir)
      textHelper.writeText(FXID.TEXT_IMAGE_BUILD_DIR, imageBuildDir)
      textHelper.writeText(FXID.TEXT_INSTALLER_DIR, installerDir)
      saveCurrentProject()

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isNotEmpty() //OS specific default values
      assertThat(project.version).isNullOrEmpty()
      assertThat(project.copyright).isNullOrEmpty()
      assertThat(project.applicationLogo).isNull()
      
      assertThat(project.inputDirectory?.path).isEqualTo(inputDir)
      assertThat(project.imageBuildDirectory?.path).isEqualTo(imageBuildDir)
      assertThat(project.installerDirectory?.path).isEqualTo(installerDir)
      assertThat(project.installJDK).isNull()
      assertThat(project.mainClass).isNullOrEmpty()
      assertThat(project.mainJar).isNull()
      assertThat(project.classPath).isEmpty()
      assertThat(project.customModules).isEmpty()
      assertThat(project.javaFXLib).isNull()
      assertThat(project.javaFXMods).isNull()
      assertThat(project.jpackageJDK).isNull()
   }

   @Test
   fun shouldCreateAndSaveProjectJavaBinaries() {
      val projectName = "binaries"
      val jpackageJDK = TestJDKFactory.create("package-jdk")
      val installJDK = TestJDKFactory.create("install-jdk")
      val moduleDir = NamedDirectory("module1", File("module1"))
      val jmodDir = NamedDirectory("jmod1", File("jmod1"))

      createProject(projectName)

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

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isNotEmpty() //OS specific default values
      assertThat(project.version).isNullOrEmpty()
      assertThat(project.copyright).isNullOrEmpty()
      assertThat(project.applicationLogo).isNull()
      
      assertThat(project.installJDK).isEqualTo(installJDK)
      assertThat(project.jpackageJDK).isEqualTo(jpackageJDK)
      assertThat(project.javaFXLib).isEqualTo(moduleDir)
      assertThat(project.javaFXMods).isEqualTo(jmodDir)
      assertThat(project.customModules).isEmpty()

      assertThat(project.inputDirectory).isNull()
      assertThat(project.imageBuildDirectory).isNull()
      assertThat(project.installerDirectory).isNull()
      assertThat(project.mainClass).isNullOrEmpty()
      assertThat(project.mainJar).isNull()
      assertThat(project.classPath).isEmpty()
   }

   @Test
   fun shouldCreateAndSaveProjectApplicationExecutables() {
      val projectName = "exec"
      val mainJar = File("usr", "main.jar").path
      val mainClass = "org.Main"

      createProject(projectName)

      // set single tab properties and save to disk
      clickOn(FXID.TAB_EXECUTABLE)
      textHelper.writeText(FXID.TEXT_MAIN_JAR, mainJar)
      textHelper.writeText(FXID.TEXT_MAIN_CLASS, mainClass)

      saveCurrentProject()

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isNotEmpty() //OS specific default values
      assertThat(project.version).isNullOrEmpty()
      assertThat(project.copyright).isNullOrEmpty()
      assertThat(project.applicationLogo).isNull()
      
      assertThat(project.mainClass).isEqualTo(mainClass)
      assertThat(project.mainJar?.path).isEqualTo(mainJar)

      assertThat(project.installJDK).isNull()
      assertThat(project.jpackageJDK).isNull()
      assertThat(project.javaFXLib).isNull()
      assertThat(project.javaFXMods).isNull()
      assertThat(project.customModules).isEmpty()

      assertThat(project.inputDirectory).isNull()
      assertThat(project.imageBuildDirectory).isNull()
      assertThat(project.installerDirectory).isNull()
      assertThat(project.classPath).isEmpty()
   }

   @Test
   fun shouldCreateAndSaveProjectDependencies() {
      val projectName = "deps"
      val classPathItem = File("usr", "lib").path

      createProject(projectName)

      // set single tab properties and save to disk
      clickOn(FXID.TAB_DEPENDENCIES)
      listHelper.populateItems(FXID.LISTVIEW_CLASSPATH, classPathItem)

      saveCurrentProject()

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isNotEmpty() //OS specific default values
      assertThat(project.version).isNullOrEmpty()
      assertThat(project.copyright).isNullOrEmpty()
      assertThat(project.applicationLogo).isNull()

      assertThat(project.classPath).hasSize(1)
      assertThat(project.classPath).contains(File(classPathItem))

      assertThat(project.mainClass).isNullOrEmpty()
      assertThat(project.mainJar).isNull()

      assertThat(project.installJDK).isNull()
      assertThat(project.jpackageJDK).isNull()
      assertThat(project.javaFXLib).isNull()
      assertThat(project.javaFXMods).isNull()
      assertThat(project.customModules).isEmpty()

      assertThat(project.inputDirectory).isNull()
      assertThat(project.imageBuildDirectory).isNull()
      assertThat(project.installerDirectory).isNull()
   }

   private fun saveCurrentProject() {
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_SAVE_PROJECT)
   }

   private fun createProject(projectName: String) {
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_NEW_PROJECT)
      textHelper.writeText(FXID.TEXT_SINGLEVAL_DLG_ITEM_VALUE, projectName)
      clickOn(FXID.BUTTON_SINGLEVAL_DLG_SAVE)
      saveCurrentProject()
   }

   private fun loadCurrentProject(projectName: String): InstallProject {
      val projectFile = File(File(BASE_CONFIG_DIR, Constant.PROJECT_DIR), "$projectName.json")
      assertThat(projectFile).exists()
      val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, projectFile, JsonParserFactory.configurationParser())
      return reader.load()
   }
}