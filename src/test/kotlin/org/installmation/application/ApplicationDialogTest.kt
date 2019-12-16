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

package org.installmation.application

import com.google.common.eventbus.EventBus
import javafx.stage.Stage
import org.installmation.InstallmationApplication
import org.installmation.configuration.Configuration
import org.installmation.core.OperatingSystem
import org.installmation.core.RunningAsTestEvent
import org.installmation.javafx.test.FXID
import org.junit.Test
import org.testfx.api.FxAssert
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.base.NodeMatchers
import org.testfx.util.WaitForAsyncUtils

/** org.installmation.application.ApplicationDialogTest
 * Ensure all dialogs are at least shown in button and menu clicks. No check on dialog contents.
 * Note: Running tests in Windows with > 1 Intellij projects will fail on first test in this class.
 * Shut down other projects, minimize IDEA window and then it works.
 */
class ApplicationDialogTest : ApplicationTest() {

   private val application = InstallmationApplication()
   override fun start(stage: Stage?) {
      super.start(stage)
      val bus = EventBus()
      val configuration = Configuration(bus)
      application.startApplication(stage!!, configuration, bus)
      bus.post(RunningAsTestEvent())
   }

   @Test
   fun shouldOpenAboutDialog() {
      clickOn(FXID.MENU_ABOUT).clickOn(FXID.MENUITEM_ABOUT)
      FxAssert.verifyThat(FXID.DIALOG_ABOUT_ID, NodeMatchers.isVisible())
      clickOn(FXID.BUTTON_ABOUT_DLG_OK)
   }

   @Test
   fun shouldOpenNewProjectDialog() {
      clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_NEW_PROJECT)
      FxAssert.verifyThat(FXID.DIALOG_SINGLEVAL_ID, NodeMatchers.isVisible())
      clickOn(FXID.BUTTON_SINGLEVAL_DLG_CANCEL)
   }

   @Test
   fun shouldOpenJDKDialogFromMenu() {
      clickOn(FXID.MENU_LIBRARY).clickOn(FXID.MENUITEM_JPACKAGE)
      FxAssert.verifyThat(FXID.DIALOG_BINARTEFACT_ID, NodeMatchers.isVisible())
      clickOn(FXID.BUTTON_BINARTEFACT_CANCEL)
   }

   @Test
   fun shouldOpenJavaFXDialogFromMenu() {
      clickOn(FXID.MENU_LIBRARY).clickOn(FXID.MENUITEM_JFX)
      FxAssert.verifyThat(FXID.DIALOG_BINARTEFACT_ID, NodeMatchers.isVisible())
      clickOn(FXID.BUTTON_BINARTEFACT_CANCEL)
   }

   @Test
   fun shouldOpenJDKDialogFromButton() {
      clickOn(FXID.TOOLBAR_BUTTON_JPACKAGE)
      FxAssert.verifyThat(FXID.DIALOG_BINARTEFACT_ID, NodeMatchers.isVisible())
      clickOn(FXID.BUTTON_BINARTEFACT_CANCEL)
   }

   @Test
   fun shouldOpenJavaFXDialogFromButton() {
      clickOn(FXID.TOOLBAR_BUTTON_JAVAFX)
      FxAssert.verifyThat(FXID.DIALOG_BINARTEFACT_ID, NodeMatchers.isVisible())
      clickOn(FXID.BUTTON_BINARTEFACT_CANCEL)
   }

   @Test
   fun shouldOpenJdepsDialog() {
      clickOn(FXID.MENU_TOOL)
      clickOn(FXID.MENUITEM_JDEPS)
      FxAssert.verifyThat(FXID.DIALOG_JDEPS, NodeMatchers.isVisible())
      clickOn(FXID.BUTTON_JDEPS_DLG_CLOSE)
   }

}