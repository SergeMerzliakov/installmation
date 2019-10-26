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

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


class SingleValueDialogController(val label: String, private val defaultValue: String?) {

   private var saved: Boolean = false
   @FXML lateinit var itemLabel: Label
   @FXML lateinit var itemValue: TextField

   @FXML
   fun initialize() {
      itemLabel.text = label
      //check if editing an existing value
      if (defaultValue?.isNotEmpty() == true) {
         itemValue.text = defaultValue
      }
   }

   /**
    * return null if cancelled dialog
    */
   fun getValue(): String? {
      if (saved)
         return itemValue.text.trim()
      return null
   }

   @FXML
   fun save() {
      saved = true
      val stage = itemLabel.scene.window as Stage
      stage.close()
   }

   @FXML
   fun cancel() {
      saved = false
      val stage = itemLabel.scene.window as Stage
      stage.close()
   }

}