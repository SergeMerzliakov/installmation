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
package org.installmation.controller

import javafx.collections.ObservableList
import javafx.util.StringConverter
import org.installmation.core.UserNamed


/**
 * factory for creating JFX String converters. Used by Containers to display string
 * representations of objects. StringConverter is a final class hence the factory
 * approach
 */
object StringConverterFactory {

   fun <T : UserNamed> namedItemConverter(items: ObservableList<T>): StringConverter<T> {
      return object : javafx.util.StringConverter<T>() {
         override fun toString(obj: T?): String? {
            return obj?.name
         }

         override fun fromString(name: String): T {
            return items.first { it.name == name }
         }
      }
   }
}