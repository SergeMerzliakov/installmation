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
import org.installmation.io.PathValidator
import org.installmation.model.binary.JDK
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

   // TODO - Should all these be String/Value/Label type artefacts? 
   var name: String? = null
   var version: String? = null
   var mainClass: String? = null
   var mainJar: File? = null
   var installerType: String? = null
   var copyright: String? = null
   var jpackageJDK: JDK? = null
   // for JavaFX only - TODO
   var javaFXLib: NamedDirectory? = null
   var javaFXMods: NamedDirectory? = null
   var installJDK: JDK? = null // JDK to install with application
   var inputDirectory: File? = null
   var imageBuildDirectory: File? = null  //output
   var installerDirectory: File? = null  //output
   // not used now - in future for modular applications
   var modulePath = mutableSetOf<File>()
   var classPath = mutableSetOf<File>()

   fun projectFile(baseDirectory: File = Constant.DEFAULT_BASE_DIR): File {
      checkNotNull(name)
      val baseDir = File(baseDirectory, Constant.PROJECT_DIR)
      return File(baseDir, projectFileName(name!!))
   }

   fun validateConfiguration(): ValidationResult {
      val result = ValidationResult(true)
      validateStringField("Project Name", name, result)
      validateStringField("Project Version", version, result)
      if (validateFieldNotNull("Java JDK", jpackageJDK, result))
         validateExistingFileField("Java JDK Path", jpackageJDK?.path, result)
      validateFutureFileField("Image Build Directory", imageBuildDirectory, result)
      validateFutureFileField("Installer Directory", installerDirectory, result)
      for (m in modulePath)
         validateExistingFileField("Module Path Item", m, result)
      for (cp in classPath)
         validateExistingFileField("Class Path Item ", cp, result)

      return result
   }

   /**
    * Clear collections before state update and save
    */
   fun prepareForSave() {
      modulePath.clear()
      classPath.clear()
   }

   private fun validateStringField(fieldName: String, field: String?, result: ValidationResult) {
      if (field == null || field.isNullOrBlank()) {
         result.success = false
         result.errors.add("$fieldName Error - Field is empty")
      }
   }

   private fun validateFieldNotNull(fieldName: String, field: Any?, result: ValidationResult): Boolean {
      if (field == null) {
         result.success = false
         result.errors.add("$fieldName Error - Field is missing")
      }
      return result.success
   }

   /**
    * A File to be created  - just path is valid path and nothing else
    */
   private fun validateFutureFileField(fieldName: String, field: File?, result: ValidationResult) {
      val valid = PathValidator.isValidPath(field?.canonicalPath)
      if (!valid) {
         result.success = false
         result.errors.add("$fieldName Error - Invalid Path String '${field?.canonicalPath}'")
      }
   }

   private fun validateExistingFileField(fieldName: String, field: File?, result: ValidationResult) {
      if (field == null || !field.exists()) {
         result.success = false
         if (field == null)
            result.errors.add("$fieldName Error - File path empty")
         else
            result.errors.add("$fieldName Error - File path not found '${field.canonicalPath}'")
      }
   }

   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is InstallProject) return false

      if (name != other.name) return false
      if (version != other.version) return false
      if (jpackageJDK != other.jpackageJDK) return false
      if (modulePath != other.modulePath) return false
      if (installerDirectory != other.installerDirectory) return false
      if (imageBuildDirectory != other.imageBuildDirectory) return false
      return true
   }

   override fun hashCode(): Int {
      var result = name?.hashCode() ?: 0
      result = 31 * result + (version?.hashCode() ?: 0)
      result = 31 * result + (jpackageJDK?.hashCode() ?: 0)
      result = 31 * result + modulePath.hashCode()
      result = 31 * result + (imageBuildDirectory?.hashCode() ?: 0)
      result = 31 * result + (installerDirectory?.hashCode() ?: 0)
      return result
   }
}