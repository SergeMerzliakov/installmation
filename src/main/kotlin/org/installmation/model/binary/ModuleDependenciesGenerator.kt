package org.installmation.model.binary

import org.installmation.model.FlagArgument
import org.installmation.model.ValueArgument

class ModuleDependenciesGenerator(val jdeps: JDepsExecutable, val classPath: String, val modulePath: String, val mainJar: String) {

   var output = mutableListOf<String>()

   /**
    * returns a sorted list of Java 9 modules detected
    */
   fun generate(): List<String> {
      jdeps.parameters.addArgument(FlagArgument("-s"))
      jdeps.parameters.addArgument(ValueArgument("--module-path", modulePath))
      jdeps.parameters.addArgument(ValueArgument("-classpath", classPath))
      jdeps.parameters.addArgument(FlagArgument(mainJar))
      val processOutput = jdeps.execute(15)
      output.addAll(processOutput)
      val jpd = JDepsParser(output)
      return jpd.dependencies.toList().sorted()
   }
}