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
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.installmation.InstallmationApplication
import org.installmation.configuration.Configuration
import org.installmation.configuration.Constant
import org.installmation.configuration.JsonParserFactory
import org.installmation.core.RunningAsTestEvent
import org.installmation.io.ApplicationJsonReader
import org.installmation.javafx.test.FXID
import org.installmation.javafx.test.JavaFxTest
import org.installmation.model.binary.JDK
import org.junit.AfterClass
import org.junit.Test
import java.io.File

/**
 * Ensure all dialogs are at least shown in button and menu clicks. No check on dialog contents
 */
class InstallProjectUITest : JavaFxTest() {

   private val application = InstallmationApplication()

   companion object {
      val BASE_CONFIG_DIR = File("testrun")

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
      assertThat(project.version).isEmpty()
      assertThat(project.copyright).isEmpty()
      assertThat(project.imageBuildDirectory).isNull()
      assertThat(project.installerDirectory).isNull()
      assertThat(project.inputDirectory).isNull()
      assertThat(project.installJDK).isNull()
      assertThat(project.mainClass).isNull()
      assertThat(project.mainJar).isNull()
      assertThat(project.classPath).isEmpty()
      assertThat(project.modulePath).isEmpty()
      assertThat(project.javaFXLib).isNull()
      assertThat(project.javaFXMods).isNull()
      assertThat(project.jpackageJDK).isNull()
   }

   @Test
   fun shouldCreateAndSaveProjectGeneralInfo() {
      val projectName = "info"
      val originalProjectName = "otherName"
      val version = "1"
      val copyrightMessage = "copyright"

      createProject(originalProjectName)

      // set single tab properties and save to disk
      clickOn(FXID.TAB_INFO)
      writeTextField(FXID.TEXT_PROJECT_NAME, projectName)
      writeTextField(FXID.TEXT_APP_VERSION, version)
      writeTextField(FXID.TEXT_COPYRIGHT, copyrightMessage)
      val installer = selectComboByIndex<String>(FXID.COMBO_INSTALLER_TYPE, 0)
      saveCurrentProject()

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isEqualTo(installer) //OS specific default values
      assertThat(project.version).isEqualTo(version)
      assertThat(project.copyright).isEqualTo(copyrightMessage)

      assertThat(project.imageBuildDirectory).isNull()
      assertThat(project.installerDirectory).isNull()
      assertThat(project.inputDirectory).isNull()
      assertThat(project.installJDK).isNull()
      assertThat(project.mainClass).isNull()
      assertThat(project.mainJar).isNull()
      assertThat(project.classPath).isEmpty()
      assertThat(project.modulePath).isEmpty()
      assertThat(project.javaFXLib).isNull()
      assertThat(project.javaFXMods).isNull()
      assertThat(project.jpackageJDK).isNull()
   }

   @Test
   fun shouldCreateAndSaveProjectInstallLocation() {
      val projectName = "locate"
      val inputDir = "/input"
      val imageBuildDir = "/image"
      val installerDir = "/installer"

      createProject(projectName)

      // set single tab properties and save to disk
      clickOn(FXID.TAB_INSTALL)
      writeTextField(FXID.TEXT_INPUT_DIR, inputDir)
      writeTextField(FXID.TEXT_IMAGE_BUILD_DIR, imageBuildDir)
      writeTextField(FXID.TEXT_INSTALLER_DIR, installerDir)
      saveCurrentProject()

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isNotEmpty() //OS specific default values
      assertThat(project.version).isNullOrEmpty()
      assertThat(project.copyright).isNullOrEmpty()

      assertThat(project.inputDirectory?.path).isEqualTo(inputDir)
      assertThat(project.imageBuildDirectory?.path).isEqualTo(imageBuildDir)
      assertThat(project.installerDirectory?.path).isEqualTo(installerDir)
      assertThat(project.installJDK).isNull()
      assertThat(project.mainClass).isNull()
      assertThat(project.mainJar).isNull()
      assertThat(project.classPath).isEmpty()
      assertThat(project.modulePath).isEmpty()
      assertThat(project.javaFXLib).isNull()
      assertThat(project.javaFXMods).isNull()
      assertThat(project.jpackageJDK).isNull()
   }

