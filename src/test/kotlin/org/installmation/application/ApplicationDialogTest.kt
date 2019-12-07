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
import org.installmation.core.RunningAsTestEvent
import org.installmation.service.Workspace
import org.junit.Test
import org.testfx.api.FxAssert
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.base.NodeMatchers

/**
 * Ensure all dialogs are at least shown in button and menu clicks. No check on dialog contents
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
      clickOn("#aboutMenu").clickOn("#aboutMenuItem")
      FxAssert.verifyThat("#aboutDialog", NodeMatchers.isVisible())
      clickOn("#okButton")
   }

   @Test
   fun shouldOpenNewProjectDialog() {
      clickOn("#projectMenu").clickOn("#newProjectMenuItem")
      FxAssert.verifyThat("#singleValueDialog", NodeMatchers.isVisible())
      clickOn("#cancelButton")
   }

   @Test
   fun shouldOpenJDKDialogFromMenu() {
      clickOn("#libraryMenu").clickOn("#jpackageMenuItem")
      FxAssert.verifyThat("#binaryArtefactDialog", NodeMatchers.isVisible())
      clickOn("#cancelButton")
   }

   @Test
   fun shouldOpenJavaFXDialogFromMenu() {
      clickOn("#libraryMenu").clickOn("#javafxMenuItem")
      FxAssert.verifyThat("#binaryArtefactDialog", NodeMatchers.isVisible())
      clickOn("#cancelButton")
   }

   @Test
   fun shouldOpenJDKDialogFromButton() {
      clickOn("#jpackageButton")
      FxAssert.verifyThat("#binaryArtefactDialog", NodeMatchers.isVisible())
      clickOn("#cancelButton")
   }

   @Test
   fun shouldOpenJavaFXDialogFromButton() {
      clickOn("#javafxButton")
      FxAssert.verifyThat("#binaryArtefactDialog", NodeMatchers.isVisible())
      clickOn("#cancelButton")
   }
}