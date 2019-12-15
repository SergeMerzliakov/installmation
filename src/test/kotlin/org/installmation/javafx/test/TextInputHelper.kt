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

import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import org.testfx.framework.junit.ApplicationTest
import org.testfx.service.query.EmptyNodeQueryException

/**
 * JavaFX util functions for nodes with Text Input
 */
class TextInputHelper(private val test: ApplicationTest) {

   fun writeText(id: String, value: String) {
      val fxId = FXIdentity.cleanFxId(id)
      test.clickOn(fxId)

      var field: TextInputControl
      try {
         field = test.lookup(fxId).query<TextField>()
      } catch (e: EmptyNodeQueryException) {
         field = test.lookup(fxId).query<TextArea>()
      }
      field.text = null // clear first - this appears the simplest way
      test.write(value)
   }
}