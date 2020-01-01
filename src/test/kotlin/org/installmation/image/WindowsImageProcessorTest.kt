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
import org.installmation.TestConstants
import org.installmation.WindowsOnlyTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(value = WindowsOnlyTest::class)
class WindowsImageProcessorTest {

   companion object {
      val OUTPUT_DIR = File(TestConstants.TEST_TEMP_DIR)
      val IMAGE = File("src/test/resources/image/green16.png")
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
   fun shouldCreateICOFile() {
      val win = WindowsImageProcessor()
      val ico = win.createApplicationLogo(IMAGE, OUTPUT_DIR)
      assertThat(ico).exists().isFile()
      assertThat(ico.extension).isEqualTo(ImageTool.ImageType.Ico.value)
      assertThat(ico.length()).isNotZero()
   }
   
   @Test(expected = ImageProcessingException::class)
   fun shouldFailIfImageNotFound() {
      val win = WindowsImageProcessor()
      win.createApplicationLogo(File("wrong/dir/green16.png"), OUTPUT_DIR)
   }
}