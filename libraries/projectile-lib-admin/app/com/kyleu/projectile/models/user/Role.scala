package com.kyleu.projectile.models.user

import java.util.UUID

import enumeratum.values.{StringEnum, StringEnumEntry, StringCirceEnum}

sealed abstract class Role(override val value: String) extends StringEnumEntry {
  def qualifies(target: Role): Boolean
  override def toString = value
}

object Role extends StringEnum[Role] with StringCirceEnum[Role] {
  def apply(role: String): Role = Role.withValue(role)
  def unapply(role: Role): Option[String] = Some(role.toString)

  override val values = findValues

  object Admin extends Role("admin") {
    override def qualifies(target: Role) = true
  }
  object User extends Role("user") {
    override def qualifies(target: Role) = target == Role.User
  }

  def matchPermissions(user: Option[SystemUser], owner: UUID, model: String, perm: String, value: Permission) = {
    if (user.map(_.id).contains(owner)) {
      true -> s"You are the owner of this $model"
    } else {
      value match {
        case Permission.Administrator => if (user.map(_.role).contains(Role.Admin)) {
          true -> s"Administrators may $perm this $model"
        } else {
          false -> s"Only administrators are allowed to $perm this $model"
        }
        case Permission.User => if (user.map(_.role).contains(Role.Admin) || user.map(_.role).contains(Role.User)) {
          true -> s"All normal users may $perm this $model"
        } else {
          false -> s"Visitors are not allowed to $perm this $model"
        }
        case Permission.Visitor => true -> s"All users, including visitors, may $perm this $model"
        case Permission.Private => false -> s"Only the owner of this $model may $perm it"
        case x => false -> x.toString
      }
    }
  }
}
