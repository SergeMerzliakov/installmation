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

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO

/**
 * Useful Image functions
 */
object ImageTool {

   enum class ImageType {
      Png
   }

   class Dimension(val width: Int, val height: Int)

   /**
    * Duplicates the image with new size
    */
   fun newImageWithSize(type: ImageType, imageFile: File, outputName: String, outputPath: File, newWidth: Int, newHeight: Int): File {
      try {
         val inputImage: BufferedImage = ImageIO.read(imageFile)
         val outputImage = BufferedImage(newWidth, newHeight, inputImage.type)

         val g2d = outputImage.createGraphics()
         g2d.drawImage(inputImage, 0, 0, newWidth, newHeight, null)
         g2d.dispose()

         val outputFile = File(outputPath, outputName)
         ImageIO.write(outputImage, type.name.toLowerCase(), outputFile)
         return outputFile
      } catch (e: Exception) {
         throw ImageProcessingException("Error creating new ${type.name.toLowerCase()} image from ${imageFile.path}", e)
      }
   }

   /*
  return width and height of image file
 */
   fun imageDimensions(f: File): Dimension {
      try {
         val imageTest: ByteArray = f.readBytes()
         val bs = ByteArrayInputStream(imageTest)
         val bi: BufferedImage = ImageIO.read(bs)
         return Dimension(bi.width, bi.height)
      } catch (e: Exception) {
         throw ImageProcessingException("Error determine image dimensions of ${f.path}", e)
      }
   }
}