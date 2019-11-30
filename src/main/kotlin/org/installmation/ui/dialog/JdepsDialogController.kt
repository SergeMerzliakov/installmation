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
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.Stage
import org.installmation.core.CollectionUtils
import org.installmation.model.FlagArgument
import org.installmation.model.ValueArgument
import org.installmation.model.binary.JDK
import org.installmation.model.binary.JDepsExecutable
import java.io.File

class JdepsDialogController(private val jdkList: Collection<JDK>, private val mainJarFile: File?, classPathFiles: Collection<File>?, modulePathFiles: Collection<File>?) {

   @FXML lateinit var mainJar: TextField
   @FXML lateinit var generatedCommandText: TextField
   @FXML lateinit var classPathListView: ListView<String>
   @FXML lateinit var modulePathListView: ListView<String>
   @FXML lateinit var jdkComboBox: ComboBox<JDK>
   @FXML lateinit var processOutputView: ListView<String>
   @FXML lateinit var dependencyListView: ListView<String>
   @FXML lateinit var dependencyTextArea: TextArea

   // model
   private val classPath: ObservableList<String> = FXCollections.observableArrayList()
   private val modulePath: ObservableList<String> = FXCollections.observableArrayList()
   private val moduleOutput: ObservableList<String> = FXCollections.observableArrayList()
   private val processOutput: ObservableList<String> = FXCollections.observableArrayList()

   init {
      if (classPathFiles != null) {
         for (f in classPathFiles)
            classPath.add(f.path)
      }
      if (modulePathFiles != null) {
         for (f in modulePathFiles)
            modulePath.add(f.path)
      }
   }

   @FXML
   fun initialize() {
      mainJar.text = mainJarFile?.path
      classPathListView.items = classPath.sorted()
      modulePathListView.items = modulePath.sorted()
      dependencyListView.items = moduleOutput.sorted()
      processOutputView.items = processOutput.sorted()
      for (jdk in jdkList)
         jdkComboBox.items.add(jdk)
      jdkComboBox.selectionModel.select(0)
   }

   @FXML
   fun run() {
      jdkComboBox.selectionModel
      val jdeps = JDepsExecutable(jdkComboBox.selectionModel.selectedItem)
      jdeps.parameters.addArgument(FlagArgument("-s"))
      jdeps.parameters.addArgument(ValueArgument("--class-path", CollectionUtils.toPathList(classPath)))
      jdeps.parameters.addArgument(ValueArgument("--module-path", CollectionUtils.toPathList(modulePath)))
      jdeps.parameters.addArgument(FlagArgument(mainJar.text))
      generatedCommandText.text = jdeps.toString()
      val output = jdeps.execute(15)
      moduleOutput.clear()
      for (line in output) {
         processOutput.add(line)
         moduleOutput.add(line)
      }
   }

   @FXML
   fun close() {
      val stage = classPathListView.scene.window as Stage
      stage.close()
   }

   @FXML
   fun addClassPath() {
   }

   @FXML
   fun addModulePath() {
   }

   @FXML
   fun clearClassPath() {
   }
}
