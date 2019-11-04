package org.installmation.model.binary

import java.io.File

/**
 * Operating system aware factory
 */
object JDKFactory {

   fun create(os: OperatingSystem.Type, name: String, path: File): JDK {
      return when (os) {
         OperatingSystem.Type.OSX -> MacJDK(name, path)
         OperatingSystem.Type.Windows -> WindowsJDK(name, path)
         OperatingSystem.Type.Linux -> LinuxJDK(name, path)
      }
   }
}