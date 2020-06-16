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
package org.installmation.configuration

import com.google.gson.Gson
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class UserHistoryTest {


	@Test
	fun shouldSetValue() {
		val hist = UserHistory()
		hist.set("name", "john")
		assertThat(hist.get("name")).isEqualTo("john")
	}


	@Test
	fun shouldNotGetWrongValue() {
		val hist = UserHistory()
		hist.set("name", "john")
		assertThat(hist.get("wrong")).isNull()
	}


	@Test
	fun shouldSerialize() {
		val hist = UserHistory()
		hist.set("name", "john")
		hist.set("path", File("/usr/home"))

		val gson = Gson()
		val data = gson.toJson(hist)

		val hist2 = gson.fromJson(data, UserHistory::class.java)

		assertThat(hist2).isEqualToComparingFieldByField(hist)
	}

}