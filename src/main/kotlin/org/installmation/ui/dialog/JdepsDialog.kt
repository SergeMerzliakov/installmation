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

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.installmation.configuration.Configuration
import org.installmation.configuration.UserHistory
import org.installmation.model.InstallProject

/**
 * Run Jdeps utility to generate module dependencies. Only useful for modular JDK 9+ applications
 */
class JdepsDialog(parentStage: Stage, configuration: Configuration, userHistory: UserHistory, p: InstallProject?) : CustomDialog<Boolean>(parentStage, "Jdeps JDK Tool") {

   private var controller: JdepsDialogController

   init {
      val loader = FXMLLoader(javaClass.classLoader.getResource("fxml/dialog/jdepsDialog.fxml"))
      controller = JdepsDialogController(configuration.eventBus, userHistory, configuration.jdkEntries.values, p?.javaFXLib?.path, p?.mainJar, p?.classPath)
      loader.setController(controller)
      val root = loader.load<Pane>()
      stage.scene = Scene(root)
   }

   override fun result(): DialogResult<Boolean> {
      return DialogResult(ok = true, data = true)
   }
}