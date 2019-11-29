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

import org.assertj.core.api.Assertions
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class DateUtilsTest {

   @Test
   fun shouldGenerateDisplayDate() {
      val d = OffsetDateTime.of(2000, 7, 23, 9, 45, 46, 999, ZoneOffset.UTC)
      val display = DateUtils.displayDate(d)
      Assertions.assertThat(display).isEqualTo("23-Jul-2000 09:45:46")
   }
}