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
package org.installmation.model.binary

/**
 * Parse output of jdeps JDK tool into a list of dependencies
 */
class JDepsParser(output: List<String>) {

   val dependencies = mutableSetOf<String>()

   init {
      parse(output)
   }

   /**
    * output is of form:

   xxxx.jar -> java.base
   xxxx.jar -> javafx.base
   xxxx.jar -> javafx.controls
   xxxx.jar -> javafx.fxml
   xxxx.jar -> javafx.graphics
    
    */
   private fun parse(output: List<String>) {
      for(line in output){
         val parts = line.split(" -> ")
         if (parts.size == 2)
            dependencies.add(parts[1])
      }
      dependencies.remove("not found")
      dependencies.remove("java.base") // implicit in all modular applications
   }
}