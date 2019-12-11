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

import org.installmation.core.OperatingSystem
import java.io.File

class MacJDK(usersJDKName: String, path: File) : AbstractJDK(usersJDKName, path) {

   override val binaryDirectory = "bin" 
   override val javaExecutableName = "java"
   override val jpackageExecutableName = "jpackage"
   override val jdepsExecutableName = "jdeps"
   
   override val operatingSystem: OperatingSystem.Type = OperatingSystem.Type.OSX
}