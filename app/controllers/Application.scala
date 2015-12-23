package controllers

import java.sql
import java.sql.Timestamp
import java.util.Calendar

import play.api._
import play.api.mvc._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


import models._

class Application extends Controller {

  def index(message:Option[String] = None) = Action.async { implicit request =>
    ShortenedEntries.listAll() map { entries =>
      println("entries(there are " + entries.length + "): " + entries)
      Ok(views.html.index(message))
    }
  }

  def shorten() = Action { implicit request =>
    ShortenForm.form.bindFromRequest.fold(
      errorForm => Redirect(routes.Application.index(Some("Entry box is Empty!"))), // Handle error in form submission
      formData => {
        val newShortened = Shortener(formData)
        // Check DB for existing entry for this longURL, only add to the DB if we need to
        ShortenedEntries.findByLong(formData) map { foundEntry =>
          if (foundEntry.isEmpty) {
            ShortenedEntries.add(newShortened)
          }
        }
        // Redirect to our index() page, passing a message string with the new full shortURL
        Redirect(routes.Application.index(Some("Your shortened URL is: " + request.host + "/" + newShortened.shortUrl)))
      }
    )
  }

  def redirect(shortUrl: String) = Action.async { implicit request =>
    ShortenedEntries.findByShort(shortUrl) map { foundEntry =>
      if (foundEntry.isDefined) {
        // try to get original IP from 'x-forwarded-for' header, else just grab the ip from remoteAddress
        val ip = request.headers.get("x-forwarded-for").getOrElse(request.remoteAddress)
        val theTime = Calendar.getInstance().getTimeInMillis
        // Create new Hit and Insert into "hit" table
        HitEntries.add(Hit(0, foundEntry.get.id, new Timestamp(theTime), ip))
        // Redirect to longUrl
        Redirect(foundEntry.get.longUrl)
      }
      else {  // Redirect to index() if the shortUrl provided does not exist
        Redirect(routes.Application.index(Some("No mapping for shortUrl: " + shortUrl)))
      }
    }
  }

  def viewStats(shortUrl: String) = Action.async { implicit request =>
    ShortenedEntries.findByShort(shortUrl) map { foundEntry =>
      if (foundEntry.isDefined) {
        // Obtain all hits fo this shortUrl, and pass them to stats view
        val entries = HitEntries.getAllByShortened(foundEntry.get) map { hits =>
          Ok(views.html.stats(shortUrl, hits))
        }
        Await.result(entries, Duration.Inf)
      }
      else {  // Redirect to index() if the shortUrl provided does not exist
        Redirect(routes.Application.index(Some("No mapping for shortUrl: " + shortUrl)))
      }
    }
  }
}
