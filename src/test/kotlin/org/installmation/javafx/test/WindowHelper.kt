/*
 * Copyright 2020 Serge Merzliakov
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

package org.installmation.javafx.test

import javafx.application.Platform
import javafx.stage.Stage
import javafx.stage.Window

/**
 * Used for testing System Dialogs which are hard to test with mocking
 * libraries.
 */
object WindowHelper {

   /**
    * Useful for JavaFx standard windows and dialogs i.e. the ones that
    * come with the frame and do not have a fx:id
    */
   fun verifyWindowVisible(title: String): Stage? {
      var retry = 50
      var windows = Window.getWindows()
      var match = windows.find { (it as Stage).title == title }
      if (match == null && --retry > 0) {
         Thread.sleep(100)
         windows = Window.getWindows()
         match = windows.find { (it as Stage).title == title }
      }
      if (match == null)
         throw RuntimeException("Window not found: $title")
      return match as Stage
   }

   fun closeWindow(w: Stage?) {
      Platform.runLater {
         w?.close()
      }
   }
}