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

import java.io.File

interface JDK {
   /**
    * User defined name or label
    */
   val name:String
   
   /*
    location full path
    */
   val path: File
   
   val operatingSystem: OperatingSystem.Type
   
   /**
    * java executable full path
    */
   val javaExecutable: File
   
   /**
    * Returns true of JPackage was found (in JDK 14 or later)
    */
   val supportsJPackage:Boolean

   /**
    * jpackage full path is part of JDK14+ used to build installs
    */
   val packageExecutable: File
}