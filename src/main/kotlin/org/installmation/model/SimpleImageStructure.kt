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
   private val fileNames: MutableCollection<String> = mutableSetOf()
   private val directories: MutableCollection<File> = mutableSetOf()

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

   override fun getFiles(): Collection<String> {
      return fileNames
   }

   override fun getDirectories(): Collection<File> {
      return directories
   }
}