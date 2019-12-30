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
package org.installmation.image

import org.assertj.core.api.Assertions.assertThat
import org.installmation.OSXOnlyTest
import org.installmation.TestConstants
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(value = OSXOnlyTest::class)
class OSXImageProcessorTest {

   companion object {
      val OUTPUT_DIR = File(TestConstants.TEST_TEMP_DIR)
      val IMAGE = File("src/test/resources/image/green16.png")
      val ICNS_IMAGE = File("src/test/resources/image/green16.icns")
   }

   @Before
   fun setup() {
      OUTPUT_DIR.mkdirs()
   }

   @After
   fun teardown() {
      OUTPUT_DIR.deleteRecursively()
   }

   @Test
   fun shouldCreateICNSFile() {
      val osx = OSXImageProcessor()
      val icns = osx.createApplicationLogo(IMAGE, OUTPUT_DIR)
      assertThat(icns).exists().isFile()
      assertThat(icns.length()).isNotZero()
   }
   
   @Test
   fun shouldHandleExistingICNSFile() {
      val osx = OSXImageProcessor()
      val icns = osx.createApplicationLogo(ICNS_IMAGE, OUTPUT_DIR)
      // icns file should just be moved to output directory
      assertThat(icns).exists().isFile()
      assertThat(icns.length()).isNotZero()
   }

   @Test(expected = ImageProcessingException::class)
   fun shouldFailIfImageNotFound() {
      val osx = OSXImageProcessor()
      osx.createApplicationLogo(File("wrong/dir/green16.png"), OUTPUT_DIR)
   }
}