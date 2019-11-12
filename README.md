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

Before You run update this variable in the build.gradle file to location of your OpenJFX install

    def JFX_INSTALL = '/Library/Java/javafx/13.0'


This path should contain an OpenJFX install:

    /libs
    /jmods


You can run the latest version (rudimentary so far) with the gradle command (under Java 11 or later)
 
    gradlew runApp

Application starts and currently does very little.


### Prequisites

    1. JDK 11+
    2. OpenJFX 11+
    3. gradle 5+

