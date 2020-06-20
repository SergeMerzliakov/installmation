package org.installmation.model.binary

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.model.FlagArgument
import org.installmation.model.ValueArgument
import java.io.File
import java.io.FileFilter


private val log: Logger = LogManager.getLogger(ModuleDependenciesGenerator::class.java)

class ModuleDependenciesGenerator(val jdeps: JDepsExecutable, val classPath: String, val javaFxLibs: File, val mainJar: String) {

   var output = mutableListOf<String>()

   // commands generated for users benefit
   val commands = mutableListOf<String>()

   /**
    * returns a sorted list of Java 9 modules detected
    * This is complicated, as we need to run the command on the application, classpath
    * and JFX modules, but we also run the jdeps command over every dependency, as some
    * transitive dependencies are missed (f#$@ !!)
    *
    * Only return list as it's sorted.
    */
   fun generate(): List<String> {
      val merged = mutableSetOf<String>()
      val applicationDependencies = generateAppDependencies()
      merged.addAll(applicationDependencies)

      val jarDependencies = generateJarDependencies()
      merged.addAll(jarDependencies)

      return merged.toList().sorted()
   }

   /*
    * Run the command on the application, classpath
    * and JFX modules, which gets NEARLY all dependencies
    */
   private fun generateAppDependencies(): Set<String> {
      jdeps.parameters.clear()
      jdeps.parameters.addArgument(FlagArgument("-s"))
      jdeps.parameters.addArgument(ValueArgument("--multi-release", "11"))
      jdeps.parameters.addArgument(ValueArgument("--module-path", javaFxLibs.path))
      jdeps.parameters.addArgument(ValueArgument("-classpath", classPath))
      jdeps.parameters.addArgument(FlagArgument(mainJar))
      commands.add(jdeps.toString())

      val processOutput = jdeps.execute(15)

      output.addAll(processOutput.output)
      output.addAll(processOutput.errors)
      if (processOutput.errors.isEmpty()) {
         val jpd = JDepsParser(processOutput.output)
         return jpd.dependencies
      }
      return setOf()
   }

   private fun generateJarDependencies(): Set<String> {
      val merged = mutableSetOf<String>()
      if (!File(classPath).exists()){
         log.warn("Cannot generate module dependenciess for classpath [$classPath] as directory does not exist.")
         return setOf()
      }
      val jars = File(classPath).listFiles(FileFilter { it.extension == "jar" })

      if (jars.isNullOrEmpty())
         return setOf()

      for (jar in jars){
         val deps = generateSingleJarDependencies(jar)
         merged.addAll(deps)
      }
      return merged
   }

   /*
    * Run the command on the application, classpath
    * and JFX modules, which gets NEARLY all dependencies
    */
   private fun generateSingleJarDependencies(jar:File): Set<String> {
      jdeps.parameters.clear()
      jdeps.parameters.addArgument(FlagArgument("-s"))
      jdeps.parameters.addArgument(FlagArgument(jar.absolutePath))
      commands.add(jdeps.toString())

      val processOutput = jdeps.execute(10)

      output.addAll(processOutput.output)
      output.addAll(processOutput.errors)
      if (processOutput.errors.isEmpty()) {
         val jpd = JDepsParser(processOutput.output)
         return jpd.dependencies
      }
      return setOf()
   }

}