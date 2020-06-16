/*
 * Copyright 2020 Serge Merzliakov
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
package org.installmation.javafx.test

import javafx.application.Platform
import java.util.concurrent.Semaphore

/**
 * Executes a block of JavaFX in the JavaFX application thread
 * and propagates the exception back to calling thread.
 * Blocks until code has completed
 */
class FXBlock(private val runnable:Runnable) {

	private var success = false
	private var error:Throwable? = null

	fun run() {
		success = false
		val semaphore = Semaphore(0)

		//run the JavaFX code in the FX application thread
		Platform.runLater {
			try {
				runnable.run()
				success = true
			}
			catch (e: Throwable) {
				error = e
			}
			finally {
				semaphore.release()
			}
		}
		semaphore.acquire() //wait until FX code completed
		if (!success)
			throw(error!!)
	}
}