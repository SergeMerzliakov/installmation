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
package org.installmation.core

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Date formatting helper functions
 */
object DateUtils {

   private val messageFormat = DateTimeFormatter.ofPattern("d-MMM-uuuu HH:mm:ss")

   fun now(): String {
      return displayDate(OffsetDateTime.now())
   }

   fun displayDate(d: OffsetDateTime): String {
      // month names have period eg. Feb. - strip
      return d.format(messageFormat).replace(".", "")
   }
}
