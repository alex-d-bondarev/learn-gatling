package test.scala.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AddPauseTime extends Simulation {

  private val httpConf = http.baseUrl("http://127.0.0.1:5000")
    .header("Accept", "application/json")

  private val scn = scenario("Flask Simple App")
    .exec(getAboutCall()).pause(500.milliseconds)
    .exec(getIndexCall()).pause(2000.milliseconds)
    .exec(getHelloCall()).pause(1, 3)
    .exec(getAboutCall()).pause(200.milliseconds)
    .exec(getIndexCall()).pause(100.milliseconds)

  setUp(
    scn.inject(atOnceUsers(1))
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
