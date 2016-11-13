import play.api.mvc.{Action, Controller}

object AnalizerServer extends Controller {
  def main(args: Array[String]): Unit = {

  }

  def hello() = Action {
    Ok("Simple response from play")
  }
}
