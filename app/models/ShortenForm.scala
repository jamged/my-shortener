package models

import play.api.data.Form
import play.api.data.Forms._

object ShortenForm {
  val form = Form(
    single(
      "longUrl" -> nonEmptyText
    )
  )
}
