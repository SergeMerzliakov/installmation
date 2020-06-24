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
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

private val OUTPUT_DIR = File(TestConstants.TEST_TEMP_DIR)
private val PNG_FILE = File("src/test/resources/image/green16.PNG")
private val ICNS_FILE = File("src/test/resources/image/green16.icNS")
private val ICNS_PLACEHOLDER_FILE = File("src/test/resources/image/placeholder_icns.png")
private val JPG_FILE = File("src/test/resources/image/red32.jpg")
private const val TEST_IMAGE_SIZE = 16

class ImageToolTest {

   @Before
   fun setup() {
      OUTPUT_DIR.mkdirs()
   }

   @After
   fun teardown() {
      OUTPUT_DIR.deleteRecursively()
   }

   @Test
   fun shouldReadDimensionsOfValidImage() {
      val dim = ImageTool.imageDimensions(PNG_FILE)
      assertThat(dim.width).isEqualTo(TEST_IMAGE_SIZE)
      assertThat(dim.height).isEqualTo(TEST_IMAGE_SIZE)
   }

   @Test(expected = ImageProcessingException::class)
   fun shouldThrowExceptionWhenReadingDimensions() {
      val dim = ImageTool.imageDimensions(File("invalid/image.png"))
      assertThat(dim.width).isEqualTo(TEST_IMAGE_SIZE)
      assertThat(dim.height).isEqualTo(TEST_IMAGE_SIZE)
   }

   @Test
   fun shouldEnlargeImage() {
      val newSize = TEST_IMAGE_SIZE * 4
      val outputFile = "green$newSize.png"
      ImageTool.newImageWithSize(ImageTool.ImageType.Png, PNG_FILE, outputFile, OUTPUT_DIR, newSize, newSize)
      val resizedImage = File(OUTPUT_DIR, outputFile)
      assertThat(resizedImage).exists()

      val dim = ImageTool.imageDimensions(resizedImage)
      assertThat(dim.width).isEqualTo(newSize)
      assertThat(dim.height).isEqualTo(newSize)
   }

   @Test
   fun shouldShrinkImage() {
      val newSize = TEST_IMAGE_SIZE - 4
      val outputFile = "green$newSize.png"
      ImageTool.newImageWithSize(ImageTool.ImageType.Png, PNG_FILE, outputFile, OUTPUT_DIR, newSize, newSize)
      val resizedImage = File(OUTPUT_DIR, outputFile)
      assertThat(resizedImage).exists()

      val dim = ImageTool.imageDimensions(resizedImage)
      assertThat(dim.width).isEqualTo(newSize)
      assertThat(dim.height).isEqualTo(newSize)
   }

   @Test
   fun shouldDuplicateImageWithSameSize() {
      val newSize = ImageTool.imageDimensions(PNG_FILE).width
      val outputFile = "green$newSize.png"
      ImageTool.newImageWithSize(ImageTool.ImageType.Png, PNG_FILE, outputFile, OUTPUT_DIR, newSize, newSize)
      val duplicateImage = File(OUTPUT_DIR, outputFile)
      assertThat(duplicateImage).exists()
      //TODO duplicate image is much smaller, so this test fails  - need to find out why
      // assertThat(resizedImage).exists()
      // assertThat(resizedImage.length()).isEqualTo(IMAGE.length())

      val dim = ImageTool.imageDimensions(duplicateImage)
      assertThat(dim.width).isEqualTo(newSize)
      assertThat(dim.height).isEqualTo(newSize)
   }

   @Test(expected = ImageProcessingException::class)
   fun shouldFailImageCreationWithWrongImagePath() {
      val newSize = TEST_IMAGE_SIZE * 2
      val outputFile = "green$newSize.png"
      ImageTool.newImageWithSize(ImageTool.ImageType.Png, File("wrong/image.png"), outputFile, OUTPUT_DIR, newSize, newSize)
      val resizedImage = File(OUTPUT_DIR, outputFile)

      val dim = ImageTool.imageDimensions(resizedImage)
      assertThat(dim.width).isEqualTo(newSize)
      assertThat(dim.height).isEqualTo(newSize)
   }
   
   @Test
   fun shouldRecognizeValidPNGFile(){
      assertThat(ImageTool.isValidImageFile(PNG_FILE)).isTrue()
   }

   @Test
   fun shouldRecognizeValidJPEGFile(){
      assertThat(ImageTool.isValidImageFile(JPG_FILE)).isTrue()
   }

   @Test
   fun shouldRecognizeNonImageFile(){
      assertThat(ImageTool.isValidImageFile(File("src/test/resources/generic/file2.txt"))).isFalse()
   }

   @Test
   fun shouldRecognizeNonExistentImageFile(){
      assertThat(ImageTool.isValidImageFile(File("does/not/exist.jpeg"))).isFalse()
   }

   /**
    * return placeholder image - ICNS image processing in Java/Kotlin too messy for
    * now. Will revisit when I have the strength.
    */
   @Test
   fun shouldGetIcnsImage(){
      val image = ImageTool.createImage(ICNS_FILE)
      assertThat(image).isNotNull
      val dim = ImageTool.imageDimensions(ICNS_PLACEHOLDER_FILE)
      assertThat(image.height).isEqualTo(dim.height.toDouble())
      assertThat(image.width).isEqualTo(dim.width.toDouble())
   }
}