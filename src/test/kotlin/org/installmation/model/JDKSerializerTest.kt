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

import org.assertj.core.api.Assertions.assertThat
import org.installmation.SerializationUtils
import org.installmation.model.binary.LinuxJDK
import org.installmation.model.binary.MacJDK
import org.installmation.model.binary.WindowsJDK
import org.junit.Test
import java.io.File

class JDKSerializerTest {

   @Test
   fun shouldSerializeOSX() {
      val gson = SerializationUtils.createAdapterGson(MacJDK::class.java)

      val jdk = MacJDK("myJDK", File("dir/java14"))
      val data = gson.toJson(jdk, MacJDK::class.java)

      val loadedJdk = gson.fromJson(data, MacJDK::class.java)
      assertThat(loadedJdk).isEqualTo(jdk)
   }

   @Test
   fun shouldSerializeWindows() {
      val gson = SerializationUtils.createAdapterGson(WindowsJDK::class.java)

      val jdk = WindowsJDK("myJDK", File("C:\\java\\java14"))
      val data = gson.toJson(jdk, WindowsJDK::class.java)

      val loadedJdk = gson.fromJson(data, WindowsJDK::class.java)
      assertThat(loadedJdk).isEqualTo(jdk)
   }
   
   @Test
   fun shouldSerializeLinux() {
      val gson = SerializationUtils.createAdapterGson(LinuxJDK::class.java)

      val jdk = LinuxJDK("myJDK", File("/usr/var/java14"))
      val data = gson.toJson(jdk, LinuxJDK::class.java)

      val loadedJdk = gson.fromJson(data, LinuxJDK::class.java)
      assertThat(loadedJdk).isEqualTo(jdk)
   }
}