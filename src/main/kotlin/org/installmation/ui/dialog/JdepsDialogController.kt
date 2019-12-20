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
import javafx.scene.control.*
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.configuration.UserHistory
import org.installmation.core.CollectionUtils
import org.installmation.model.NamedDirectory
import org.installmation.model.binary.JDK
import org.installmation.model.binary.JDepsExecutable
import org.installmation.model.binary.ModuleDependenciesGenerator
import java.io.File
import kotlin.random.Random

class JdepsDialogController(private val jdkList: Collection<JDK>, private val javaFXLibs: File?, private val mainJarFile: File?, classPathFiles: Collection<File>?, private val userHistory: UserHistory) {

   companion object {
      val log: Logger = LogManager.getLogger(JdepsDialogController::class.java)
      const val NO_DEPENDENCIES_MESSAGE = "<No module dependencies>"
   }

   @FXML lateinit var mainJarText: TextField
   @FXML lateinit var generatedCommandText: TextArea
   @FXML lateinit var classPathListView: ListView<String>
   @FXML lateinit var modulePathListView: ListView<String>
   @FXML lateinit var jdkComboBox: ComboBox<JDK>
   @FXML lateinit var processOutputView: ListView<String>
   @FXML lateinit var dependencyListView: ListView<String>
   @FXML lateinit var dependencyTextArea: TextArea
   @FXML private lateinit var configureMainJarButton: Button
   
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
      if (javaFXLibs != null)
         modulePath.add(javaFXLibs.path)
   }

   @FXML
   fun initialize() {
      mainJarText.text = mainJarFile?.path
      classPathListView.items = classPath.sorted()
      modulePathListView.items = modulePath.sorted()
      dependencyListView.items = moduleOutput.sorted()
      processOutputView.items = processOutput
      for (jdk in jdkList)
         jdkComboBox.items.add(jdk)
      jdkComboBox.selectionModel.select(0)
   }

   @FXML
   fun run() {
      val jdeps = JDepsExecutable(jdkComboBox.selectionModel.selectedItem)
      val classPathString = CollectionUtils.toPathList(classPath)

      if (javaFXLibs != null) {
         val mdg = ModuleDependenciesGenerator(jdeps, classPathString, javaFXLibs, mainJarText.text)
         val moduleDependencies = mdg.generate()
         displayResults(jdeps, mdg, moduleDependencies)
      }
      else
         HelpDialog.showAndWait("JavaFX Not defined", "Please set location of JavaFX jar library files.")
   }

   private fun displayResults(jdeps: JDepsExecutable, mdg: ModuleDependenciesGenerator, moduleDependencies: List<String>) {
      generatedCommandText.text = jdeps.toString()
      moduleOutput.clear()
      processOutput.clear()

      if (mdg.output.isNotEmpty()) {
         mdg.output.map { processOutput.add(it) }
      }

      if (moduleDependencies.isNotEmpty()) {
         moduleDependencies.map { moduleOutput.add(it) }
         dependencyTextArea.text = moduleDependencies.joinToString()
      } else {
         moduleOutput.add(NO_DEPENDENCIES_MESSAGE)
         dependencyTextArea.text = NO_DEPENDENCIES_MESSAGE
         processOutput.add(NO_DEPENDENCIES_MESSAGE.replace(">", "") + " - java.base is not considered a module dependency>")
      }
   }

   @FXML
   fun close() {
      val stage = classPathListView.scene.window as Stage
      stage.close()
   }

   @FXML
   fun addClassPath() {
      val result = ChooseDirectoryDialog.showAndWait(classPathListView.scene.window as Stage, "Add Classpath Item", userHistory)
      if (result.ok) {
         classPath.add(result.data!!.path)
         log.debug("Added ${result.data.path} to classpath")
      }
   }

   @FXML
   fun clearClassPath() {
      classPath.clear()
   }

   @FXML
   fun addModulePath() {
      val result = ChooseDirectoryDialog.showAndWait(classPathListView.scene.window as Stage, "Add Module Path Item", userHistory)
      if (result.ok) {
         modulePath.add(result.data!!.path)
         log.debug("Added ${result.data.path} to module path")
      }
   }

   @FXML
   fun clearModulePath() {
      modulePath.clear()
   }

   @FXML
   fun removeClassPathItem() {
      if (classPathListView.selectionModel.isEmpty)
         return
      classPath.remove(classPathListView.selectionModel.selectedItem)
   }
   
   @FXML
   fun removeModulePathItem() {
      if (modulePathListView.selectionModel.isEmpty)
          return
      modulePath.remove(modulePathListView.selectionModel.selectedItem)
   }
   
   @FXML
   fun configureMainJar() {
      val result = ChooseFileDialog.showAndWait(mainJarText.scene.window as Stage, "Select Main Application Jar File", userHistory, InstallmationExtensionFilters.jarFilter())
      if (result.ok) {
         mainJarText.text = result.data!!.path
      }
   }
}
