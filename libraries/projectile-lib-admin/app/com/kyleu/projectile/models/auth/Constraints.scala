package com.kyleu.projectile.models.auth

import play.api.data.Forms.text
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object Constraints {
  private val phoneRegex = """^\+38\(0[1-9]{2}\)\s?[0-9]{3}-[0-9]{2}-[0-9]{2}$""".r

  def phoneNumber(errorMessage: String = "error.required"): Constraint[String] = Constraint[String]("constraint.phone") { e =>
    if (e == null) { Invalid(ValidationError(errorMessage)) }
    else if (e.trim.isEmpty) { Invalid(ValidationError(errorMessage)) }
    else {
      phoneRegex
        .findFirstMatchIn(e)
        .map(_ => Valid)
        .getOrElse(Invalid(ValidationError(errorMessage)))
    }
  }

  def phoneNumber: Constraint[String] = phoneNumber()

  val phone: Mapping[String] = text.verifying(Constraints.phoneNumber)

}
