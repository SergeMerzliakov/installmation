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

package org.installmation.model

import org.junit.Test

class JPackageExecutableTest {

   companion object {
      const val JDK_14_BUILD49 = "14-jpackage"
   }

   //@Test
   fun shouldGetVersionEarlyAccessJdk14() {
// TODO refactor with ProcessOutput
//      val jdk = JDKFactory.create(OperatingSystem.os(), "test", File("src/test/resources/${OperatingSystem.testDirectory()}/jpackage49"))
//      val mockPackage = spyk(JPackageExecutable(jdk))
//      every { mockPackage.execute() }.returns(listOf("WARNING: Using experimental tool jpackage", JDK_14_BUILD49))
//      assertThat(mockPackage.queryVersion()).isEqualTo(JDK_14_BUILD49)
   }
}