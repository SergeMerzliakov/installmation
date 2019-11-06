package org.installmation.ui.dialog

import javafx.util.StringConverter
import java.io.File

class FileStringConverter : StringConverter<File>(){

   override fun toString(obj: File?): String {
      // use path value as is not a relative path
      return obj?.path ?: ""
   }

   override fun fromString(str: String?): File {
      return File(str ?: "")
   }
}