/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
**/

package org.installmation.ui.dialog

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.installmation.configuration.UserHistory

/**
 * Dialog for selecting a JDK or JFX install. Can also add items
 * Pair<String, File> is a label -> location
 * e.g. "jkd13" -> File("/usr/local/java13")
 */
class BinaryArtefactDialog(ownerStage: Stage, title: String, currentArtefacts: List<BinaryArtefactDialogController.Item>, userHistory: UserHistory) : CustomDialog<BinaryArtefactDialogController.Item>(ownerStage, title) {

   private var controller: BinaryArtefactDialogController

   init {
      val loader = FXMLLoader(javaClass.classLoader.getResource("dialog/binaryArtefactDialog.fxml"))
      controller = BinaryArtefactDialogController(currentArtefacts, userHistory)
      loader.setController(controller)
      val root = loader.load<Pane>()
      stage.scene = Scene(root)
   }

   override fun result(): DialogResult<BinaryArtefactDialogController.Item> {
      val v = controller.getSelected()
      if (v != null)
         return DialogResult(true, v)
      else {
         return if (controller.modelUpdated)
            DialogResult(true, null)
         else
            DialogResult(false, null)
      }
   }

   /**
    * If model updated return it, otherwise return null
    */
   fun updatedModel(): List<BinaryArtefactDialogController.Item>? {
      if (controller.modelUpdated)
         return controller.artefacts()
      return null
   }
}