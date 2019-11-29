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

import java.io.File

/**
 * Helper functions not found elsewhere
 */
object CollectionUtils {

   /**
    * Create a string like a classpath entry
    */
   fun toClasspath(items: Collection<String>): String {
      val buf = StringBuilder()
      items.forEach{ buf.append(it).append(File.pathSeparatorChar)}
      if (buf.isNotEmpty())
         buf.setLength(buf.length - 1)
      return buf.toString()
   }
   
}