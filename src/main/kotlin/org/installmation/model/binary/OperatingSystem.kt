package org.installmation.model.binary

object OperatingSystem {

	enum class Type {
      Windows,
      OSX,
      Linux
	}

	fun os(): Type {
		val name = System.getProperty("os.name").trim().toLowerCase()
		val version = System.getProperty("os.version").trim().toLowerCase()

		when {
			name.startsWith("mac")     -> return Type.OSX
			name.startsWith("windows") -> return Type.Windows
			name.startsWith("linux")   -> return Type.Linux
		}

		throw RuntimeException("Unknown operating system - $name $version")
	}
}