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

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.testfx.framework.junit.ApplicationTest
import org.testfx.matcher.control.LabeledMatchers.hasText

class HelpDialogTest : ApplicationTest() {

   companion object {
      const val DIALOG_BUTTON = "button1"
      const val TITLE = "Title 1"
      const val HELP_TEXT = "Some help here!"
   }

   private lateinit var buttonSingle: Button

   override fun start(stage: Stage?) {
      super.start(stage)
      buttonSingle = Button("Show Dialog")
      buttonSingle.id = DIALOG_BUTTON
      buttonSingle.setOnAction {
         HelpDialog.showAndWait(TITLE, HELP_TEXT)
      }

      stage?.scene = Scene(VBox(buttonSingle), 100.0, 100.0)
      stage?.title = "Runner"
      stage?.show()
   }

   @Test
   fun shouldShowDialog() {
      clickOn("#$DIALOG_BUTTON")
      val messageLabel = lookup(hasText(HELP_TEXT)).query<Label>()

      // check help message
      assertThat(messageLabel.text).isNotNull()

      //TODO find a clean way to test title
      clickOn("OK")
   }
}