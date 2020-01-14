/*
 * Copyright 2020 Serge Merzliakov
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
package org.installmation.application

import com.google.common.eventbus.EventBus
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.installmation.InstallmationApplication
import org.installmation.configuration.Configuration
import org.installmation.configuration.JsonParserFactory
import org.installmation.core.RunningAsTestEvent
import org.installmation.javafx.test.FXID
import org.installmation.javafx.test.MockHelper
import org.installmation.model.InstallProject
import org.installmation.model.NamedDirectory
import org.installmation.model.binary.JDK
import org.junit.Test
import org.testfx.api.FxAssert
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.base.NodeMatchers
import java.io.File
import java.io.FileReader

/**
 * Test to ensure validations pick up all invalid fields before
 * image or installer generation
 */
class LoadProjectTest : ApplicationTest() {

   private val application = InstallmationApplication()
   override fun start(stage: Stage?) {
      super.start(stage)
      val bus = EventBus()
      val configuration = Configuration(bus, File("."))
      application.startApplication(stage!!, configuration, bus)
      bus.post(RunningAsTestEvent())
   }

   @Test
   fun shouldLoadProject() {
      // load project from JSON to check data in UI
      val gson = JsonParserFactory.configurationParser()
      val project: InstallProject = gson.fromJson(FileReader("src/test/resources/projects/project3.json"), InstallProject::class.java)
      MockHelper.mockChooseFileDialog("src/test/resources/projects/project3.json")
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_OPEN_PROJECT)

      // check values in UI
      validateInfoTab(project)
      validateInstallTab(project)
      validateBinariesTab(project)
      validateExecutableTab(project)
      validateDependenciesTab(project)
   }

   @Test
   fun shouldDetectNoProject() {
      clickOn(FXID.TAB_INFO)
      val generateButton = lookup(FXID.TOOLBAR_BUTTON_GENERATE_IMAGE).queryButton()
      clickOn(generateButton)

      val dialog = targetWindow("Cannot Generate Image")
      val ok = dialog.lookup("OK").queryButton()
      clickOn(ok)
   }

   @Test
   fun shouldDetectMissingProjectName() {
      // load project from JSON
      MockHelper.mockChooseFileDialog("src/test/resources/projects/project3.json")
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_OPEN_PROJECT)

      clickOn(FXID.TAB_INFO)
      
      // clear project name
      val field = lookup(FXID.TEXT_PROJECT_NAME).queryTextInputControl()
      field.text = ""

      // try to generate
      val generateButton = lookup(FXID.TOOLBAR_BUTTON_GENERATE_IMAGE).queryButton()
      clickOn(generateButton)

      // should show error window
      FxAssert.verifyThat(FXID.DIALOG_ITEMLIST, NodeMatchers.isVisible())
      val ok = lookup(FXID.BUTTON_ITEMLIST_DLG_OK).queryButton()
      clickOn(ok)
   }

   private fun validateInfoTab(project: InstallProject) {
      clickOn(FXID.TAB_INFO)
      var field = lookup(FXID.TEXT_PROJECT_NAME).queryTextInputControl()
      assertThat(field.text).isEqualTo(project.name)

      field = lookup(FXID.TEXT_APP_VERSION).queryTextInputControl()
      assertThat(field.text).isEqualTo(project.version)

      field = lookup(FXID.TEXT_COPYRIGHT).queryTextInputControl()
      assertThat(field.text).isEqualTo(project.copyright)

      field = lookup(FXID.TEXT_LOGO_PATH).queryTextInputControl()
      assertThat(field.text).isEqualTo(project.applicationLogo?.path)

      val combo = lookup(FXID.COMBO_INSTALLER_TYPE).queryComboBox<String>()
      assertThat(combo.selectionModel.selectedItem).isEqualTo(project.installerType)
   }

   private fun validateInstallTab(project: InstallProject) {
      clickOn(FXID.TAB_INSTALL)
      var field = lookup(FXID.TEXT_INPUT_DIR).queryTextInputControl()
      assertThat(field.text).isEqualTo(project.inputDirectory?.path)

      field = lookup(FXID.TEXT_IMAGE_BUILD_DIR).queryTextInputControl()
      assertThat(field.text).isEqualTo(project.imageBuildDirectory?.path)

      field = lookup(FXID.TEXT_INSTALLER_DIR).queryTextInputControl()
      assertThat(field.text).isEqualTo(project.installerDirectory?.path)
   }

   private fun validateExecutableTab(project: InstallProject) {
      clickOn(FXID.TAB_EXECUTABLE)

      var field = lookup(FXID.TEXT_MAIN_JAR).queryTextInputControl()
      assertThat(field.text).isEqualTo(project.mainJar?.path)

      field = lookup(FXID.TEXT_MAIN_CLASS).queryTextInputControl()
      assertThat(field.text).isEqualTo(project.mainClass)
   }

   private fun validateDependenciesTab(project: InstallProject) {
      clickOn(FXID.TAB_DEPENDENCIES)

      val field = lookup(FXID.TEXT_MODULE_LIST).queryTextInputControl()
      assertThat(field.text).isEqualTo("")

      val classpath = lookup(FXID.LISTVIEW_CLASSPATH).queryListView<String>()
      assertThat(classpath.items).containsExactlyInAnyOrderElementsOf(project.classPath.map({ it.path }))
   }

   private fun validateBinariesTab(project: InstallProject) {
      clickOn(FXID.TAB_BINARIES)
      val combo1 = lookup(FXID.COMBO_JPACKAGE).queryComboBox<JDK>()
      assertThat(combo1.selectionModel.selectedItem.name).isEqualTo(project.jpackageJDK?.name)

      val combo2 = lookup(FXID.COMBO_MODULE_LIB).queryComboBox<NamedDirectory>()
      assertThat(combo2.selectionModel.selectedItem.name).isEqualTo(project.javaFXLib?.name)

      val combo3 = lookup(FXID.COMBO_INSTALL_JDK).queryComboBox<JDK>()
      assertThat(combo3.selectionModel.selectedItem.name).isEqualTo(project.installJDK?.name)

      val combo4 = lookup(FXID.COMBO_MODULE_JMOD).queryComboBox<NamedDirectory>()
      assertThat(combo4.selectionModel.selectedItem.name).isEqualTo(project.javaFXMods?.name)
   }
}