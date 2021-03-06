package io.scalding.taps.hive

import com.twitter.scalding._
import cascading.hcatalog.{HCatTap, HCatScheme}
import cascading.tap.{Tap, SinkMode}
import cascading.tuple.Fields
import com.twitter.scalding.Local


case class HiveSource(
                  table: String,
                  sinkMode: SinkMode,
                  db: Option[String] = None,
                  filter: Option[String] = None,
                  hCatScheme: Option[HCatScheme] = None,
                  path: Option[String] = None,
                  sourceFields : Option[Fields] = None
                  ) extends Source {

  def createHCatTap : Tap[_, _, _] =
    new HCatTap(
      db.getOrElse(null),
      table,
      filter.getOrElse(null),
      hCatScheme.getOrElse(null),
      path.getOrElse(null),
      sourceFields.getOrElse(null),
      sinkMode
    ).asInstanceOf[Tap[_, _, _]]


  override def createTap(readOrWrite: AccessMode)(implicit mode: Mode): Tap[_, _, _] = {
    mode match {
      case Local(_) | Hdfs(_, _) => createHCatTap

      case _ => super.createTap(readOrWrite)(mode)
    }
  }

  def withSourceFields(fields: Fields) = copy(sourceFields = Some(fields))
  def withDb(db: String) = copy(db = Some(db))
  def withFilter(filter: String) = copy(filter = Some(filter))
}
