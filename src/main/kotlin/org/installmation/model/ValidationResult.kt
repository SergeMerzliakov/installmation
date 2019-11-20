package org.installmation.model

class ValidationResult(var success:Boolean) {
   
   val errors = mutableListOf<String>()
}