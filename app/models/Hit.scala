package models

import java.sql.Timestamp
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._


// Case class for items stored in "hit" table
case class Hit (id: Long, shortened_id: Long, timestamp: Timestamp, ip: String)


// Slick table definition for "hit" table
class HitTableDef (tag: Tag) extends Table[Hit](tag, "hit") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def shortened_id = column[Long]("shortened_id")
  def timestamp = column[Timestamp]("hit_timestamp")
  def ip = column[String]("ip")

  // define foreign key relation to "Shortened" table
  def shortened = foreignKey("shortened_fk", shortened_id, ShortenedEntries.shortenedEntries)(_.id)

  override def * = {
    (id, shortened_id, timestamp, ip) <> (Hit.tupled, Hit.unapply)
  }
}

// Object for "hit" table transactions
object HitEntries {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val hitEntries = TableQuery[HitTableDef]

  def add(entry: Hit) {
    dbConfig.db.run(hitEntries += entry)
  }

  def get(id: Long): Future[Option[Hit]] = {
    dbConfig.db.run(hitEntries.filter(_.id === id).result.headOption)
  }

  def listAll(): Future[Seq[Hit]] = {
    dbConfig.db.run(hitEntries.result)
  }

  def getAllByShortened(shortened: Shortened): Future[Seq[Hit]] = {
    dbConfig.db.run(hitEntries.filter(_.shortened_id === shortened.id).sortBy(_.ip).result)
  }

}