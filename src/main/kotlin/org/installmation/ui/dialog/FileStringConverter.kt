package org.installmation.ui.dialog

import javafx.util.StringConverter
import java.io.File

class FileStringConverter : StringConverter<File>(){

   override fun toString(obj: File?): String {
      return obj?.canonicalPath ?: ""
   }

   override fun fromString(str: String?): File {
      return File(str ?: "")
   }
}