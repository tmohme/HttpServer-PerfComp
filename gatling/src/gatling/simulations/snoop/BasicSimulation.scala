package snoop

class BasicSimulation extends Simulation {
  val maxUsersPerSec = 100
  val minUsersPerSec = maxUsersPerSec / 5
  val rampUpDuration = 10 seconds
  val peakDuration = 1 minutes

  val httpConf = http
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")


  val springboot = scenario("Spring Boot")
    .repeat(100) {
      exec(http("spring boot")
        .get("http://localhost:8080/"))
    }

  val netty = scenario("Netty")
    .repeat(100) {
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