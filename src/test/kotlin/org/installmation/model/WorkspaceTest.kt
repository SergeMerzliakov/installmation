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
package org.installmation.model

import com.google.common.eventbus.EventBus
import org.assertj.core.api.Assertions.assertThat
import org.installmation.TestConstants
import org.installmation.configuration.Configuration
import org.installmation.configuration.Constant
import org.installmation.configuration.JsonParserFactory
import org.installmation.configuration.UserHistory
import org.installmation.io.ApplicationJsonReader
import org.installmation.io.ApplicationJsonWriter
import org.installmation.service.Workspace
import org.junit.AfterClass
import org.junit.Test
import java.io.File

private val SAVED_FILE = File(TestConstants.TEST_TEMP_DIR, "workspace.json")
private val PROJECT_NAME = "WorkspaceTestProject"
private val BASE_CONFIG_DIR = File(TestConstants.TEST_TEMP_DIR)
private val configuration = Configuration(EventBus(), BASE_CONFIG_DIR)

// for now... projects try hard to save themselves in user.home directory - fix in future to make projects more test-friendly
val SAVED_PROJECT_FILE = File(File(configuration.baseDirectory, Constant.PROJECT_DIR), "$PROJECT_NAME.json")

class WorkspaceTest {

   companion object {
      @AfterClass
      @JvmStatic
      fun cleanup() {
         SAVED_FILE.parentFile.deleteRecursively()
         SAVED_PROJECT_FILE.delete()
         BASE_CONFIG_DIR.deleteRecursively()
      }
   }

   @Test
   fun shouldSerializeNestedUserHistory() {
      val ws = Workspace(UserHistory(), configuration)
      ws.userHistory.set("name", "serge")
      ws.userHistory.set("install", File("/usr/bin"))

      val gson = JsonParserFactory.workspaceParser(configuration)

      val data = gson.toJson(ws)

      val ws2 = gson.fromJson(data, Workspace::class.java)
      assertThat(ws2.userHistory).isEqualToComparingFieldByField(ws.userHistory)
   }

   @Test
   fun shouldSerializeEmptyWorkspace() {
      val ws = Workspace(UserHistory(), configuration)
      val writer = ApplicationJsonWriter<Workspace>(SAVED_FILE, JsonParserFactory.workspaceParser(configuration))
      writer.save(ws)

      val reader = ApplicationJsonReader<Workspace>(Workspace::class, SAVED_FILE, JsonParserFactory.workspaceParser(configuration))
      val ws2 = reader.load()

      assertThat(ws2).isEqualToComparingFieldByField(ws)
   }

   @Test
   fun shouldSerializeWorkspace() {
      val ws = Workspace(UserHistory(), configuration)
      val proj = InstallProject()
      proj.name = PROJECT_NAME
      val projWriter = ApplicationJsonWriter<InstallProject>(SAVED_PROJECT_FILE, JsonParserFactory.configurationParser())
      projWriter.save(proj)

      ws.setCurrentProject(proj)
      val writer = ApplicationJsonWriter<Workspace>(SAVED_FILE, JsonParserFactory.workspaceParser(configuration))
      writer.save(ws)

      val reader = ApplicationJsonReader<Workspace>(Workspace::class, SAVED_FILE, JsonParserFactory.workspaceParser(configuration))
      val ws2 = reader.load()

      assertThat(ws2).isEqualToComparingFieldByField(ws)
   }

}