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

import javafx.stage.Modality
import javafx.stage.Stage

/**
 * Superclass of all non-trivial custom dialogs which are essential modal windows
 * Not for standard message or warning dialogs - use JavaFX dialogs for those
 */
abstract class CustomDialog<T>(ownerStage:Stage, title:String) {

   private var result: T? = null

   var stage: Stage = Stage()

   init {
      stage.title = title
      stage.initOwner(ownerStage)
   }

   /**
    * override this to return result from dialog controller
    */
   abstract fun result(): DialogResult<T>

   /**
    * Show dialog modally
    */
   fun showAndWait(): DialogResult<T> {
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.showAndWait()
      return result()
   }
}