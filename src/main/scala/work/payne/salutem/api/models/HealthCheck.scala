package work.payne.salutem.api.models

import work.payne.salutem.api.ApiResponse

case class HealthCheck(
  status: String,
  api: Boolean,
  alarm: Boolean
) extends Product with ApiResponse {

}
