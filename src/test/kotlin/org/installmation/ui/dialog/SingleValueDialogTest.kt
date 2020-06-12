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
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.installmation.FXTest
import org.installmation.javafx.test.FXID
import org.junit.Test

class SingleValueDialogTest : FXTest() {

   companion object {
      const val SHOW_DIALOG_BUTTON = "button1"
      const val DIALOG_TITLE = "Colour Picker"
      const val DIALOG_LABEL = "Choose Colour"
      const val DEFAULT_VALUE = "red"
   }

   private lateinit var dialog: SingleValueDialog
   private lateinit var buttonSingle: Button
   private lateinit var result: DialogResult<String>

   override fun start(stage: Stage?) {
      super.start(stage)
      buttonSingle = Button("Show Dialog")
      buttonSingle.id = SHOW_DIALOG_BUTTON
      buttonSingle.setOnAction {
         dialog = SingleValueDialog(stage!!, DIALOG_TITLE, DIALOG_LABEL, DEFAULT_VALUE)
         result = dialog.showAndWait()
      }

      stage?.scene = Scene(VBox(buttonSingle), 100.0, 100.0)
      stage?.show()
   }

   @Test
   fun showHaveCorrectTitleAndLabelAndDefault() {
      clickOn("#$SHOW_DIALOG_BUTTON")

      assertThat(dialog.stage.title).isEqualTo(DIALOG_TITLE)

      val label = lookup(FXID.LABEL_SINGLEVAL_DLG_ITEM).query<Label>()
      assertThat(label.text).isEqualTo(DIALOG_LABEL)

      val value = lookup(FXID.TEXT_SINGLEVAL_DLG_ITEM_VALUE).query<TextField>()
      assertThat(value.text).isEqualTo(DEFAULT_VALUE)
   }

   @Test
   fun shouldSave() {
      clickOn("#$SHOW_DIALOG_BUTTON")
      val newColour = "green"

      val field = lookup(FXID.TEXT_SINGLEVAL_DLG_ITEM_VALUE).query<TextField>()
      doubleClickOn(field)
      write(newColour)
      type(KeyCode.TAB)

      clickOn(FXID.BUTTON_SINGLEVAL_DLG_SAVE)

      assertThat(result.ok).isTrue()
      assertThat(result.data).isEqualTo(newColour)
   }

   @Test
   fun shouldCancel() {
      clickOn("#$SHOW_DIALOG_BUTTON")
      val newColour = "green"

      val field = lookup(FXID.TEXT_SINGLEVAL_DLG_ITEM_VALUE).query<TextField>()
      doubleClickOn(field)
      write(newColour)
      type(KeyCode.TAB)

      clickOn(FXID.BUTTON_SINGLEVAL_DLG_CANCEL)

      assertThat(result.ok).isFalse()
      assertThat(result.data).isNull()
   }
}