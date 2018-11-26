package com.projectile.models.database.input

import com.projectile.models.database.schema.View
import com.projectile.models.export.config.ExportConfigurationDefault
import com.projectile.models.export.{ExportEnum, ExportModel}
import com.projectile.models.output.ExportHelper.{toClassName, toDefaultTitle, toIdentifier}
import com.projectile.models.project.member.ModelMember.InputType

object ViewExportModel {
  def loadViewModel(v: View, enums: Seq[ExportEnum]) = {
    val cn = toClassName(v.name)
    val title = toDefaultTitle(cn)

    ExportModel(
      inputType = InputType.PostgresView,
      key = v.name,
      pkg = Nil,
      propertyName = toIdentifier(cn),
      className = cn,
      title = title,
      description = v.description,
      plural = title + "s",
      fields = loadViewFields(v, enums),
      pkColumns = Nil,
      foreignKeys = Nil,
      references = Nil
    )
  }

  private[this] def loadViewFields(v: View, enums: Seq[ExportEnum]) = v.columns.zipWithIndex.toList.map { col =>
    ExportConfigurationDefault.loadField(col = col._1, idx = col._2, indexed = false, unique = false, enums = enums)
  }
}