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

import org.installmation.core.OperatingSystem
import org.installmation.model.binary.JDK
import org.installmation.model.binary.JDKFactory
import java.io.File

/**
 * Create Dummy JDKs for unit tests
 */
object TestJDKFactory {

   fun create(name: String): JDK {
      return JDKFactory.create(OperatingSystem.os(), name, File("/$name"))
   }

   fun create(name: String, path: String): JDK {
      return JDKFactory.create(OperatingSystem.os(), name, File(path))
   }

}