package com.kyleu.projectile.models.feature.graphql

import com.kyleu.projectile.models.export.config.ExportConfiguration
import com.kyleu.projectile.models.export.typ.FieldType
import com.kyleu.projectile.models.export.typ.FieldType._

object ExportFieldGraphQL {
  private[this] def graphQLType(config: ExportConfiguration, name: String, t: FieldType): String = t match {
    case UnitType => "StringType"

    case StringType => "StringType"
    case EncryptedStringType => "StringType"

    case BooleanType => "BooleanType"
    case ByteType => "byteType"
    case ShortType => "shortType"
    case IntegerType => "IntType"
    case LongType => "LongType"
    case FloatType => "FloatType"
    case DoubleType => "DoubleType"
    case BigDecimalType => "BigDecimalType"

    case DateType => "localDateType"
    case TimeType => "localTimeType"
    case TimestampType => "localDateTimeType"
    case TimestampZonedType => "zonedDateTimeType"

    case RefType => "StringType"
    case XmlType => "StringType"
    case UuidType => "uuidType"

    case ObjectType(_, _) => throw new IllegalStateException("TODO: Objects")
    case StructType(key) => config.getModelOpt(key) match {
      case Some(_) => throw new IllegalStateException("TODO: Struct types")
      case None => "StringType"
    }

    case EnumType(key) => config.getEnumOpt(key) match {
      case Some(enum) => enum.propertyName + "EnumType"
      case None => throw new IllegalStateException(s"Cannot load enum for field [$name]")
    }
    case ListType(typ) => s"ListInputType(${graphQLType(config, name, typ)})"
    case SetType(typ) => s"ListInputType(${graphQLType(config, name, typ)})"
    case MapType(_, _) => throw new IllegalStateException("Maps are not supported in GraphQL")
    case UnionType(_, _) => throw new IllegalStateException("Unions are not currently supported in GraphQL")

    case JsonType => "jsonType"
    case CodeType => "StringType"
    case TagsType => "StringType"
    case ByteArrayType => "ArrayType(StringType)"
  }

  def argType(config: ExportConfiguration, name: String, t: FieldType, required: Boolean) = graphQLType(config, name, t)
}
