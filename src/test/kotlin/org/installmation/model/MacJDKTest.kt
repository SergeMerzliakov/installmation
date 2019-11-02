/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
**/

package org.installmation.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.installmation.model.binary.MacJDK
import org.installmation.testutil.TestFile
import org.junit.Test
import java.io.FileNotFoundException

class MacJDKTest {

   @Test
   fun shouldFindExecutablesInJDKPrior14() {
      val f = TestFile.resourceFile("mac/jdk13")
      val mac = MacJDK("myJDK", f)
      assertThat(mac.javaExecutable).exists()
      assertThatExceptionOfType(FileNotFoundException::class.java).isThrownBy { mac.packageExecutable }
      assertThat(mac.supportsJPackage).isFalse()
   }

   @Test
   fun shouldFindAllExecutablesInJDK14() {
      val f = TestFile.resourceFile("mac/jpackage49")
      val mac = MacJDK("myJDK", f)
      assertThat(mac.javaExecutable).exists()
      assertThat(mac.supportsJPackage).isTrue()
      assertThat(mac.packageExecutable).exists()
   }
}