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

import org.installmation.configuration.Constant
import org.installmation.model.binary.JPackageExecutable
import java.io.File

/**
 * Contains all the details for an installation project
 * The simplest model is used - to create a project and 
 * then set all it's properties.
 */
class InstallProject {
   
   companion object{
        fun projectFileName(name: String): String {
         return "$name.json"
      }
   }
   var name: String? = null
   var version: String? = null
   var jpackage: JPackageExecutable? = null
   var modulePath:File? = null
   var imageStructure: ImageStructure? = null
   var imageContentDirectory: File? = null  // application content defined by imageStructure
   var imageBuildDirectory: File? = null  //output
   
   val artefacts = mutableMapOf<String, InstallArtefact>()

   fun projectFile(baseDirectory: File = Constant.DEFAULT_BASE_DIR): File {
      checkNotNull(name)
      val baseDir = File(baseDirectory, Constant.PROJECT_DIR)
      return File(baseDir, projectFileName(name!!))
   }
}