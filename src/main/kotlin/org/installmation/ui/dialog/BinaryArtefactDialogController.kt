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
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.UserHistory
import java.io.File
import kotlin.random.Random


class BinaryArtefactDialogController(currentArtefacts: List<Item>, private val userHistory: UserHistory) {

   companion object {
      val log: Logger = LogManager.getLogger(BinaryArtefactDialogController::class.java)
   }

   // identifier is a reference to original object
   class Item(val identifier: String, var name: String, var path: File)

   @FXML lateinit var nameColumn: TableColumn<Item, String>
   @FXML lateinit var locationColumn: TableColumn<Item, File>
   @FXML lateinit var artefactTableView: TableView<Item>

   // true if user has modified artefact list
   private var save = false
   var modelUpdated = false
      private set

   private val artefacts = FXCollections.observableArrayList<Item>(currentArtefacts)

   @FXML
   fun initialize() {
      artefactTableView.selectionModel.selectionMode = SelectionMode.SINGLE
      nameColumn.cellValueFactory = PropertyValueFactory<Item, String>("name")
      locationColumn.cellValueFactory = PropertyValueFactory<Item, File>("path")
      artefactTableView.items = artefacts.sorted()

      nameColumn.cellFactory = TextFieldTableCell.forTableColumn()
      locationColumn.cellFactory = TextFieldTableCell.forTableColumn(FileStringConverter())

      // save tableview cell edits to data model
      nameColumn.onEditCommit = EventHandler<TableColumn.CellEditEvent<Item, String>> { t ->
         val updatedItem = artefacts.find { it.identifier == t.oldValue }
         updatedItem?.name = t.newValue
         modelUpdated = true
      }

      locationColumn.onEditCommit = EventHandler<TableColumn.CellEditEvent<Item, File>> { t ->
         val updatedItem = artefacts.find { it.name == t.rowValue.name }
         updatedItem?.path = t.newValue
         modelUpdated = true
      }
   }

   fun getSelected(): Item? {
      return artefactTableView.selectionModel.selectedItem
   }

   /**
    * Return the updated artefacts. Only useful if modelUpdated is true
    */
   fun artefacts(): List<Item> {
      return artefacts
   }

   @FXML
   fun save() {
      save = true
      val stage = artefactTableView.scene.window as Stage
      stage.close()
   }

   @FXML
   fun cancel() {
      save = false
      val stage = artefactTableView.scene.window as Stage
      stage.close()
   }

   @FXML
   fun addBinaryArtefact() {
      val fileChooser = DirectoryChooser()
      fileChooser.title = "Add Artefact Directory"
      fileChooser.initialDirectory = userHistory.lastPath
      val chosen = fileChooser.showDialog(artefactTableView.scene.window as Stage)
      if (chosen != null) {
         userHistory.lastPath = chosen.parentFile
         val id = "artefact-${Random.nextInt(99, 9999)}"
         val addedItem = Item(id, id, chosen)
         artefacts.add(addedItem)
         log.debug("Added new artefact $id at ${chosen.canonicalPath}")
         modelUpdated = true
      }
   }
}