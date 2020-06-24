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
package org.installmation.ui.dialog

import com.google.common.eventbus.EventBus
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.installmation.FXTest
import org.installmation.TestingBootstrap
import org.installmation.configuration.Configuration
import org.installmation.configuration.UserHistory
import org.installmation.core.OperatingSystem
import org.installmation.javafx.test.FXID
import org.installmation.model.InstallProject
import org.installmation.model.NamedDirectory
import org.installmation.model.binary.*
import org.junit.BeforeClass
import org.junit.Test
import org.testfx.util.WaitForAsyncUtils
import java.io.File

private const val DIALOG_BUTTON = "button1"
private const val JDK_NAME = "jdk1"
private val RESOURCE_ROOT = File("src/test/resources/").absolutePath
private val LIB1_PATH = File(RESOURCE_ROOT, "/tiny-app/lib/log4j-core-2.12.1.jar")
private val LIB2_PATH = File(RESOURCE_ROOT, "/tiny-app/lib/log4j-api-2.12.1.jar")
private val MAIN_JAR = File(RESOURCE_ROOT, "/tiny-app/tiny-java-app-1.0.jar")

class JdepsDialogTest : FXTest() {

   companion object {
      @BeforeClass
      @JvmStatic
      fun setup() {
         TestingBootstrap.checkBinariesInstalled()
      }
   }

   private lateinit var buttonSingle: Button

   override fun start(stage: Stage?) {
      super.start(stage)
      buttonSingle = Button("Show Dialog")
      buttonSingle.id = DIALOG_BUTTON
      buttonSingle.setOnAction {
         val tinyClasspath = listOf(LIB1_PATH, LIB2_PATH)
         val proj = InstallProject()
         proj.javaFXLib = NamedDirectory("jfx", TestingBootstrap.javafx!!)
         proj.mainJar = MAIN_JAR
         proj.classPath.addAll(tinyClasspath)
         val config = Configuration(EventBus())
         config.jdkEntries["jdk1"] = JDKFactory.create(OperatingSystem.os(), JDK_NAME, TestingBootstrap.jdk!!)
         val jd = JdepsDialog(stage!!, config, UserHistory(), proj)
         jd.showAndWait()
      }

      stage?.scene = Scene(VBox(buttonSingle), 100.0, 100.0)
      stage?.title = "Runner"
      stage?.show()
   }

   @Test
   fun shouldRunCommand() {
      clickOn("#$DIALOG_BUTTON")
      WaitForAsyncUtils.waitForFxEvents(20) //sometimes the dialog is slow to appear

      val jdk = lookup(FXID.COMBO_JDEPS_DLG_JDK).query<ComboBox<JDK>>()
      assertThat(jdk.selectionModel.selectedItem.name).isEqualTo(JDK_NAME)

      val mainJarText = lookup(FXID.TEXT_JDEPS_DLG_MAINJAR).query<TextField>()
      assertThat(mainJarText.text).isEqualTo(MAIN_JAR.path)

      val classPathListView = lookup(FXID.LISTVIEW_JDEPS_DLG_CLASSPATH).query<ListView<String>>()
      assertThat(classPathListView.items).hasSize(2)
      assertThat(classPathListView.items).contains(LIB1_PATH.path, LIB2_PATH.path)

      val modulePathListView = lookup(FXID.LISTVIEW_JDEPS_DLG_MODULEPATH).query<ListView<String>>()
      assertThat(modulePathListView.items).hasSize(1)
      assertThat(modulePathListView.items).contains(TestingBootstrap.javafx!!.path)

      //run jdeps
      clickOn(FXID.BUTTON_JDEPS_DLG_RUN)

      val generatedCommandText = lookup(FXID.TEXT_JDEPS_DLG_GENERATED_CMD).query<TextArea>()
      // test key elements of command present
      assertThat(generatedCommandText.text).contains("jdeps", "--multi-release 11", "-classpath",
            "log4j-core-2.12.1.jar",
            "log4j-api-2.12.1.jar ",
            "tiny-java-app-1.0.jar")

      clickOn(FXID.TAB_JDEPS_DLG_OUTPUT)
      val processOutputView = lookup(FXID.LISTVIEW_JDEPS_DLG_PROCESS_OUTPUT).query<ListView<String>>()
      assertThat(processOutputView.items).hasSizeGreaterThan(0)

      clickOn(FXID.TAB_JDEPS_DLG_LIST)
      val dependencyListView = lookup(FXID.LISTVIEW_JDEPS_DLG_DEP_LIST).query<ListView<String>>()
      assertThat(dependencyListView.items).hasSize(1)
      assertThat(dependencyListView.items).contains(LIB2_PATH.path)

      clickOn(FXID.TAB_JDEPS_DLG_TEXT)
      val dependencyTextArea = lookup(FXID.TEXT_JDEPS_DLG_DEPENDENCY).query<TextArea>()
      assertThat(dependencyTextArea.text).isEqualTo(LIB2_PATH.path)

      clickOn(FXID.BUTTON_JDEPS_DLG_CLOSE)
   }

   @Test
   fun shouldClearOutputAfterRunCommand() {
      clickOn("#$DIALOG_BUTTON")
      WaitForAsyncUtils.waitForFxEvents(20) //sometimes the dialog is slow to appear

      val mainJarText = lookup(FXID.TEXT_JDEPS_DLG_MAINJAR).query<TextField>()
      assertThat(mainJarText.text).isEqualTo(MAIN_JAR.path)

      val classPathListView = lookup(FXID.LISTVIEW_JDEPS_DLG_CLASSPATH).query<ListView<String>>()
      assertThat(classPathListView.items).hasSize(2)
      assertThat(classPathListView.items).contains(LIB1_PATH.path, LIB2_PATH.path)

      val modulePathListView = lookup(FXID.LISTVIEW_JDEPS_DLG_MODULEPATH).query<ListView<String>>()
      assertThat(modulePathListView.items).hasSize(1)
      assertThat(modulePathListView.items).contains(TestingBootstrap.javafx!!.path)

      //run jdeps
      clickOn(FXID.BUTTON_JDEPS_DLG_RUN)

      // just check something generated - this ensures that run command actually generated
      // output to clear
      val generatedCommandText = lookup(FXID.TEXT_JDEPS_DLG_GENERATED_CMD).query<TextArea>()
      assertThat(generatedCommandText.text).contains("jdeps", "--multi-release 11", "-classpath")

      // clear generated output
      clickOn(FXID.BUTTON_JDEPS_DLG_CLEAR)

      // ensure all relevant controls are cleared of text
      assertThat(generatedCommandText.text).isNullOrEmpty()

      clickOn(FXID.TAB_JDEPS_DLG_OUTPUT)
      val processOutputView = lookup(FXID.LISTVIEW_JDEPS_DLG_PROCESS_OUTPUT).query<ListView<String>>()
      assertThat(processOutputView.items).isEmpty()

      clickOn(FXID.TAB_JDEPS_DLG_LIST)
      val dependencyListView = lookup(FXID.LISTVIEW_JDEPS_DLG_DEP_LIST).query<ListView<String>>()
      assertThat(dependencyListView.items).isEmpty()

      clickOn(FXID.TAB_JDEPS_DLG_TEXT)
      val dependencyTextArea = lookup(FXID.TEXT_JDEPS_DLG_DEPENDENCY).query<TextArea>()
      assertThat(dependencyTextArea.text).isNullOrEmpty()
   }
}