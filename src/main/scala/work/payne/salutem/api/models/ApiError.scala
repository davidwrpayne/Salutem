package work.payne.salutem.api.models

/**
  * Created by dpayne on 12/3/17.
  */
case class ApiError(status: Int, message: String, errors: Seq[String]) extends Product {

}
