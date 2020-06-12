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

package org.installmation.application

import com.google.common.eventbus.EventBus
import javafx.stage.Stage
import org.installmation.FXTest
import org.installmation.InstallmationApplication
import org.installmation.configuration.Configuration
import org.installmation.controller.BinariesController
import org.installmation.controller.DependenciesController
import org.installmation.controller.LocationController
import org.installmation.controller.OSXController
import org.installmation.core.OperatingSystem
import org.installmation.core.RunningAsTestEvent
import org.installmation.javafx.test.FXID
import org.installmation.javafx.test.WindowHelper
import org.junit.Test
import org.testfx.api.FxAssert
import org.testfx.matcher.base.NodeMatchers

/** org.installmation.application.ApplicationDialogTest
 * Ensure all dialogs are at least shown in button and menu clicks. No check on dialog contents.
 * Note: Running tests in Windows with > 1 Intellij projects will fail on first test in this class.
 * Shut down other projects, minimize IDEA window and then it works.
 */
class ApplicationDialogTest : FXTest() {

    private val application = InstallmationApplication()
    override fun start(stage: Stage?) {
        super.start(stage)
        val bus = EventBus()
        val configuration = Configuration(bus)
        application.startApplication(stage!!, configuration, bus)
        bus.post(RunningAsTestEvent())
    }

    @Test
    fun shouldOpenAboutDialog() {
        clickOn(FXID.MENU_ABOUT).clickOn(FXID.MENUITEM_ABOUT)
        FxAssert.verifyThat(FXID.DIALOG_ABOUT_ID, NodeMatchers.isVisible())
        clickOn(FXID.BUTTON_ABOUT_DLG_OK)
    }

    @Test
    fun shouldOpenNewProjectDialog() {
        clickOn(FXID.MENU_PROJECT).clickOn(FXID.MENUITEM_NEW_PROJECT)
        FxAssert.verifyThat(FXID.DIALOG_SINGLEVAL_ID, NodeMatchers.isVisible())
        clickOn(FXID.BUTTON_SINGLEVAL_DLG_CANCEL)
    }

    @Test
    fun shouldOpenJDKDialogFromMenu() {
        clickOn(FXID.MENU_LIBRARY).clickOn(FXID.MENUITEM_JPACKAGE)
        FxAssert.verifyThat(FXID.DIALOG_BINARTEFACT_ID, NodeMatchers.isVisible())
        clickOn(FXID.BUTTON_BINARTEFACT_CANCEL)
    }

    @Test
    fun shouldOpenJavaFXJarDialogFromMenu() {
        clickOn(FXID.MENU_LIBRARY).clickOn(FXID.MENUITEM_JFX_JAR)
        FxAssert.verifyThat(FXID.DIALOG_BINARTEFACT_ID, NodeMatchers.isVisible())
        clickOn(FXID.BUTTON_BINARTEFACT_CANCEL)
    }

    @Test
    fun shouldOpenJavaFXJmodDialogFromMenu() {
        clickOn(FXID.MENU_LIBRARY).clickOn(FXID.MENUITEM_JFX_JMOD)
        FxAssert.verifyThat(FXID.DIALOG_BINARTEFACT_ID, NodeMatchers.isVisible())
        clickOn(FXID.BUTTON_BINARTEFACT_CANCEL)
    }

    @Test
    fun shouldOpenJDKDialogFromButton() {
        clickOn(FXID.TOOLBAR_BUTTON_JPACKAGE)
        FxAssert.verifyThat(FXID.DIALOG_BINARTEFACT_ID, NodeMatchers.isVisible())
        clickOn(FXID.BUTTON_BINARTEFACT_CANCEL)
    }

    @Test
    fun shouldOpenJavaFXJmodDialogFromButton() {
        clickOn(FXID.TOOLBAR_BUTTON_JAVAFX_JMOD)
        FxAssert.verifyThat(FXID.DIALOG_BINARTEFACT_ID, NodeMatchers.isVisible())
        clickOn(FXID.BUTTON_BINARTEFACT_CANCEL)
    }

