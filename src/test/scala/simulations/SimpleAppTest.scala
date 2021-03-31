package test.scala.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class SimpleAppTest extends Simulation {

  private val warmUpPeriod = 5.seconds
  private val rumpUpPeriod = 20.seconds
  private val loadPeriod = 60.seconds
  private val tearDownPeriod = 15.seconds

  private val warmingUsers = 1
  private val loadUsers = 25

  private val maxRps = 150

  private val httpConf = http.baseUrl("http://127.0.0.1:5000")
    .header("Accept", "application/json")

  private val scn = scenario("Flask Simple App")
    .exec(getAboutCall()).pause(500.milliseconds)
    .exec(getIndexCall()).pause(500.milliseconds)
    .exec(getHelloCall()).pause(1.second)
    .exec(getAboutCall()).pause(500.milliseconds)
    .exec(getIndexCall()).pause(500.milliseconds)

  setUp(
    scn.inject(
      constantUsersPerSec(warmingUsers).during(warmUpPeriod),
      rampUsersPerSec(warmingUsers).to(loadUsers).during(rumpUpPeriod),
      constantUsersPerSec(loadUsers).during(loadPeriod),
      rampUsersPerSec(loadUsers).to(warmingUsers).during(tearDownPeriod),
    ).throttle(
      reachRps(maxRps).in(warmUpPeriod + rumpUpPeriod),
      holdFor(warmUpPeriod + rumpUpPeriod + loadPeriod + tearDownPeriod)
    )
  ).protocols(httpConf)

  private def getAboutCall() = exec(
    http("Get Simple App About call")
      .get("/about")
      .check(status.is(200)))

  private def getIndexCall() = exec(
    http("Navigate to Index page")
      .get("/about")
      .check(status.is(200)))

  private def getHelloCall() =
    repeat(2) {
      exec(
        http("Get Simple App About call")
          .get("/about")
          .check(status.is(200)))
    }

}
