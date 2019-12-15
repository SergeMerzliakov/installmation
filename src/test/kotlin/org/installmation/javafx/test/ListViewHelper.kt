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

import javafx.collections.FXCollections
import javafx.scene.control.ListView
import org.testfx.framework.junit.ApplicationTest

/**
 * JavaFX util functions for ListViews
 */
class ListViewHelper(private val test: ApplicationTest) {

   fun <T> populateItems(id: String, vararg item: T) {
      val model = FXCollections.observableArrayList<T>()
      val fxId = FXIdentity.cleanFxId(id)
      val view = test.lookup(fxId).query<ListView<T>>()
      view.items = model
      for (i in item)
         model.add(i)
   }

}