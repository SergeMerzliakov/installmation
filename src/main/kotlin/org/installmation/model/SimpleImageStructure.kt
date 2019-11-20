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

import java.io.File

/**
 * Simplest image structure for now:
 *   single directory with:
 *       main jar file in root dir
 *       other files in root dir
 *       /lib dir containing all dependencies
 */
class SimpleImageStructure : ImageStructure {
   var mainJar: String? = null
   private val fileNames: MutableSet<String> = mutableSetOf()
   private val directories: MutableSet<File> = mutableSetOf()

   override fun addFile(name: String) {
      val trimmed = name.trim()
      check(trimmed.isNotEmpty())
      fileNames.add(trimmed)
   }

   override fun addDirectory(name: String) {
      val trimmed = name.trim()
      check(trimmed.isNotEmpty())
      //TODO better checks for a subdirectory
      directories.add(File(trimmed))
   }

   override fun getFiles(): Set<String> {
      return fileNames
   }

   override fun getDirectories(): Set<File> {
      return directories
   }

   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is SimpleImageStructure) return false

      if (mainJar != other.mainJar) return false
      if (fileNames != other.fileNames) return false
      if (directories != other.directories) return false

      return true
   }

   override fun hashCode(): Int {
      var result = mainJar?.hashCode() ?: 0
      result = 31 * result + fileNames.hashCode()
      result = 31 * result + directories.hashCode()
      return result
   }


}