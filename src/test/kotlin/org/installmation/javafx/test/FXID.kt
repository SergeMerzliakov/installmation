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
package org.installmation.javafx.test

/**
 * Single list of all FX identifiers (fx:id in fxml files). Use these in all tests
 */
object FXID {

   // top menu
   const val MENU_PROJECT = "#projectMenu"
   const val MENU_SHUTDOWN = "#shutdownMenu"
   const val MENU_LIBRARY = "#libraryMenu"
   const val MENU_GENERATE = "#generateMenu"
   const val MENU_TOOL = "#toolMenu"
   const val MENU_ABOUT = "#aboutMenu"

   // toolbar buttons
   const val TOOLBAR_BUTTON_JPACKAGE = "#jpackageButton"
   const val TOOLBAR_BUTTON_JAVAFX_JMOD = "#javafxJmodButton"
   const val TOOLBAR_BUTTON_JAVAFX_JAR = "#javafxJarButton"
   const val TOOLBAR_BUTTON_GENERATE_SCRIPTS = "#generateScriptsButton"
   const val TOOLBAR_BUTTON_GENERATE_IMAGE = "#generateImageButton"
   const val TOOLBAR_BUTTON_GENERATE_INSTALLER = "#generateInstallerButton"

   // PROJECT MENU
   const val MENUITEM_SAVE_PROJECT = "#saveProjectMenuItem"
   const val MENUITEM_NEW_PROJECT = "#newProjectMenuItem"
   const val MENUITEM_OPEN_PROJECT = "#openProjectMenuItem"
   const val MENUITEM_CLOSE_PROJECT = "#closeProjectMenuItem"

   // ABOUT MENU
   const val MENUITEM_ABOUT = "#aboutMenuItem"

   // LIBRARY MENU
   const val MENUITEM_JPACKAGE = "#jpackageMenuItem"
   const val MENUITEM_JFX_JAR = "#javafxJarMenuItem"
   const val MENUITEM_JFX_JMOD = "#javafxModMenuItem"

   // TOOL menu
   const val MENUITEM_JDEPS = "#jdepsMenuItem"

   // ALL TABS
   const val TAB_INFO = "#infoTab"
   const val TAB_OSX = "#osxTab"
   const val TAB_INSTALL = "#installTab"
   const val TAB_BINARIES = "#binariesTab"
   const val TAB_EXECUTABLE = "#executableTab"
   const val TAB_DEPENDENCIES = "#dependenciesTab"

   // GENERAL INFO TAB
   const val TEXT_PROJECT_NAME = "#projectNameField"
   const val TEXT_APP_VERSION = "#applicationVersionField"
   const val TEXT_COPYRIGHT = "#copyrightField"
   const val TEXT_VENDOR = "#vendorField"
   const val COMBO_INSTALLER_TYPE = "#installerTypeCombo"
   const val BUTTON_CHOOSE_LOGO = "#chooseLogoButton"
   const val TEXT_LOGO_PATH = "#logoPathField"

   // OSX TAB
   const val TEXT_OSX_PACKAGE_IDENTIFIER = "#packageIdentifierField"
   const val TEXT_OSX_PACKAGE_NAME = "#packageNameField"
   const val TEXT_OSX_SIGN_PREFIX = "#signPrefixField"
   const val TEXT_OSX_SIGN_USER = "#signKeyUserField"
   const val TEXT_OSX_SIGN_KEYCHAIN = "#signKeyChainField"
   const val CHECKBOX_OSX_SIGN = "#signCheckBox"
   const val BUTTON_OSX_KEY_CHAIN = "#chooseKeyChainButton"
   const val BUTTON_OSX_HELP_SIGN_KEYCHAIN = "#helpSignKeychainButton"
   const val BUTTON_OSX_HELP_PACKAGE_ = "#helpPackageIdentifierButton"
   const val BUTTON_OSX_HELP_IDENTIFIER = "#helpPackageNameButton"
   const val BUTTON_OSX_HELP_SIGN_PREFIX = "#helpSignPrefixButton"
   const val BUTTON_OSX_HELP_SIGN_USER = "#helpSignUserButton"

   // INSTALL TAB
   const val TEXT_INPUT_DIR = "#inputDirectoryText"
   const val TEXT_IMAGE_BUILD_DIR = "#imageBuildDirectoryText"
   const val TEXT_INSTALLER_DIR = "#installerDirectoryText"
   const val BUTTON_INPUT_DIR = "#inputDirectoryButton"
   const val BUTTON_IMAGE_BUILD_DIR = "#imageBuildDirectoryButton"
   const val BUTTON_INSTALLER_DIR = "#installerDirectoryButton"

