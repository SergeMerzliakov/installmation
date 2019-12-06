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

package org.installmation.core

/**
 * Utilities for OS-dependent variables or parameters
 */
object OperatingSystem {

   enum class Type {
      Windows,
      OSX,
      Linux
   }

   /**
    * Return current os as an enumerated type
    */
   fun os(): Type {
      val name = System.getProperty("os.name").trim().toLowerCase()
      val version = System.getProperty("os.version").trim().toLowerCase()

      when {
         name.startsWith("mac") -> return Type.OSX
         name.startsWith("windows") -> return Type.Windows
         name.startsWith("linux") -> return Type.Linux
      }

      throw RuntimeException("Unknown operating system - $name $version")
   }

   /**
    * Return image extension for each OS
    */
   fun imageFileExtension(): String {
      val name = System.getProperty("os.name").trim().toLowerCase()

      when {
         name.startsWith("mac") -> return ".app"
         name.startsWith("windows") -> return ".exe"
         name.startsWith("linux") -> return ""
      }
      throw RuntimeException("Unknown operating system - $name ${System.getProperty("os.version").trim().toLowerCase()}")
   }
}