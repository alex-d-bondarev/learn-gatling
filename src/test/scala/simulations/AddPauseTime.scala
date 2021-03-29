package test.scala.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AddPauseTime extends Simulation {

  val httpConf = http.baseUrl("http://127.0.0.1:5000")
    .header("Accept", "application/json")

  val scn = scenario("Flask Simple App")
    .exec(
      http("Get Simple App About call")
        .get("/about")
        .check(status.is(200)))
    .pause(3)

    .exec(
      http("Navigate to Index page")
        .get("/about")
        .check(status.is(200)))
    .pause(2000.milliseconds)

    .exec(
      http("Navigate to Hello page")
        .get("/about")
        .check(status.is(200)))
    .pause(1, 4)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
