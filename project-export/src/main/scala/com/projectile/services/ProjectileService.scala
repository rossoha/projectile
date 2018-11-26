package com.projectile.services

import io.circe.Json
import com.projectile.models.command.{ProjectileCommand, ProjectileResponse}
import com.projectile.models.command.ProjectileCommand._
import com.projectile.models.command.ProjectileResponse._
import com.projectile.services.config.{ConfigService, ConfigValidator}
import com.projectile.services.input.InputHelper
import com.projectile.services.project.ProjectHelper

class ProjectileService(val cfg: ConfigService = new ConfigService(".")) extends InputHelper with ProjectHelper {
  val rootDir = better.files.File(cfg.path)
  val fullPath = rootDir.pathAsString

  def process(cmd: ProjectileCommand, verbose: Boolean = false): ProjectileResponse = {
    import com.projectile.models.command.ProjectileCommand._

    val processCore: PartialFunction[ProjectileCommand, ProjectileResponse] = {
      case Init => cfg.init()
      case Doctor => ConfigValidator.validate(cfg, verbose)
      case Testbed => JsonResponse(Json.True)
    }

    processCore.orElse(processInput).orElse(processProject).apply(cmd)
  }

  def init() = process(Init)
  def doctor() = process(Doctor)
  def testbed() = process(Testbed).asInstanceOf[JsonResponse]

  override val toString = s"Projectile Service @ ${cfg.workingDirectory}"
}