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

import javafx.stage.FileChooser
import javafx.stage.Stage
import org.installmation.configuration.UserHistory
import java.io.File

/**
 * Wrapper around JavaFX FileChooser dialog. Chooses a single file for now
 */
object ChooseFileDialog {

   /**
    * For extensionFilter parameter use InstallmationExtensionFilters methods - no need to create your own
    */
   fun showAndWait(parent: Stage, title: String, lastPath: File, extensionFilter: FileChooser.ExtensionFilter? = null): DialogResult<File> {
      val chooser = FileChooser()
      chooser.title = title
      chooser.initialDirectory = lastPath
      if (extensionFilter != null)
         chooser.extensionFilters.add(extensionFilter)

      val chosen = chooser.showOpenDialog(parent) //single file only
      if (chosen != null) {
         return DialogResult(true, chosen)
      }
      return DialogResult(false, chosen)
   }
}