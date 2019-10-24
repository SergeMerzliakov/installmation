package org.installmation

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.installmation.model.binary.JDKSerializer

object SerializationUtils {

   /**
    * helper function to create OS specific serializer
    */
   fun createAdapterGson(jdkAdapter: Class<*>): Gson {
      val builder = GsonBuilder()
      builder.registerTypeAdapter(jdkAdapter, JDKSerializer())
      return builder.setPrettyPrinting().create()
   }
}