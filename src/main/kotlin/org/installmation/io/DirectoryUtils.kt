package org.installmation.io

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File


private val log: Logger = LogManager.getLogger("DirectoryUtils")

fun ensureDirectory(dir: File) {
	if (!dir.exists() && dir.isDirectory) {
		log.debug("projects directory not found - creating directory [${dir.absolutePath}]")
		try {
			dir.mkdirs()
		}
		catch (e: Exception) {
			log.error("Could not create directory [${dir.absolutePath}]", e)
		}
	}

}