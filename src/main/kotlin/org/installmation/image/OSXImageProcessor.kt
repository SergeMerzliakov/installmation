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

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.model.FlagArgument
import org.installmation.model.ValueArgument
import org.installmation.model.binary.IconsetExecutable
import java.io.File


/**
 * Converts JPG and PNG files into correct image format for each operating system
 *
 *    OSX - > ICNS format
 *    Windows ->
 */
class OSXImageProcessor : ImageProcessor {

   companion object {
      val log: Logger = LogManager.getLogger(OSXImageProcessor::class.java)
      const val ICONSET_DIR = "AppIcon.iconset"
   }

   // need to call iconutil tool with result of this call
   override fun createApplicationLogo(imageFile: File, destination: File): File {
      if (imageFile.exists() && imageFile.extension == "icns"){
         val moved = File(destination, imageFile.name)
         imageFile.copyTo(moved, true)
         return moved
      }
      
      try {
         val iconsetDir = File(destination, ICONSET_DIR)
         iconsetDir.deleteRecursively()
         iconsetDir.mkdirs()

         ImageTool.newImageWithSize(ImageTool.ImageType.Png, imageFile, "icon_16x16.png", iconsetDir, 16, 16)
         ImageTool.newImageWithSize(ImageTool.ImageType.Png, imageFile, "icon_16x16@2x.png", iconsetDir, 32, 32)
         ImageTool.newImageWithSize(ImageTool.ImageType.Png, imageFile, "icon_32x32.png", iconsetDir, 32, 32)
         ImageTool.newImageWithSize(ImageTool.ImageType.Png, imageFile, "icon_32x32@2x.png", iconsetDir, 64, 64)
         ImageTool.newImageWithSize(ImageTool.ImageType.Png, imageFile, "icon_128x128.png", iconsetDir, 128, 128)
         ImageTool.newImageWithSize(ImageTool.ImageType.Png, imageFile, "icon_128x128@2x.png", iconsetDir, 256, 256)
         ImageTool.newImageWithSize(ImageTool.ImageType.Png, imageFile, "icon_256x256.png", iconsetDir, 256, 256)
         ImageTool.newImageWithSize(ImageTool.ImageType.Png, imageFile, "icon_256x256@2x.png", iconsetDir, 512, 512)
         ImageTool.newImageWithSize(ImageTool.ImageType.Png, imageFile, "icon_512x512.png", iconsetDir, 512, 512)
         ImageTool.newImageWithSize(ImageTool.ImageType.Png, imageFile, "icon_512x512@2x.png", iconsetDir, 1024, 1024)

         val icns = generateICNS(imageFile.nameWithoutExtension, destination, iconsetDir)
         iconsetDir.deleteRecursively()
         return icns
      } catch (e: Exception) {
         val msg = "Error generating ICNS on OSX. Source image: ${imageFile.path} into ${destination.path}"
         log.error(msg, e)
         throw ImageProcessingException(msg, e)
      }
   }


   private fun generateICNS(icnsName: String, destination: File, iconset: File): File {
      val icns = IconsetExecutable()
      val outputFile = File(destination, "$icnsName.icns")
      log.debug("Generating ICNS file ${outputFile.path}.....")
      icns.parameters.addArgument(ValueArgument("-c", "icns"))
      icns.parameters.addArgument(ValueArgument("--output", outputFile.path))
      icns.parameters.addArgument(FlagArgument(iconset.path))
      val output = icns.execute(30)
      if (!output.hasErrors())
         log.info("Generated ICNS file $outputFile.path")
      else
         throw ImageProcessingException("Errors with iconutil when generating ICNS file: ${output.errors.joinToString(".")}")
      return outputFile
   }

}