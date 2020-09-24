package com.kyleu.projectile.models.feature.graphql

import com.kyleu.projectile.models.export.config.ExportConfiguration
import com.kyleu.projectile.models.export.typ.FieldType
import com.kyleu.projectile.models.export.typ.FieldType._

object ExportFieldGraphQLQuery {

  private[this] def isReq(req: Boolean): String = if (req) "!" else ""

  private[this] def graphQLType(config: ExportConfiguration, name: String, t: FieldType, req: Boolean): String = t match {
    case UnitType => "String" + isReq(req)

    case StringType => "String" + isReq(req)
    case EncryptedStringType => "String" + isReq(req)

    case BooleanType => "Boolean" + isReq(req)
    case ByteType => "Byte" + isReq(req)
    case ShortType => "Short" + isReq(req)
    case IntegerType => "Int" + isReq(req)
    case LongType => "Long" + isReq(req)
    case SerialType => "Long" + isReq(req)
    case FloatType => "Float" + isReq(req)
    case DoubleType => "Float" + isReq(req)
    case BigDecimalType => "BigDecimal" + isReq(req)

    case DateType => "LocalDate" + isReq(req)
    case TimeType => "LocalTime" + isReq(req)
    case TimestampType => "LocalDateTime" + isReq(req)
    case TimestampZonedType => "ZonedDateTime" + isReq(req)

    case RefType => "String" + isReq(req)
    case XmlType => "String" + isReq(req)
    case UuidType => "UUID" + isReq(req)

    case StructType(key, _) =>
      config.getModelOpt(key) match {
        case Some(_) => throw new IllegalStateException("TODO: Struct types")
        case None => "String" + isReq(req)
      }

    case EnumType(key) =>
      config.getEnumOpt(key) match {
        case Some(enum) => enum.propertyName + "EnumType" + isReq(req)
        case None => throw new IllegalStateException(s"Cannot load enum for field [$name]")
      }
    case ListType(typ) => s"[${graphQLType(config, name, typ, false)}]" + isReq(req)
    case SetType(typ) => s"[${graphQLType(config, name, typ, false)}]" + isReq(req)

    case JsonType => "Json" + isReq(req)
    case CodeType => "String" + isReq(req)
    case TagsType => "String" + isReq(req)
    case ByteArrayType => "[String]" + isReq(req)
    case x if !x.isScalar => x.value + isReq(req)

    case typ => throw new IllegalStateException(s"[$typ] is not currently supported in GraphQL")
  }

  def buildKeyTypes(config: ExportConfiguration, name: String, t: FieldType, required: Boolean) = graphQLType(config, name, t, required)
}
