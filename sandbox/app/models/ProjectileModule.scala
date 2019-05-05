package models

import com.google.inject.{AbstractModule, Provides}
import com.kyleu.projectile.models.Application
import com.kyleu.projectile.models.auth.AuthActions
import com.kyleu.projectile.models.config.{NavHtml, UiConfig, UserSettings}
import com.kyleu.projectile.models.user.Role
import com.kyleu.projectile.services.database.JdbcDatabase
import com.kyleu.projectile.util.JsonSerializers.extract
import com.kyleu.projectile.util.metrics.MetricsConfig
import com.kyleu.projectile.util.tracing.{OpenTracingService, TracingService}
import com.kyleu.projectile.web.util.{ErrorHandler, GravatarUrl}
import io.circe.JsonObject
import models.template.UserMenu
import net.codingwell.scalaguice.ScalaModule
import util.Version

import scala.concurrent.ExecutionContext

class ProjectileModule extends AbstractModule with ScalaModule {
  @Provides @javax.inject.Singleton
  def providesTracingService(cnf: MetricsConfig, ec: ExecutionContext): TracingService = new OpenTracingService(cnf)(ec)

  @Provides @javax.inject.Singleton
  def providesJdbcDatabase(ec: ExecutionContext): JdbcDatabase = new JdbcDatabase("application", "database.application")(ec)

  @Provides @javax.inject.Singleton
  def providesApplicationActions = Application.Actions(
    projectName = Version.projectName,
    configForUser = (su, admin, notifications, crumbs) => su match {
      case None => UiConfig(projectName = Version.projectName, menu = UserMenu.guestMenu)
      case Some(u) =>
        val menu = if (admin) { UserMenu.adminMenu(u) } else { UserMenu.standardMenu(u) }

        val settings = extract[JsonObject](u.settings)
        val theme = settings("theme").map(extract[String]).getOrElse("dark")
        val user = UserSettings(name = u.username, theme = theme, avatarUrl = Some(GravatarUrl(u.email)))

        val html = NavHtml(com.kyleu.projectile.views.html.components.headerRightMenu(user.name, user.avatarUrl.getOrElse(""), notifications))

        val breadcrumbs = UserMenu.breadcrumbs(menu, crumbs)

        UiConfig(
          projectName = Version.projectName,
          menu = menu,
          html = html,
          user = user,
          notifications = notifications,
          breadcrumbs = breadcrumbs
        )
    }
  )

  @Provides @javax.inject.Singleton
  def providesErrorActions() = new ErrorHandler.Actions()

  @Provides @javax.inject.Singleton
  def providesAuthActions() = new AuthActions(projectName = Version.projectName) {
    override def allowRegistration = true
    override def defaultRole = Role.Admin
  }
}