package org.installmation.model.binary

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class JDKFactoryTest {

   @Test
   fun shouldCreateOSXJDK() {
      val jdk = JDKFactory.create(OperatingSystem.Type.OSX, "mac1", File("/usr/mac/jdk13"))
      assertThat(jdk).isInstanceOf(MacJDK::class.java)
   }

   @Test
   fun shouldCreateWindowsJDK() {
      val jdk = JDKFactory.create(OperatingSystem.Type.Windows, "windows1", File("C:\\java\\jdk13"))
      assertThat(jdk).isInstanceOf(WindowsJDK::class.java)
   }

   @Test
   fun shouldCreateLinuxJDK() {
      val jdk = JDKFactory.create(OperatingSystem.Type.Linux, "linux", File("/usr/ubuntu/jdk13"))
      assertThat(jdk).isInstanceOf(LinuxJDK::class.java)
   }
}