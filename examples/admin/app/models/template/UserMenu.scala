package models.template

import com.kyleu.projectile.models.config.{MenuProvider, NavMenu}
import com.kyleu.projectile.models.user.SystemUser

object UserMenu extends MenuProvider {
  private[this] lazy val system = NavMenu(key = "system", title = "System", children = NavMenu.all ++ Seq(
  ), flatSection = true)

  private[this] lazy val staticMenu = ComponentMenu.menu :+ system

  override def adminMenu(u: SystemUser) = staticMenu
}