package org.installmation.model

import org.assertj.core.api.Assertions.assertThat
import org.installmation.io.ApplicationJsonReader
import org.installmation.io.ApplicationJsonWriter
import org.junit.AfterClass
import org.junit.Test
import java.io.File

class WorkspaceTest {

   companion object {
      val SAVED_FILE = File("testdata", "workspace.json")
      const val PROJECT_NAME = "testproject"
      // for now... projects try hard to save themselves in user.home directory - fix in future to make projects more test-friendly
      val SAVED_PROJECT_FILE = File(System.getProperty("user.home"), ".installmation/projects/$PROJECT_NAME.json")

      @AfterClass
      @JvmStatic
      fun cleanup() {
         SAVED_FILE.parentFile.deleteRecursively()
         SAVED_PROJECT_FILE.delete()
      }
   }

   @Test
   fun shouldSerializeEmptyWorkspace() {
      val ws = Workspace()
      val writer = ApplicationJsonWriter<Workspace>(SAVED_FILE, JsonParserFactory.configurationParser())
      writer.save(ws)

      val reader = ApplicationJsonReader<Workspace>(Workspace::class, SAVED_FILE, JsonParserFactory.configurationParser())
      val ws2 = reader.load()

      assertThat(ws2).isEqualToComparingFieldByField(ws)
   }

   @Test
   fun shouldSerializeWorkspace() {
      val ws = Workspace()
      val proj = InstallProject()
      proj.name = PROJECT_NAME
      val projWriter = ApplicationJsonWriter<InstallProject>(SAVED_PROJECT_FILE, JsonParserFactory.configurationParser())
      projWriter.save(proj)
      
      ws.setCurrentProject(proj)
      val writer = ApplicationJsonWriter<Workspace>(SAVED_FILE, JsonParserFactory.configurationParser())
      writer.save(ws)

      val reader = ApplicationJsonReader<Workspace>(Workspace::class, SAVED_FILE, JsonParserFactory.configurationParser())
      val ws2 = reader.load()

      assertThat(ws2).isEqualToComparingFieldByField(ws)
   }

}