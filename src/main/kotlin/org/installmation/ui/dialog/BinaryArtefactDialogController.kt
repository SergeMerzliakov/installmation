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
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.Stage
import java.io.File


/**
 * Data
 */
class BinaryArtefactDialogController(currentArtefacts: List<MutablePair<String, File>>) {

   private var saved: Boolean = false
   @FXML lateinit var nameColumn: TableColumn<MutablePair<String, File>, String>
   @FXML lateinit var locationColumn: TableColumn<MutablePair<String, File>, File>
   @FXML lateinit var artefactTableView: TableView<MutablePair<String, File>>

   // true if user has modified artefact list
   var artefactsUpdated = false
      private set

   private val artefacts = FXCollections.observableArrayList<MutablePair<String, File>>(currentArtefacts)

   @FXML
   fun initialize() {
      artefactTableView.selectionModel.selectionMode = SelectionMode.SINGLE
      nameColumn.cellValueFactory = PropertyValueFactory<MutablePair<String, File>, String>("first")
      locationColumn.cellValueFactory = PropertyValueFactory<MutablePair<String, File>, File>("second")
      artefactTableView.items = artefacts

      nameColumn.cellFactory = TextFieldTableCell.forTableColumn()
      locationColumn.cellFactory = TextFieldTableCell.forTableColumn(FileStringConverter())

      // save tableview cell edits to data model
      nameColumn.onEditCommit = EventHandler<TableColumn.CellEditEvent<MutablePair<String, File>, String>> { t ->
         val updatedItem = artefacts.find { it.first == t.oldValue }
         updatedItem?.first = t.newValue
      }

      locationColumn.onEditCommit = EventHandler<TableColumn.CellEditEvent<MutablePair<String, File>, File>> { t ->
         val updatedItem = artefacts.find { it.first == t.rowValue.first }
         updatedItem?.second = t.newValue
      }
   }

   /**
    * return null if cancelled dialog
    */
   fun getValue(): MutablePair<String, File>? {
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