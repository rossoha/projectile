package com.projectile.services.project

import com.projectile.models.input.Input
import com.projectile.models.project.member.ModelMember
import com.projectile.services.ProjectileService
import com.projectile.util.JsonSerializers._

class ModelMemberService(val svc: ProjectileService) {
  def saveModels(p: String, members: Seq[ModelMember]) = {
    val inputs = members.map(_.input).distinct.map(i => i -> svc.getInput(i)).toMap
    members.map(m => saveMember(p, inputs(m.input), m))
  }

  def removeModel(p: String, member: String) = {
    val f = fileFor(p, member)
    f.delete(true)
    s"Removed model [$member]"
  }

  private[this] def fileFor(p: String, k: String) = svc.cfg.projectDir(p) / "model" / (k + ".json")

  private[this] def saveMember(p: String, i: Input, member: ModelMember) = {
    val m = i.exportModel(member.key).apply(member)

    val f = fileFor(p, member.key)
    f.createFileIfNotExists(createParents = true)
    f.overwrite(printJson(member.asJson))
    m
  }
}
