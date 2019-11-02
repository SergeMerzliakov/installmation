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

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.Stage
import java.io.File


/**
 * Data
 */
class BinaryArtefactDialogController(currentArtefacts: List<Pair<String, File>>) {

   private var saved: Boolean = false
   @FXML lateinit var nameColumn: TableColumn<Pair<String, File>, String>
   @FXML lateinit var locationColumn: TableColumn<Pair<String, File>, String>
   @FXML lateinit var artefactTableView: TableView<Pair<String, File>>

   // true if user has modified artefact list
   var artefactsUpdated = false
      private set

   private val artefacts = FXCollections.observableArrayList<Pair<String, File>>(currentArtefacts)

   @FXML
   fun initialize() {
      artefactTableView.selectionModel.selectionMode = SelectionMode.SINGLE
      nameColumn.cellValueFactory = PropertyValueFactory<Pair<String, File>, String>("first")
      locationColumn.cellValueFactory = PropertyValueFactory<Pair<String, File>, String>("second")
      artefactTableView.items = artefacts
   }

   /**
    * return null if cancelled dialog
    */
   fun getValue(): Pair<String, File>? {
      if (artefactTableView.selectionModel.isEmpty)
         return null
      return artefactTableView.selectionModel.selectedItem
   }

   @FXML
   fun save() {
      saved = true
      val stage = artefactTableView.scene.window as Stage
      stage.close()
   }

   @FXML
   fun cancel() {
      saved = false
      val stage = artefactTableView.scene.window as Stage
      stage.close()
   }

   @FXML
   fun addBinaryArtefact() {
      
   }
}