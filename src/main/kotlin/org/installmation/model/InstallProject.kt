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
import org.installmation.core.OperatingSystem
import org.installmation.image.ImageTool
import org.installmation.io.PathValidator
import org.installmation.model.binary.JDK
import java.io.File

/**
 * Contains all the details for an installation project
 * The simplest model is used - to create a project and 
 * then set all it's properties.
 */
class InstallProject(var name: String? = null) {
   
   companion object{
        fun projectFileName(name: String): String {
         return "$name.json"
      }
   }
   var version: String = "1.1"
   var mainClass: String? = null
   var vendor: String? = null
   var mainJar: File? = null
   var jvmArguments:String? = null
   var installerType: String? = null
   var copyright: String? = null
   var jpackageJDK: JDK? = null
   var applicationLogo: File? = null
   var javaFXLib: NamedDirectory? = null
   var javaFXMods: NamedDirectory? = null
   var installJDK: JDK? = null // JDK to install with application
   var inputDirectory: File? = null
   var imageBuildDirectory: File? = null  //output
   var installerDirectory: File? = null  //output
   // not used now - in future for modular applications
   var customModules = mutableSetOf<String>()
   var classPath = mutableSetOf<File>()

   // OSX - sign application options - not implemented
   var packageIdentifier: String? = null
   var packageName: String? = null
   var signPrefix: String? = null

   // OSX - sign installer options
   var signInstaller: Boolean = false
   var appleInstallerCertName: String? = null
   var appleInstallerKeyChain: File? = null

   fun projectFile(baseDirectory: File): File {
      checkNotNull(name)
      val baseDir = File(baseDirectory, Constant.PROJECT_DIR)
      return File(baseDir, projectFileName(name!!))
   }

   fun hasValidName(): Boolean {
      return name != null && !name.isNullOrEmpty()
   }

   fun validateConfiguration(): ValidationResult {
      val result = ValidationResult(true)
      validateStringField("Project Name", name, result)
      validateStringField("Project Version", version, result)
      validateOptionalImageFileField("Application Logo", applicationLogo, result)
      if (validateFieldNotNull("Java JDK", jpackageJDK, result))
         validateExistingFileField("Java JDK Path", jpackageJDK?.path, result)
      validateExistingFileField("Main Jar File", mainJar, result)
      validateStringField("Main Class", mainClass, result)
      validateStringField("Installer Type", installerType, result)

      if (classPath.isEmpty()) {
         result.errors.add("Class path was empty - should have a least one entry for your code")
      } else {
         for (cp in classPath)
            validateExistingFileField("Class Path Item ", cp, result)
      }

      validateOSXFields(result)
      return result
   }

   /**
    * Signing fields 
    */
   private fun validateOSXFields(result: ValidationResult) {
      if (OperatingSystem.os() != OperatingSystem.Type.OSX)
         return
      
      if (signInstaller) {
         validateExistingFileField("Signing Keychain", appleInstallerKeyChain, result)
         validateStringField("Apple Installer Certificate", appleInstallerCertName, result)
      }
   }

   /**
    * Clear collections before state update and save
    */
   fun prepareForSave() {
      customModules.clear()
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

   private fun validateOptionalImageFileField(fieldName: String, field: File?, result: ValidationResult) {
      if (field == null)
         return
      if (!validateExistingFileField(fieldName, field, result))
         return
      // file exists
      val validImage = ImageTool.isValidImageFile(field)

      if (!validImage) {
         result.success = false
         result.errors.add("$fieldName Error - Image File '${field.path}' is not supported. Supported image types: ${ImageTool.validImageTypes()}")
      }
      // file is a valid image
   }

   private fun validateExistingFileField(fieldName: String, field: File?, result: ValidationResult): Boolean {
      var success = true
      if (field == null || !field.exists()) {
         success = false
         result.success = false
         if (field == null)
            result.errors.add("$fieldName Error - File path empty")
         else
            result.errors.add("$fieldName Error - File path not found '${field.path}'")
      }
      return success
   }

   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is InstallProject) return false

      if (name != other.name) return false
      if (version != other.version) return false
      if (jpackageJDK != other.jpackageJDK) return false
      if (customModules != other.customModules) return false
      if (installerDirectory != other.installerDirectory) return false
      if (imageBuildDirectory != other.imageBuildDirectory) return false
      return true
   }

   override fun hashCode(): Int {
      var result = name?.hashCode() ?: 0
      result = 31 * result + (version.hashCode())
      result = 31 * result + (jpackageJDK?.hashCode() ?: 0)
      result = 31 * result + customModules.hashCode()
      result = 31 * result + (imageBuildDirectory?.hashCode() ?: 0)
      result = 31 * result + (installerDirectory?.hashCode() ?: 0)
      return result
   }
}
