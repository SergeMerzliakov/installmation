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