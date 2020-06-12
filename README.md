![](src/main/resources/image/logo_small.png)
## Overview

Installmation is a GUI tool, similar to exe4j, which generates installers for Mac and Windows using Java 11+ JDK jpackage tool.

I am writing it in Kotlin.

The tool will provide the following:
    
    1. Ability to create installable image 
    2. Ability to create complete installer
    3. Ability to generate batch scripts for the relevant JPackage commands
    4. Ability to list the Java 9 module dependencies for an application (Jdeps Tool Dialog)
    
No doubt plugins for doing this in Gradle or Maven will appear, and this is meant 
to complement those approaches.

### Release 0.1.57 ###

Full installers for Mac and Windows can be found at https://github.com/SergeMerzliakov/installmation/releases

These installers are created by Installmation itself.

#### What's New

* OSX installer signing with Developer ID Installer certificates


### Running from the Command Line
You can run the latest version (rudimentary so far) with the gradle command (under Java 11 or later)
 
    gradlew runApp

These will build installers, but testing has been limited, so feedback would be welcome, as I am sure there 
are use cases which I have not covered.

### Prerequisites

    1. JDK 11+
    2. OpenJFX 11+
    3. gradle 5+
    4. WiX Toolkit (windows only)


### Setup

1. Download JavaFX SDK 13.0.1 or later (https://gluonhq.com/products/javafx/)
2. Download JavaFX jmods 13.0.1 or later (https://gluonhq.com/products/javafx/)
3. Install them together in the same root dir e.g. /somewhere/JavaFX/13
4. (Windows) Download WiX toolset from https://github.com/wixtoolset/wix3/releases and add to PATH
5. Create local.properties with JFX version and path to root dir. Here is an OSX sample:

        JFX_VERSION=13
        JFX=/somewhere/JavaFX/13
        JDK=/Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk  # this is for unit testing and must be v11+

Just to reiterate, JFX variable path should contain both jars and jmod files:

    <JFX>/libs
    <JFX>/jmods

#### Unit Testing
The unit tests run lots of Testfx tests, some of which tests dialogs which run JDK tools like jdeps. These tests require various binaries
like JDK, JavaFX libraries as input parameters, which cannot versioned inside the repo. 

So on first startup for these unit tests, the object org.installmation.TestingBootstrap checks for local configuration 
in **local.properties**, to make sure JavaFX and JDK are setup.
 
    <repo>/local.properties


The tests run in `headless` mode by default. To run tests and show their execution with the TestFX robot use the `showTests` environment variable

    gradlew test -DshowTests=true

