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

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 * Generic dialog for entering a single string value
 */
class SingleValueDialog(ownerStage: Stage, label: String, title: String, defaultValue: String?) : CustomDialog<String>(ownerStage, title) {

   private var controller: SingleValueDialogController

   init {
      val loader = FXMLLoader(javaClass.classLoader.getResource("dialog/singleValueDialog.fxml"))
      controller = SingleValueDialogController(label, defaultValue)
      loader.setController(controller)
      val root = loader.load<VBox>()
      stage.scene = Scene(root)
   }

   override fun result(): DialogResult<String> {
      val v = controller.getValue()
      if (v != null)
         return DialogResult(true, v)
      return DialogResult(false, null)
   }
}