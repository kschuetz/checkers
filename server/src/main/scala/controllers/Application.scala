package controllers

import com.google.inject.Inject
import play.api.{Configuration, Environment}
import play.api.mvc._

class Application @Inject()(implicit val config: Configuration, env: Environment) extends Controller {

  def index = Action {
    Ok(views.html.index("Checkers"))
  }

  def logging = Action(parse.anyContent) {
    implicit request =>
      request.body.asJson.foreach { msg =>
        println(s"CLIENT - $msg")
      }
      Ok("")
  }
}
