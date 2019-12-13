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
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_NEW_PROJECT)
      writeTextField(FXID.TEXT_SINGLEVAL_DLG_ITEM_VALUE, projectName)
      clickOn(FXID.BUTTON_SINGLEVAL_DLG_SAVE)
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_SAVE_PROJECT)

      // now check saved project exists
      val projectFile = File(File(BASE_CONFIG_DIR, Constant.PROJECT_DIR), "$projectName.json")
      assertThat(projectFile).exists()
      val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, projectFile, JsonParserFactory.configurationParser())
      val project = reader.load()
      
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
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_NEW_PROJECT)
      writeTextField(FXID.TEXT_SINGLEVAL_DLG_ITEM_VALUE, originalProjectName)
      clickOn(FXID.BUTTON_SINGLEVAL_DLG_SAVE)
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_SAVE_PROJECT)
      
      // project info properties
      clickOn(FXID.TAB_INFO)

      writeTextField(FXID.TEXT_PROJECT_NAME, projectName)
      writeTextField(FXID.TEXT_APP_VERSION, version)
      writeTextField(FXID.TEXT_COPYRIGHT, copyrightMessage)

      val installer = selectComboByIndex<String>(FXID.COMBO_INSTALLER_TYPE, 0)
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_SAVE_PROJECT)
      
      // now check saved project exists
      val projectFile = File(File(BASE_CONFIG_DIR, Constant.PROJECT_DIR), "$projectName.json")
      assertThat(projectFile).exists()
      val reader = ApplicationJsonReader<InstallProject>(InstallProject::class, projectFile, JsonParserFactory.configurationParser())
      val project = reader.load()

      assertThat(project.name).isEqualTo(projectName)
      assertThat(project.installerType).isEqualTo(installer) //OS specific default values
      assertThat(project.version).isEqualTo(version)
      assertThat(project.copyright).isEqualTo(copyrightMessage)

      assertThat(project.imageBuildDirectory).isNull()
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
}