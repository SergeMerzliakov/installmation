#### Overview

I got really tired of building cross platform installers for Java and Kotlin desktop applications with jpackage, Wix, Inno setup
and a few other combinations, and experimenting with different command line options.

So I am writing a GUI tool, similar to exe4j, which generates installers for Mac and Windows using Java 11+ JDK jpackage tool.

I am writing it in Kotlin, as there is no reason to use Java anymore.

The tool will provide the following:
    
    1. Ability to create complete installer
    2. Ability to generate batch scripts for the relevant JPackage commands
    
No doubt plugins for doing this in Gradle or Maven will appear, and this is meant 
to complement those approaches.

### Current State of Master Branch ###

#### Setup ####
 
Update gradle.properties file with JavaFX details. JavaFX is a separate download (https://gluonhq.com/products/javafx/)

1. Download JavaFX SDK
2. Download JavaFX jmods
3. Install them together in the same root dir e.g. /somewhere/JavaFX/13.0
4. Update gradle.properties with JFX version and path to root dir. Here is a sample

        JFX_VERSION=13
        JFX=/Library/Java/javafx/13.0


Just to reiterate, JFX should contain both jars and jmod files:

    <JFX>/libs
    <JFX>/jmods


You can run the latest version (rudimentary so far) with the gradle command (under Java 11 or later)
 
    gradlew runApp

Builds 20+ can build installers for OSX applications, but with specific limitations. 
Not yet ready for general release.

### Prequisites

    1. JDK 11+
    2. OpenJFX 11+
    3. gradle 5+


### Setup

#### Unit Testing
The unit tests run lots of Testfx tests, some of which tests dialogs which run JDK tools like jdeps. These tests require various binaries
like JDK, JavaFX libraries as input parameters, which cannot versioned inside the repo. 

So on first startup, the object org.installmation.TestingBootstrap checks whether /testconfig files exist, and if not, create empty ones for the 
developer/user to fill in. These are generated here: 
 
    <repo>/gradle.properties
    
Only the OS-specific versions need to be filled in with:

**test.jdk** - the JDK used for installer creation or dependency analysis (Can be the current JDK on the system, as long as its JDK 11+. 
There is no need to install a second JDK just to run some tests!!)

**test.javafx** - For JavaFX systems. Currently mandatory as of build 20 - but will be optional VERY soon (mea culpa)

An Example (author's OSX system):

    test.jdk=/Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk
    test.javafx=/Library/Java/javafx/13.0
