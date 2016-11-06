import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class HttpServerPerfComp extends Simulation {
  val repetitions = 100
  val maxUsersPerSec = 50
  val minUsersPerSec = maxUsersPerSec / 10
  val rampUpDuration = 30 seconds
  val peakDuration = 1 minutes

  val httpConf = http
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")


  val springboot = scenario("Spring Boot")
    .repeat(repetitions) {
      exec(http("spring boot")
        .get("http://localhost:8080/"))
    }

  val netty = scenario("Netty")
    .repeat(repetitions) {
      exec(http("netty")
        .get("http://localhost:8081/"))
    }

  setUp(
    springboot.inject(
      rampUsersPerSec(minUsersPerSec) to maxUsersPerSec during(rampUpDuration),
      constantUsersPerSec(maxUsersPerSec) during(peakDuration)
    ),
    netty.inject(
      rampUsersPerSec(minUsersPerSec) to maxUsersPerSec during(rampUpDuration),
      constantUsersPerSec(maxUsersPerSec) during(peakDuration)
    )
  ).protocols(httpConf)
}