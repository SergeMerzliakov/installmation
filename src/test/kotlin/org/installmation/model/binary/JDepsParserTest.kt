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
package org.installmation.model.binary

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class JDepsParserTest {

   @Test
   fun shouldIgnoreJavaBase() {
      val output = listOf("target.jar -> java.base", "target.jar -> javafx.base")

      val jp = JDepsParser(output)
      assertThat(jp.dependencies).hasSize(1)
      assertThat(jp.dependencies).contains("javafx.base")
   }

   @Test
   fun shouldIgnoreNotFound() {
      val output = listOf("target.jar -> not found", "target.jar -> javafx.base")

      val jp = JDepsParser(output)
      assertThat(jp.dependencies).hasSize(1)
      assertThat(jp.dependencies).contains("javafx.base")
   }

   @Test
   fun shouldParseValidOutput() {
      val output = listOf("target.jar -> javafx.base", "target.jar -> javafx.graphics")

      val jp = JDepsParser(output)
      assertThat(jp.dependencies).hasSize(2)
      assertThat(jp.dependencies).contains("javafx.base", "javafx.graphics")
   }

   @Test
   fun shouldParseErrorOutput() {
      val output = listOf("Warning: Path does not exist: target.jar")

      val jp = JDepsParser(output)
      assertThat(jp.dependencies).hasSize(0)
   }

   @Test
   fun shouldIgnoreJunkOutput() {
      val output = listOf("dsafdefedsfds", "3erdsfdsfredf", "")

      val jp = JDepsParser(output)
      assertThat(jp.dependencies).hasSize(0)
   }

   @Test
   fun shouldIgnoreEmptyOutput() {
      val output = listOf("", "", "")
      val jp = JDepsParser(output)
      assertThat(jp.dependencies).hasSize(0)
   }


   @Test
   fun shouldParseEmptyOutput() {
      val output = listOf<String>()

      val jp = JDepsParser(output)
      assertThat(jp.dependencies).hasSize(0)
   }
}