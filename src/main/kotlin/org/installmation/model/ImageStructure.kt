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

import java.io.File

/**
 * Directory structure of application files (excluding Java runtime files)
 * which will be used to create a full install image.
 * 
 * Most often contains main jar file, configuration files (depending on OS), 
 * and a single lib directory with application dependencies  
 */
interface ImageStructure {
   fun addFile(name:String)
   fun addDirectory(name:String)
   fun getFiles():Set<String>
   fun getDirectories():Set<File>
}