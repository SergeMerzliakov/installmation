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

import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.HISTORY_ARTEFACT
import org.installmation.configuration.UserHistory
import org.installmation.model.NamedDirectory
import java.io.File
import kotlin.random.Random

private val log: Logger = LogManager.getLogger(BinaryArtefactDialogController::class.java)

class BinaryArtefactDialogController(currentArtefacts: List<NamedDirectory>, private val userHistory: UserHistory) {

   @FXML lateinit var nameColumn: TableColumn<NamedDirectory, String>
   @FXML lateinit var locationColumn: TableColumn<NamedDirectory, File>
   @FXML lateinit var artefactTableView: TableView<NamedDirectory>
   @FXML lateinit var tableContextMenu: ContextMenu
   
   // true if user has modified artefact list
   private var save = false
   var modelUpdated = false
      private set

   private val artefacts = FXCollections.observableArrayList<NamedDirectory>(currentArtefacts)

   @FXML
   fun initialize() {
      initializeTableView()
      initializeContextMenu()
   }

   private fun initializeTableView() {
      artefactTableView.selectionModel.selectionMode = SelectionMode.SINGLE
      nameColumn.cellValueFactory = PropertyValueFactory<NamedDirectory, String>("name")
      locationColumn.cellValueFactory = PropertyValueFactory<NamedDirectory, File>("path")
      artefactTableView.items = artefacts.sorted()

      nameColumn.cellFactory = TextFieldTableCell.forTableColumn()
      locationColumn.cellFactory = TextFieldTableCell.forTableColumn(FileStringConverter())

      // save tableview cell edits to data model
      nameColumn.onEditCommit = EventHandler<TableColumn.CellEditEvent<NamedDirectory, String>> { t ->
         val updatedItem = artefacts.find { it.name == t.oldValue }
         updatedItem?.name = t.newValue
         modelUpdated = true
      }

      locationColumn.onEditCommit = EventHandler<TableColumn.CellEditEvent<NamedDirectory, File>> { t ->
         val updatedItem = artefacts.find { it.name == t.rowValue.name }
         updatedItem?.path = t.newValue
         modelUpdated = true
      }
   }

   private fun initializeContextMenu() {
      val removeMenuItem = MenuItem("Remove")
      removeMenuItem.onAction = EventHandler<ActionEvent> {
         if (!artefactTableView.selectionModel.isEmpty) {
            artefacts.remove(artefactTableView.selectionModel.selectedItem)
            modelUpdated = true
         }
      }

      tableContextMenu.items.add(removeMenuItem)
   }
   
   
   fun getSelected(): NamedDirectory? {
      return artefactTableView.selectionModel.selectedItem
   }

   /**
    * Return the updated artefacts. Only useful if modelUpdated is true
    */
   fun artefacts(): List<NamedDirectory> {
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
      val chooser = DirectoryChooser()
      chooser.title = "Add Artefact Directory"
      chooser.initialDirectory = userHistory.getFile(HISTORY_ARTEFACT)
      val chosen = chooser.showDialog(artefactTableView.scene.window as Stage)
      if (chosen != null) {
         userHistory.set(HISTORY_ARTEFACT, chosen.parentFile)
         val id = "artefact-${Random.nextInt(99, 9999)}"
         val addedItem = NamedDirectory(id, chosen)
         artefacts.add(addedItem)
         log.debug("Added new artefact $id at ${chosen.canonicalPath}")
         modelUpdated = true
      }
   }
}