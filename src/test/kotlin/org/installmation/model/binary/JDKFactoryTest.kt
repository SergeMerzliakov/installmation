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
import org.installmation.core.OperatingSystem
import org.junit.Test
import java.io.File

class JDKFactoryTest {

   @Test
   fun shouldCreateOSXJDK() {
      val jdk = JDKFactory.create(OperatingSystem.Type.OSX, "mac1", File("/usr/mac/jdk13"))
      assertThat(jdk).isInstanceOf(MacJDK::class.java)
   }

   @Test
   fun shouldCreateWindowsJDK() {
      val jdk = JDKFactory.create(OperatingSystem.Type.Windows, "windows1", File("C:\\java\\jdk13"))
      assertThat(jdk).isInstanceOf(WindowsJDK::class.java)
   }

   @Test
   fun shouldCreateLinuxJDK() {
      val jdk = JDKFactory.create(OperatingSystem.Type.Linux, "linux", File("/usr/ubuntu/jdk13"))
      assertThat(jdk).isInstanceOf(LinuxJDK::class.java)
   }
}