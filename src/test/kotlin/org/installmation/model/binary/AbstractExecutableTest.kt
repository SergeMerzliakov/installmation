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
package org.installmation.model.binary

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.assertj.core.api.Assertions.assertThat
import org.installmation.OSXOnlyTest
import org.installmation.model.FlagArgument
import org.junit.Test
import org.junit.runner.RunWith

// TODO get this working with windows
@RunWith(value = OSXOnlyTest::class)
class AbstractExecutableTest {

	private val eventBus = EventBus()

	@Test
	fun executeShouldSucceed() {
		val ls = ListDirExecutable(eventBus)
		val output = ls.execute() // in repo directory which always exists
		assertThat(output.success()).isTrue()
		assertThat(output.output).isNotEmpty()
		assertThat(output.errors()).isEmpty()
	}

	@Test
	fun executeShouldFail() {
		val ls = ListDirExecutable(eventBus)
		ls.parameters.addArgument(FlagArgument("/no/find/this/dir"))
		val output = ls.execute()
		assertThat(output.success()).isFalse()
		assertThat(output.output).isEmpty()
		assertThat(output.errors()).isNotEmpty()
	}
}