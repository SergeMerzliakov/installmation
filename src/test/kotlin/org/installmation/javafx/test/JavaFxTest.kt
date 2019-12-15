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
package org.installmation.javafx.test

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import org.assertj.core.api.Assertions.assertThat
import org.testfx.framework.junit.ApplicationTest
import org.testfx.util.WaitForAsyncUtils

/**
 * Contains useful helper methods, as we are forced to
 * inherit from TestFx class ApplicationTest
 */
abstract class JavaFxTest : ApplicationTest() {

   fun writeTextField(id: String, value: String) {
      val fxId = correctFxId(id)
      clickOn(fxId)

      val field = lookup(fxId).query<TextField>()
      assertThat(field).isNotNull
      field.text = null // clear first - this appears the simplest way
      write(value)
   }

   fun <T> selectComboByIndex(id: String, index: Int): T {
      var installerCombo: ComboBox<T>? = null
      Platform.runLater {
         val fxId = correctFxId(id)
         clickOn(fxId)
         installerCombo = lookup(fxId).query()
         installerCombo?.selectionModel?.select(index)
      }
      // wait a bit for JavaFX Thread to execute. FXRobot is by design quite slow
      WaitForAsyncUtils.waitForFxEvents(3)
      return installerCombo?.selectionModel!!.selectedItem
   }

   fun <T> populateCombo(id: String, vararg item: T) {
      val model = FXCollections.observableArrayList<T>()
      val fxId = correctFxId(id)
      val combo = lookup(fxId).query<ComboBox<T>>()
      combo.items = model
      for (i in item)
         model.add(i)
   }

   fun <T> populateListView(id: String, vararg item: T) {
      val model = FXCollections.observableArrayList<T>()
      val fxId = correctFxId(id)
      val view = lookup(fxId).query<ListView<T>>()
      view.items = model
      for (i in item)
         model.add(i)
   }

   /**
    * Always return a complete fxID for TestFX, which starts with '#' character
    */
   private fun correctFxId(id: String): String {
      var fxId = id
      if (!fxId.startsWith('#'))
         fxId = "#$id"
      return fxId
   }
}