   @Test
   fun shouldCreateAndSaveProjectJavaBinaries() {
      val projectName = "binaries"
      val jpackageJDK = TestJDKFactory.create("package-jdk")
      val installJDK = TestJDKFactory.create("install-jdk")
      val moduleDir = NamedDirectory("module1", File("/module1"))
      val jmodDir = NamedDirectory("jmod1", File("/jmod1"))

      createProject(projectName)

      populateCombo(FXID.COMBO_JPACKAGE, jpackageJDK)
      populateCombo(FXID.COMBO_INSTALL_JDK, installJDK)
      populateCombo(FXID.COMBO_MODULE_LIB, moduleDir)
      populateCombo(FXID.COMBO_MODULE_JMOD, jmodDir)

      // set single tab properties and save to disk
      clickOn(FXID.TAB_BINARIES)
      selectComboByIndex<JDK>(FXID.COMBO_JPACKAGE, 0)
      selectComboByIndex<JDK>(FXID.COMBO_INSTALL_JDK, 0)
      selectComboByIndex<NamedDirectory>(FXID.COMBO_MODULE_LIB, 0)
      selectComboByIndex<NamedDirectory>(FXID.COMBO_MODULE_JMOD, 0)

      saveCurrentProject()

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isNotEmpty() //OS specific default values
      assertThat(project.version).isNullOrEmpty()
      assertThat(project.copyright).isNullOrEmpty()

      assertThat(project.installJDK).isEqualTo(installJDK)
      assertThat(project.jpackageJDK).isEqualTo(jpackageJDK)
      assertThat(project.javaFXLib).isEqualTo(moduleDir)
      assertThat(project.javaFXMods).isEqualTo(jmodDir)
      assertThat(project.modulePath).hasSize(1)

      assertThat(project.inputDirectory).isNull()
      assertThat(project.imageBuildDirectory).isNull()
      assertThat(project.installerDirectory).isNull()
      assertThat(project.mainClass).isNull()
      assertThat(project.mainJar).isNull()
      assertThat(project.classPath).isEmpty()
   }

   @Test
   fun shouldCreateAndSaveProjectApplicationExecutables() {
      val projectName = "exec"
      val mainJar = "/usr/main.jar"
      val mainClass = "org.Main"

      createProject(projectName)

      // set single tab properties and save to disk
      clickOn(FXID.TAB_EXECUTABLE)
      writeTextField(FXID.TEXT_MAIN_JAR, mainJar)
      writeTextField(FXID.TEXT_MAIN_CLASS, mainClass)

      saveCurrentProject()

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isNotEmpty() //OS specific default values
      assertThat(project.version).isNullOrEmpty()
      assertThat(project.copyright).isNullOrEmpty()

      assertThat(project.mainClass).isEqualTo(mainClass)
      assertThat(project.mainJar?.path).isEqualTo(mainJar)

      assertThat(project.installJDK).isNull()
      assertThat(project.jpackageJDK).isNull()
      assertThat(project.javaFXLib).isNull()
      assertThat(project.javaFXMods).isNull()
      assertThat(project.modulePath).isEmpty()

      assertThat(project.inputDirectory).isNull()
      assertThat(project.imageBuildDirectory).isNull()
      assertThat(project.installerDirectory).isNull()
      assertThat(project.classPath).isEmpty()
   }

   @Test
   fun shouldCreateAndSaveProjectDependencies() {
      val projectName = "deps"
      val classPathItem = "/usr/lib"

      createProject(projectName)

      // set single tab properties and save to disk
      clickOn(FXID.TAB_DEPENDENCIES)
      populateListView(FXID.LISTVIEW_CLASSPATH, classPathItem)

      saveCurrentProject()

      val project = loadCurrentProject(projectName)

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isNotEmpty() //OS specific default values
      assertThat(project.version).isNullOrEmpty()
      assertThat(project.copyright).isNullOrEmpty()

      assertThat(project.classPath).hasSize(1)
      assertThat(project.classPath).contains(File(classPathItem))

      assertThat(project.mainClass).isNull()
      assertThat(project.mainJar).isNull()

      assertThat(project.installJDK).isNull()
      assertThat(project.jpackageJDK).isNull()
      assertThat(project.javaFXLib).isNull()
      assertThat(project.javaFXMods).isNull()
      assertThat(project.modulePath).isEmpty()

      assertThat(project.inputDirectory).isNull()
      assertThat(project.imageBuildDirectory).isNull()
      assertThat(project.installerDirectory).isNull()
   }

   private fun saveCurrentProject() {
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_SAVE_PROJECT)
   }

   private fun createProject(projectName: String) {
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_NEW_PROJECT)
      writeTextField(FXID.TEXT_SINGLEVAL_DLG_ITEM_VALUE, projectName)
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