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
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.installmation.javafx.test.FXID
import org.junit.Test
import org.testfx.framework.junit.ApplicationTest

class AboutDialogTest : ApplicationTest() {

   companion object {
      const val DIALOG_BUTTON = "button1"
   }

   private lateinit var dialog: AboutDialog
   private lateinit var buttonSingle: Button
   private lateinit var result: DialogResult<String>

   override fun start(stage: Stage?) {
      super.start(stage)
      buttonSingle = Button("Show Dialog")
      buttonSingle.id = DIALOG_BUTTON
      buttonSingle.setOnAction {
         dialog = AboutDialog(stage!!)
         result = dialog.showAndWait()
      }

      stage?.scene = Scene(VBox(buttonSingle), 100.0, 100.0)
      stage?.title = "Runner"
      stage?.show()
   }

   @Test
   fun shouldShowDialog() {
      clickOn("#$DIALOG_BUTTON")
      val ok = lookup(FXID.BUTTON_ABOUT_DLG_OK).query<Button>()
      clickOn(ok)
   }
}