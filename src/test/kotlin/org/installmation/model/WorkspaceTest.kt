package org.installmation.model

import com.google.common.eventbus.EventBus
import org.assertj.core.api.Assertions.assertThat
import org.installmation.TestConstants
import org.installmation.configuration.Configuration
import org.installmation.configuration.Constant
import org.installmation.configuration.JsonParserFactory
import org.installmation.io.ApplicationJsonReader
import org.installmation.io.ApplicationJsonWriter
import org.installmation.service.Workspace
import org.junit.AfterClass
import org.junit.Test
import java.io.File

class WorkspaceTest {

   companion object {
      val SAVED_FILE = File(TestConstants.TEST_TEMP_DIR, "workspace.json")
      const val PROJECT_NAME = "WorkspaceTestProject"
      val BASE_CONFIG_DIR = File(TestConstants.TEST_TEMP_DIR)
      val configuration = Configuration(EventBus(), BASE_CONFIG_DIR)
      // for now... projects try hard to save themselves in user.home directory - fix in future to make projects more test-friendly
      val SAVED_PROJECT_FILE = File(File(configuration.baseDirectory, Constant.PROJECT_DIR), PROJECT_NAME + ".json")

      @AfterClass
      @JvmStatic
      fun cleanup() {
         SAVED_FILE.parentFile.deleteRecursively()
         SAVED_PROJECT_FILE.delete()
         BASE_CONFIG_DIR.deleteRecursively()
      }
   }

   @Test
   fun shouldSerializeEmptyWorkspace() {
      val ws = Workspace(configuration)
      val writer = ApplicationJsonWriter<Workspace>(SAVED_FILE, JsonParserFactory.workspaceParser(configuration))
      writer.save(ws)

      val reader = ApplicationJsonReader<Workspace>(Workspace::class, SAVED_FILE, JsonParserFactory.workspaceParser(configuration))
      val ws2 = reader.load()

      assertThat(ws2).isEqualToComparingFieldByField(ws)
   }

   @Test
   fun shouldSerializeWorkspace() {
      val ws = Workspace(configuration)
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