    @Test
    fun shouldOpenJavaFXJarDialogFromButton() {
        clickOn(FXID.TOOLBAR_BUTTON_JAVAFX_JAR)
        FxAssert.verifyThat(FXID.DIALOG_BINARTEFACT_ID, NodeMatchers.isVisible())
        clickOn(FXID.BUTTON_BINARTEFACT_CANCEL)
    }

    @Test
    fun shouldOpenJdepsDialog() {
        clickOn(FXID.MENU_TOOL)
        clickOn(FXID.MENUITEM_JDEPS)
        FxAssert.verifyThat(FXID.DIALOG_JDEPS, NodeMatchers.isVisible())
        clickOn(FXID.BUTTON_JDEPS_DLG_CLOSE)
    }

    @Test
    fun shouldOpenOSXInfoHelpDialogs() {
        if (OperatingSystem.os() == OperatingSystem.Type.OSX) {
            clickOn(FXID.TAB_OSX)
            clickOn(FXID.BUTTON_OSX_HELP_SIGN_KEYCHAIN)
            var helpDialog = WindowHelper.verifyWindowVisible(OSXController.TITLE_HELP_SIGN_KEYCHAIN)
            WindowHelper.closeWindow(helpDialog)

            clickOn(FXID.BUTTON_OSX_HELP_SIGN_INSTALL_CERT)
            helpDialog = WindowHelper.verifyWindowVisible(OSXController.TITLE_HELP_SIGN_INSTALL_CERT)
            WindowHelper.closeWindow(helpDialog)
        }
    }

    @Test
    fun shouldOpenDependenciesHelpDialogs() {
        clickOn(FXID.TAB_DEPENDENCIES)
        clickOn(FXID.BUTTON_HELP_EXTRA_MODULES)
        val helpDialog = WindowHelper.verifyWindowVisible(DependenciesController.TITLE_HELP_EXTRA_MODULE)
        WindowHelper.closeWindow(helpDialog)
    }

    @Test
    fun shouldOpenLocationHelpDialogs() {
        clickOn(FXID.TAB_INSTALL)
        clickOn(FXID.BUTTON_HELP_INPUT_DIR)
        var helpDialog = WindowHelper.verifyWindowVisible(LocationController.TITLE_HELP_INPUT_DIR)
        WindowHelper.closeWindow(helpDialog)

        clickOn(FXID.BUTTON_HELP_IMAGE_BUILD_DIR)
        helpDialog = WindowHelper.verifyWindowVisible(LocationController.TITLE_HELP_IMAGE_BUILD_DIR)
        WindowHelper.closeWindow(helpDialog)

        clickOn(FXID.BUTTON_HELP_INSTALLER_DIR)
        helpDialog = WindowHelper.verifyWindowVisible(LocationController.TITLE_HELP_INSTALLER_DIR)
        WindowHelper.closeWindow(helpDialog)
    }

    @Test
    fun shouldOpenBinariesHelpDialogs() {
        clickOn(FXID.TAB_BINARIES)
        clickOn(FXID.BUTTON_HELP_FX_LIBRARIES)
        var helpDialog = WindowHelper.verifyWindowVisible(BinariesController.TITLE_HELP_FX_LIBRARIES)
        WindowHelper.closeWindow(helpDialog)

        clickOn(FXID.BUTTON_HELP_FX_MODULES)
        helpDialog = WindowHelper.verifyWindowVisible(BinariesController.TITLE_HELP_FX_MODULES)
        WindowHelper.closeWindow(helpDialog)

        clickOn(FXID.BUTTON_HELP_JPACKAGE)
        helpDialog = WindowHelper.verifyWindowVisible(BinariesController.TITLE_HELP_JPACKAGE)
        WindowHelper.closeWindow(helpDialog)

        clickOn(FXID.BUTTON_HELP_JDK)
        helpDialog = WindowHelper.verifyWindowVisible(BinariesController.TITLE_HELP_JDK)
        WindowHelper.closeWindow(helpDialog)
    }
}