   // JAVA BINARIES TAB
   const val COMBO_JPACKAGE = "#jpackageComboBox"
   const val BUTTON_JPACKAGE = "#configureJPackageButton"
   const val COMBO_MODULE_LIB = "#moduleLibComboBox"
   const val BUTTON_MODULE_LIB = "#configureModuleLibrariesButton"
   const val COMBO_INSTALL_JDK = "#installJDKComboBox"
   const val BUTTON_INSTALL_JDK = "#configureInstallJDKButton"
   const val COMBO_MODULE_JMOD = "#moduleJmodComboBox"
   const val BUTTON_MODULE_JMOD = "#configureModuleJmodButton"
   const val BUTTON_FX_LIBRARIES = "#helpFXLibrariesButton"
   const val BUTTON_FX_MODULES = "#helpFXModulesButton"
   const val BUTTON_HELP_JPACKAGE = "#helpJpackageButton"
   const val BUTTON_HELP_JDK = "#helpJDKButton"

   // APPLICATION EXECUTABLE
   const val TEXT_MAIN_JAR = "#mainJarField"
   const val TEXT_MAIN_CLASS = "#mainClassField"
   const val BUTTON_MAIN_JAR = "#mainJarButton"

   // DEPENDENCIES TAB
   const val LISTVIEW_CLASSPATH = "#classPathListView"
   const val BUTTON_ADD_CLASSPATH = "#addClasspathButton"
   const val TEXT_MODULE_LIST = "#moduleListText"
   const val BUTTON_HELP_EXTRA_MODULES = "#helpModulesButton"

   // ABOUT DIALOG
   const val DIALOG_ABOUT_ID = "#aboutDialog"
   const val BUTTON_ABOUT_DLG_OK = "#okButton"

   // ITEM LIST DIALOG
   const val DIALOG_ITEMLIST = "#itemListDialog"
   const val BUTTON_ITEMLIST_DLG_OK = "#okButton"
   const val LISTVIEW_ITEMLIST_DLG = "#itemListView"
   const val LABEL_ITEMLIST_DLG = "#listLabel"

   // SINGLE VALUE DIALOG
   const val DIALOG_SINGLEVAL_ID = "#singleValueDialog"
   const val LABEL_SINGLEVAL_DLG_ITEM = "#itemLabel"
   const val TEXT_SINGLEVAL_DLG_ITEM_VALUE = "#itemValue"
   const val BUTTON_SINGLEVAL_DLG_SAVE = "#saveButton"
   const val BUTTON_SINGLEVAL_DLG_CANCEL = "#cancelButton"

   // BINARY ARTEFACT DIALOG
   const val DIALOG_BINARTEFACT_ID = "#binaryArtefactDialog"
   const val TABLEVIEW_BINARTEFACT_ARTEFACT = "#artefactTableView"
   const val BUTTON_BINARTEFACT_SAVE = "#saveButton"
   const val BUTTON_BINARTEFACT_CANCEL = "#cancelButton"
   const val BUTTON_BINARTEFACT_ADD = "#addButton"

   // JDEPS DIALOG
   const val DIALOG_JDEPS = "#jdepsDialog"
   const val BUTTON_JDEPS_DLG_RUN = "#runButton"
   const val BUTTON_JDEPS_DLG_CLOSE = "#closeButton"
   const val TEXT_JDEPS_DLG_MAINJAR = "#mainJarText"
   const val COMBO_JDEPS_DLG_JDK = "#jdkComboBox"
   const val LISTVIEW_JDEPS_DLG_CLASSPATH = "#classPathListView"
   const val LISTVIEW_JDEPS_DLG_MODULEPATH = "#modulePathListView"
   const val TEXT_JDEPS_DLG_GENERATED_CMD = "#generatedCommandText"

   const val LISTVIEW_JDEPS_DLG_PROCESS_OUTPUT = "#processOutputView"
   const val LISTVIEW_JDEPS_DLG_DEP_LIST = "#dependencyListView"
   const val TEXT_JDEPS_DLG_DEPENDENCY = "#dependencyTextArea"
   const val TAB_JDEPS_DLG_OUTPUT = "#outputTab"
   const val TAB_JDEPS_DLG_LIST = "#listTab"
   const val TAB_JDEPS_DLG_TEXT = "#textTab"
   const val BUTTON_JDEPS_MAIN_JAR = "#configureMainJarButton"
}