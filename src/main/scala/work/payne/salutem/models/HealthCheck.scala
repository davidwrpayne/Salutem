package work.payne.salutem.models

case class HealthCheck(
  status: String,
  api: Boolean,
  alarm: Boolean
) {

}
