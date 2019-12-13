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

import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import org.assertj.core.api.Assertions.assertThat
import org.testfx.framework.junit.ApplicationTest

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
      val fxId = correctFxId(id)
      clickOn(fxId)
      val installerCombo = lookup(fxId).query<ComboBox<T>>()
      installerCombo.selectionModel.select(0)
      return installerCombo.selectionModel.selectedItem
   }

   /**
    * Always return a complete fxID for TestFX, starts with '#' character
    */
   private fun correctFxId(id: String): String {
      var fxId = id
      if (!fxId.startsWith('#'))
         fxId = "#$id"
      return fxId
   }
}