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

import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File



object Foo{
   fun saveFileDialog(title: String, initialFileName: String, lastPath: File, extensionFilter: FileChooser.ExtensionFilter? = InstallmationExtensionFilters.projectFilter()): DialogResult<File> {
      val chooser = createChooser(title, lastPath, extensionFilter)
      chooser.initialFileName = initialFileName
      val chosen = chooser.showSaveDialog(null) // single file only
      if (chosen != null) {
         return DialogResult(true, chosen)
      }
      return DialogResult(false, chosen)
   }



}


/**
 * Wrapper around JavaFX FileChooser dialog. Chooses a single file for now
 */


fun openFileDialog(parent: Stage, title: String, lastPath: File, extensionFilter: FileChooser.ExtensionFilter? = InstallmationExtensionFilters.projectFilter()): DialogResult<File> {
   val chooser = createChooser(title, lastPath, extensionFilter)
   val chosen = chooser.showOpenDialog(parent) //single file only
   if (chosen != null) {
      return DialogResult(true, chosen)
   }
   return DialogResult(false, chosen)
}

fun saveFileDialog(parent: Stage, title: String, initialFileName: String, lastPath: File, extensionFilter: FileChooser.ExtensionFilter? = InstallmationExtensionFilters.projectFilter()): DialogResult<File> {
   check(lastPath.isDirectory)
   val chooser = createChooser(title, lastPath, extensionFilter)
   chooser.initialFileName = initialFileName
   val chosen = chooser.showSaveDialog(parent) // single file only
   if (chosen != null) {
      return DialogResult(true, chosen)
   }
   return DialogResult(false, chosen)
}

fun openDirectoryDialog(parent: Stage, title: String, initialDirectory: File): DialogResult<File> {
   val chooser = DirectoryChooser()
   chooser.title = title
   chooser.initialDirectory = initialDirectory
   val chosen = chooser.showDialog(parent)
   if (chosen != null) {
      return DialogResult(true, chosen)
   }
   return DialogResult(false, chosen)
}


private fun createChooser(title: String, lastPath: File, extensionFilter: FileChooser.ExtensionFilter? = null): FileChooser {
   val chooser = FileChooser()
   chooser.title = title
   chooser.initialDirectory = lastPath
   if (extensionFilter != null)
      chooser.extensionFilters.add(extensionFilter)
   return chooser
}