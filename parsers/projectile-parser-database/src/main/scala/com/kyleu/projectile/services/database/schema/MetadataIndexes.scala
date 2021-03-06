package com.kyleu.projectile.services.database.schema

import java.sql.DatabaseMetaData

import com.kyleu.projectile.models.database.schema.{Index, IndexColumn, Table}
import com.kyleu.projectile.services.database.query.{JdbcHelper, JdbcRow}

object MetadataIndexes {
  def getIndexes(metadata: DatabaseMetaData, t: Table) = {
    val rs = metadata.getIndexInfo(t.catalog.orNull, t.schema.orNull, t.name, false, false)
    val indexColumns = new JdbcRow.Iter(rs).map(fromRow).toList
    val indexes = indexColumns.groupBy(_._1).map { cols =>
      val idxCols = cols._2.sortBy(_._4).map { col =>
        IndexColumn(name = col._5, ascending = col._6)
      }
      val idxInfo = cols._2.headOption.getOrElse(throw new IllegalStateException("Missing index info"))
      Index(
        name = idxInfo._1,
        unique = idxInfo._2,
        indexType = idxInfo._3,
        columns = idxCols
      )
    }.toSeq

    indexes.sortBy(_.name)
  }

  private[this] def fromRow(row: JdbcRow) = {
    // [index_qualifier], [pages], [filter_condition]
    val name = row.asOpt[String]("index_name").getOrElse("null")
    val nonUnique = row.asOpt[Any]("non_unique").exists(JdbcHelper.boolVal)
    val position = row.asOpt[Any]("ordinal_position").fold(0)(JdbcHelper.intVal)
    val ascending = row.asOpt[String]("asc_or_desc").getOrElse("A") == "A"

    val columnName = row.asOpt[String]("column_name").getOrElse("null")
    val typ = JdbcHelper.intVal(row.as[Int]("type")) match {
      case DatabaseMetaData.tableIndexStatistic => "statistic"
      case DatabaseMetaData.tableIndexClustered => "clustered"
      case DatabaseMetaData.tableIndexHashed => "hashed"
      case DatabaseMetaData.tableIndexOther => "other"
      case x => throw new IllegalArgumentException(x.toString)
    }
    (name, !nonUnique, typ, position, columnName, ascending)
  }
}
