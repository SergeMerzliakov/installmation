package org.installmation.ui.dialog

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.installmation.FXTest
import org.installmation.configuration.HISTORY_PROJECT
import org.installmation.configuration.UserHistory
import java.io.File

class ChooseFileDialogTest : FXTest() {
   companion object {
      const val DIALOG_BUTTON = "button1"
   }

   private lateinit var buttonSingle: Button
   private lateinit var result: DialogResult<File>
   private var userHistory = UserHistory()

   override fun start(stage: Stage?) {
      super.start(stage)
      userHistory.set(HISTORY_PROJECT,File("src/test/resources/projects"))
      buttonSingle = Button("Show Dialog")
      buttonSingle.id = DIALOG_BUTTON
      buttonSingle.setOnAction {
         result = openFileDialog(stage!!, "Choose Project", userHistory.getFile(HISTORY_PROJECT), InstallmationExtensionFilters.projectFilter())
      }

      stage?.scene = Scene(VBox(buttonSingle), 100.0, 100.0)
      stage?.title = "Dialog Runner"
      stage?.show()
   }

   // this does nothing TODO figure out how to test standard Dialogs
//   @Test
//   fun shouldShowDialog() {
//      clickOn("#$DIALOG_BUTTON")
//   }

}