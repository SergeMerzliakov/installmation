#### Overview

I got really tired of building cross platform installers for Java and Kotlin desktop applications with jpackage, Wix, Inno setup
and a few other combinations, and experimenting with different command line options.

So I am writing a GUI tool, similar to exe4j, which generates installers for Mac and Windows using Java 11+ JDK jpackage tool.

I am writing it in Kotlin, as there is no reason to use Java anymore.


### Current State of Master Branch ###

Rudimentary GUI mockup which runs with the gradle command (under Java 11 or later)
 
    gradlew runApp

Application starts and does very little.


### Prequisites

    1. JDK 11+
    2. OpenJFX 11+
    3. gradle 5+

