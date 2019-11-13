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

import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.installmation.configuration.UserHistory
import java.io.File

/**
 * Wrapper around JavaFX DirectoryChooser dialog. Chooses a directory
 */
object ChooseDirectoryDialog {

   fun showAndWait(parent: Stage, title: String, userHistory: UserHistory): DialogResult<File> {
      val chooser = DirectoryChooser()
      chooser.title = title
      chooser.initialDirectory = userHistory.lastPath
      val chosen = chooser.showDialog(parent)
      if (chosen != null) {
         userHistory.lastPath = chosen.parentFile
         return DialogResult(true, chosen)
      }
      return DialogResult(false, chosen)
   }
}