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

import net.sf.image4j.codec.ico.ICOEncoder
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO


class WindowsImageProcessor : ImageProcessor {

   // file must an .ico file, which is then created
   override fun createApplicationLogo(imageFile: File, destination: File): File {
      try {
         val bufferedImage = ImageIO.read(imageFile)
         val icoFile = File(destination, imageFile.nameWithoutExtension + ".ico")
         ICOEncoder.write(bufferedImage, icoFile)
         return icoFile
      } catch (e: IOException) {
         throw ImageProcessingException("Error creating .ico file ${destination.path}", e)
      }
   }
}