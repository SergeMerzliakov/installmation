/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 **/

package org.installmation.configuration

import java.io.File

object Constant {
   const val CONFIG_DIR = "configuration"
   const val CONFIG_FILE = "config.json"
   const val WORKSPACE_DIR = "workspace"
   const val WORKSPACE_FILE = "workspace.json"
   const val APP_DIR = ".installmation"
   const val PROJECT_DIR = "projects"
   val USER_HOME_DIR:String = System.getProperty("user.home")
   val DEFAULT_BASE_DIR = File(USER_HOME_DIR, APP_DIR)
}