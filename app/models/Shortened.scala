package models

import java.util.zip.CRC32

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._


// Case class for items stored in "shortened" table
case class Shortened(id: Long, longUrl: String, shortUrl: String)


// Slick table definition for "shortened" table
class ShortenedTableDef(tag: Tag) extends Table[Shortened](tag, "shortened"){
  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def longUrl = column[String]("long_url")
  def shortUrl = column[String]("short_url")

  override def * =
    (id, longUrl, shortUrl) <>(Shortened.tupled, Shortened.unapply)
}


// Object for "shortened" table transactions
object ShortenedEntries {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val shortenedEntries = TableQuery[ShortenedTableDef]

  def add(entry: Shortened) = {
    dbConfig.db.run(shortenedEntries += entry)
  }

  def get(id: Long): Future[Option[Shortened]] = {
    dbConfig.db.run(shortenedEntries.filter(_.id === id).result.headOption)
  }

  def findByShort(shortUrl: String): Future[Option[Shortened]] = {
    dbConfig.db.run(shortenedEntries.filter(_.shortUrl === shortUrl).result.headOption)
  }

  def findByLong(longUrl: String): Future[Option[Shortened]] = {
    dbConfig.db.run(shortenedEntries.filter(_.longUrl === longUrl).result.headOption)
  }

  def listAll(): Future[Seq[Shortened]] = {
    dbConfig.db.run(shortenedEntries.result)
  }
}

// Helper object for generating new "Shorten" instances from a longURL
object Shortener {

  // Build a new Shortened instance out of a supplied fullUrl
  def apply(longUrl: String) = {
    var properUrl = longUrl
    val len = longUrl.length
    // Check our URL starts with 'http://' and prepend if it is missing
    if (!longUrl.startsWith("http://")){
      properUrl = "http://" + longUrl
    }
    val shortUrl = makeShort(properUrl)  // Generate the shortUrl
    new Shortened(0, properUrl, shortUrl)  // Create the full Shortened instance
  }

  // Generate shortUrl using the CRC32 of the longURL
  def makeShort(longUrl:String): String = {
    var crc = new CRC32()
    crc.update(longUrl.getBytes())
    make62(crc.getValue)  // convert to base 64 for shorter URL
  }

  def make62(long:Long): String = {
    val CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEVGHIJKLMNOPKRSTUVWXYZ_-"
    val BASE = 64

    var ret:String = ""
    var temp = long
    var remain:Long = 0

    while(temp > 0) {
      remain = temp % BASE
      ret += CHARS.charAt(remain.toInt)
      temp /= BASE
    }
    ret
  }
}