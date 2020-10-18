package com.kyleu.projectile.models.user

import java.time.LocalDateTime
import java.util.UUID

import com.kyleu.projectile.models.config.UserSettings
import com.kyleu.projectile.models.result.data.{DataField, DataFieldModel, DataSummary}
import com.kyleu.projectile.util.DateUtils
import com.kyleu.projectile.util.JsonSerializers._
import io.circe.JsonObject

object SystemUser {
  implicit val jsonEncoder: Encoder[SystemUser] = deriveEncoder
  implicit val jsonDecoder: Decoder[SystemUser] = deriveDecoder

  def empty() = SystemUser(
    id = UUID.randomUUID,
    username = "",
    phone = "",
    profile = LoginCredentials("anonymous", "guest"),
    role = "user"
  )

  val system = SystemUser(
    id = UUID.fromString("88888888-8888-8888-8888-888888888888"),
    username = "",
    phone = "",
    profile = LoginCredentials("anonymous", "system"),
    role = "admin"
  )

  val guest = SystemUser(
    id = UUID.fromString("77777777-7777-7777-7777-777777777777"),
    username = "guest",
    phone = "",
    profile = LoginCredentials("anonymous", "guest"),
    role = "user"
  )

  val api = SystemUser(
    id = UUID.fromString("44444444-4444-4444-4444-444444444444"),
    username = "api",
    phone = "",
    profile = LoginCredentials("anonymous", "api"),
    role = "admin"
  )
}

final case class SystemUser(
    id: UUID,
    username: String,
    phone: String,
    profile: LoginCredentials,
    role: String,
    settings: Json = JsonObject.empty.asJson,
    created: LocalDateTime = DateUtils.now
) extends DataFieldModel {

  val email = profile.providerKey
  val provider = profile.providerID
  val key = profile.providerKey

  lazy val settingsObj = extract[UserSettings](settings)

  def isAdmin = role == "admin"

  override def toDataFields = Seq(
    DataField("id", Some(id.toString)),
    DataField("username", Some(username)),
    DataField("phone", Some(phone)),
    DataField("provider", Some(profile.providerID)),
    DataField("key", Some(profile.providerKey)),
    DataField("role", Some(role)),
    DataField("created", Some(created.toString))
  )

  def toSummary =
    DataSummary(
      model = "systemUser",
      pk = id.toString,
      entries = Map("Username" -> Some(username), "Phone" -> Some(phone), "Role" -> Some(role)))
}
