package org.installmation.model

import com.google.gson.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.installmation.model.binary.JDKSerializer
import java.lang.reflect.Type

class ImageStructureSerializer : JsonSerializer<ImageStructure>, JsonDeserializer<ImageStructure> {

   companion object {
      const val CLASS_NAME = "class-name"
      const val DATA = "image-structure"

      val log: Logger = LogManager.getLogger(JDKSerializer::class.java)
   }

   override fun serialize(imageStructure: ImageStructure?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
      val json = JsonObject()
      json.addProperty(CLASS_NAME, imageStructure?.javaClass?.name)
      json.add(DATA, context?.serialize(imageStructure))
      return json
   }

   override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ImageStructure {
      val obj = json?.asJsonObject
      val className = obj?.get(CLASS_NAME)?.asJsonPrimitive?.asString
      val data = obj?.get(DATA)
      when (className) {
         SimpleImageStructure::class.java.name -> return context!!.deserialize(data, SimpleImageStructure::class.java)
      }
      throw LoadDataException("Unsupported ImageStructure type '$className' in json data")
   }
}
