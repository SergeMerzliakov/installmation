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

package org.installmation.model

import org.installmation.model.binary.JPackageExecutable
import org.installmation.model.binary.MacJDK
import org.junit.jupiter.api.Test
import java.io.File

class InstallProjectTest {

   /*
   rm -rf ../../image-build     ==> temporary dir
   rm -rf ../../image-input     ==> temporary dir. put artefacts here 
   cd ../..
   gradlew imageJar             ==> action create main jar file 
   gradlew lib                  ==> copy depedencies here
   cd installer/mac
   export JPACKAGE=/Users/loyaltyuser/tools/jpackage49/Contents/Home/bin/jpackage
   $JPACKAGE 
      --package-type app-image 
      -d ../../image-build 
      -i ../../image-input 
      -n demo1 
      --module-path /Library/Java/javafx/13.0/jmods 
      --add-modules java.base,javafx.controls,javafx.fxml,javafx.graphics 
      --main-jar javafx-kotlin-demo-1.0.0.jar 
      --main-class org.epistatic.kotlindemo.DemoApp
    */
   @Test
   fun shouldCreateImage() {
      val packageJdk = MacJDK(File("/Users/loyaltyuser/tools/jpackage49"))
      val jpackage = JPackageExecutable(packageJdk.packageExecutable)
      val proj = InstallProject()
      proj.name = "CobolBrowser"
      proj.version = "1.0"
      proj.jpackage = jpackage
      proj.imageStructure = SimpleImageStructure()

      // some directories have a role such as classpath
      proj.imageStructure?.addDirectory("lib")
      proj.imageStructure?.addFile("main.jar")

      proj.imageContentDirectory = File("imageBuild")
      proj.imageBuildDirectory = File("imageBuild")
   }
}