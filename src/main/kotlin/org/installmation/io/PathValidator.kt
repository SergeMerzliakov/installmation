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
package org.installmation.io

import java.io.File
import java.io.FileNotFoundException
import java.nio.file.InvalidPathException
import java.nio.file.Paths


object PathValidator {

   /**
    * Checks path syntax only
    */
   fun isValidPath(path: String?): Boolean {
      try {
         Paths.get(path)
      } catch (ex: InvalidPathException) {
         return false
      } catch (ex: NullPointerException) {
         return false
      }
      return true
   }

   /**
    * Get File in JDK
    * Throws FileNotFoundException if not found
    */
    fun verifyFile(fileRelativePath: File, fileName: String): File {
      val fullPath = File(fileRelativePath, fileName)
      if (!fullPath.exists())
         throw FileNotFoundException("JDK file '${fullPath.absolutePath}' not found. This may occur on JDK versions below 14 for some executables")

      return fullPath
   }
}