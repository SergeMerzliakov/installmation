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
package org.installmation.ui.dialog

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.installmation.FXTest
import org.installmation.configuration.HISTORY_PROJECT
import org.installmation.configuration.UserHistory
import java.io.File

private const val DIALOG_BUTTON = "button1"

class ChooseFileDialogTest : FXTest() {

   private lateinit var buttonSingle: Button
   private lateinit var result: DialogResult<File>
   private var userHistory = UserHistory()

   override fun start(stage: Stage?) {
      super.start(stage)
      userHistory.set(HISTORY_PROJECT,File("src/test/resources/projects"))
      buttonSingle = Button("Show Dialog")
      buttonSingle.id = DIALOG_BUTTON
      buttonSingle.setOnAction {
         result = openFileDialog(stage!!, "Choose Project", userHistory.getFile(HISTORY_PROJECT), InstallmationExtensionFilters.projectFilter())
      }

      stage?.scene = Scene(VBox(buttonSingle), 100.0, 100.0)
      stage?.title = "Dialog Runner"
      stage?.show()
   }

   // this does nothing TODO figure out how to test standard Dialogs
//   @Test
//   fun shouldShowDialog() {
//      clickOn("#$DIALOG_BUTTON")
//   }

}