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

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.stage.Stage


class ItemListDialogController(private val listText: String, private val errors: List<String>) {

   @FXML lateinit var listLabel: Label
   @FXML lateinit var itemListView: ListView<String>

   @FXML
   fun initialize() {
      listLabel.text = listText
      itemListView.items.addAll(errors)
   }

   @FXML
   fun ok() {
      val stage = listLabel.scene.window as Stage
      stage.close()
   }
}