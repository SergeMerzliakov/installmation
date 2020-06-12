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
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.installmation.FXTest
import org.installmation.javafx.test.FXID
import org.junit.Test

class ItemListDialogTest : FXTest() {

   companion object {
      const val SHOW_DIALOG_BUTTON = "button1"
      const val DIALOG_TITLE = "Project Errors"
      const val DIALOG_LABEL = "Errors"
      const val MESSAGE_1 = "error1"
      const val MESSAGE_2 = "error2"
   }

   private lateinit var dialog: ItemListDialog
   private lateinit var buttonSingle: Button
   private lateinit var result: DialogResult<Boolean>

   override fun start(stage: Stage?) {
      super.start(stage)
      buttonSingle = Button("Show Dialog")
      buttonSingle.id = SHOW_DIALOG_BUTTON
      buttonSingle.setOnAction {
         dialog = ItemListDialog(DIALOG_TITLE, DIALOG_LABEL, listOf(MESSAGE_1, MESSAGE_2))
         result = dialog.showAndWait()
      }

      stage?.scene = Scene(VBox(buttonSingle), 100.0, 50.0)
      stage?.show()
   }

   @Test
   fun shouldShowCorrectly() {
      clickOn("#$SHOW_DIALOG_BUTTON")

      //title
      assertThat(dialog.stage.title).isEqualTo(DIALOG_TITLE)

      //label
      val label = lookup(FXID.LABEL_ITEMLIST_DLG).query<Label>()
      assertThat(label.text).isEqualTo(DIALOG_LABEL)

      // listview
      val itemListView = lookup(FXID.LISTVIEW_ITEMLIST_DLG).query<ListView<String>>()
      assertThat(itemListView.items).hasSize(2)
      assertThat(itemListView.items[0]).isEqualTo(MESSAGE_1)
      assertThat(itemListView.items[1]).isEqualTo(MESSAGE_2)

      clickOn(FXID.BUTTON_ITEMLIST_DLG_OK)
   }
}