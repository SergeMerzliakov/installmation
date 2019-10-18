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
import java.io.FileNotFoundException

class MacJDK(private val path: File) : JavaJDK {

   companion object {
      private const val BIN = "Contents/Home/bin"
      private const val EXE_JAVA = "java"
      private const val EXE_PACKAGE = "jpackage"
   }

   init {
      if (!path.exists())
         throw FileNotFoundException("JDK path '${path.absolutePath}' not found")
   }

   override val javaExecutable: File
      get() = getJDKFile(BIN, EXE_JAVA)

   override val supportsJPackage: Boolean
      get() = File(File(path, BIN), EXE_PACKAGE).exists()

   override val packageExecutable: File
      get() = getJDKFile(BIN, EXE_PACKAGE)

   /**
    * Get File in JDK
    * Throws FileNotFoundException if not found
    */
   private fun getJDKFile(fileRelativePath: String, fileName: String): File {
      val fullPath = File(File(path,fileRelativePath), fileName)
      if (!fullPath.exists())
         throw FileNotFoundException("JDK file '${fullPath.absolutePath}' not found")

      return fullPath
   }
}