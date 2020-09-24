// scalastyle:off file.size.limit
package com.kyleu.projectile.models.feature.graphql.db

import com.kyleu.projectile.models.export.config.ExportConfiguration
import com.kyleu.projectile.models.export.typ.FieldType
import com.kyleu.projectile.models.export.{ExportField, ExportModel}
import com.kyleu.projectile.models.feature.graphql.ExportFieldGraphQLQuery.buildKeyTypes
import com.kyleu.projectile.models.output.file.GraphQLFile

object GraphQLQueryFiles {
  def export(config: ExportConfiguration, model: ExportModel) = if (model.pkFields.isEmpty) {
    Seq(exportQuery(config, model))
  } else {
    val args = model.pkFields.map(f => f.propertyName + ": " + argValFor(f.t)).mkString(", ")
    Seq(exportQuery(config, model), exportQueryOne(config, model, model.pkFields), exportCreate(config, model), exportUpdate(config, model, args), exportRemove(config, model, args))
  }

  private[this] def addField(f: ExportField, file: GraphQLFile) = f.t match {
    case FieldType.TagsType =>
      file.add(f.propertyName + " {", 1)
      file.add("k")
      file.add("v")
      file.add("}", -1)
    case _ => file.add(f.propertyName)
  }

  private[this] def argValFor(t: FieldType) = t match {
    case FieldType.UuidType => "\"00000000-0000-0000-0000-000000000000\""
    case FieldType.IntegerType | FieldType.LongType | FieldType.SerialType => "0"
    case FieldType.FloatType | FieldType.DoubleType => "0.0"
    case _ => "\"\""
  }

  private[this] def exportQueryOne(config: ExportConfiguration, model: ExportModel, args: Seq[ExportField]) = {
    val file = GraphQLFile("explore" +: model.pkg, model.className + ".one")
    val keys = args.map(f => s"$$${f.propertyName}:${buildKeyTypes(config, f.propertyName, f.t, f.required)}")
    val params = args.map(f => s"${f.propertyName}:$$${f.propertyName}")

    file.add(s"# Queries the system for ${model.plural}.")
    file.add(s"query ${model.className}One (${keys.mkString(", ")}) {", 1)
    file.add(model.propertyName + "(", 1)
    file.add(params.mkString(", "))
    file.add(") {", -1)
    file.indent()

    model.fields.foreach(f => addField(f, file))
    file.add("}", -1)
    file.add("}", -1)

    file
  }

  private[this] def exportQuery(config: ExportConfiguration, model: ExportModel) = {
    val file = GraphQLFile("explore" +: model.pkg, model.className + ".search")
    file.add(s"# Queries the system for ${model.plural}.")
    file.add(s"query ${model.className}Search {", 1)
    file.add(s"${model.propertyName}Search (", 1)
    file.add("q: null, # Or string literal")
    file.add("""filters: null, # Or filters of type `{ k: "", o: Equal, v: "" }`""")
    file.add("""orderBy: null, # Or orderBy of type `{ col: "", dir: Ascending }`""")
    file.add("limit: null, # Or number")
    file.add("offset: null # Or number")
    file.add(") {", -1)
    file.indent()

    file.add("totalCount")
    file.add("paging {", 1)
    file.add("current")
    file.add("next")
    file.add("itemsPerPage")
    file.add("}", -1)
    file.add("results {", 1)
    model.fields.foreach(f => addField(f, file))
    file.add("}", -1)
    file.add("durationMs")
    file.add("occurred")

    file.add("}", -1)
    file.add("}", -1)

    file
  }

  private[this] def exportCreate(config: ExportConfiguration, model: ExportModel) = {
    val file = GraphQLFile("explore" +: model.pkg, model.className + ".create")

    file.add(s"# Creates a new ${model.className} entity")
    file.add(s"mutation ${model.className}Create {", 1)
    file.add(s"${model.propertyName}Mutation {", 1)
    file.add(s"""${model.propertyName}Create(fields: [{k: "", v: ""}]) {""", 1)
    model.fields.foreach(f => addField(f, file))
    file.add("}", -1)
    file.add("}", -1)
    file.add("}", -1)

    file
  }

  private[this] def exportUpdate(config: ExportConfiguration, model: ExportModel, args: String) = {
    val file = GraphQLFile("explore" +: model.pkg, model.className + ".update")

    file.add(s"# Updates a single ${model.className} entity using the provided fields")
    file.add(s"mutation ${model.className}Update {", 1)
    file.add(s"${model.propertyName}Mutation {", 1)
    file.add(s"""${model.propertyName}Update($args, fields: [{k: "", v: ""}]) {""", 1)
    model.fields.foreach(f => addField(f, file))
    file.add("}", -1)
    file.add("}", -1)
    file.add("}", -1)

    file
  }

  private[this] def exportRemove(config: ExportConfiguration, model: ExportModel, args: String) = {
    val file = GraphQLFile("explore" +: model.pkg, model.className + ".remove")

    file.add(s"# Remove a single ${model.className} entity from the system")
    file.add(s"mutation ${model.className}Remove {", 1)
    file.add(s"${model.propertyName}Mutation {", 1)
    file.add(s"${model.propertyName}Remove($args) {", 1)
    model.fields.foreach(f => addField(f, file))
    file.add("}", -1)
    file.add("}", -1)
    file.add("}", -1)

    file
  }
}
