package org.installmation.javafx

import javafx.scene.control.ComboBox
import org.installmation.core.UserNamed

object ComboUtils {

   /**
    * Change selection of combo box
    */
   fun <T : UserNamed> comboSelect(combo: ComboBox<T>, id: String?) {
      if (id == null)
         return
      val item = combo.items.find { it.name == id }
      combo.selectionModel.select(item)
   }

}