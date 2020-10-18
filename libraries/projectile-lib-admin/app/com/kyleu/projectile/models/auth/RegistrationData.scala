package com.kyleu.projectile.models.auth

final case class RegistrationData(
    username: String = "",
    phone: String = "",
    email: String = "",
    password: String = ""
)
