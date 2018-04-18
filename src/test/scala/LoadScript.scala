import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import java.util.concurrent.ThreadLocalRandom

class LoadScript extends Simulation {

  val httpProtocol = http
    .baseURL("http://computer-database.gatling.io")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0")

  val headers_0 = Map(
    "Accept" -> "*/*",
    "Pragma" -> "no-cache")

  val headers_1 = Map("Upgrade-Insecure-Requests" -> "1")

  val headers_3 = Map("Accept" -> "text/css,*/*;q=0.1")

  val headers_5 = Map("Accept" -> "*/*")

  val uri2 = "http://detectportal.firefox.com/success.txt"

  val scn = scenario("RecordedSimulation").exec(Search.search, Browse.browse, Edit.edit)

  val users = scenario("Users").exec(Search.search, Browse.browse)

  val admins = scenario("Admins").exec(Search.search, Browse.browse, Edit.edit)

  // Search
  object Search {

    val feeder = csv("search_copy.csv").random

    val search = exec(http("Home")
      .get("/")
      .headers(headers_0)
      .resources(http("request_1")
        .get(uri2 + "")
        .headers(headers_1)))
      .pause(15)
      .feed(feeder)
      .exec(http("Search")
        .get("/computers?f=${searchCriterion}")
        .check(css("a:contains('${searchComputerName}')", "href")
          .saveAs("computerURL"))
        .headers(headers_1))
      .pause(1)
      .exec(http("Select")
        .get("${computerURL}")
        .headers(headers_1))
      .pause(24)
  }

  object Browse {

    val  browse = repeat(5, "n") {
      exec(http("Page ${n}")
        .get("/computers?p=${n}"))
        .pause(1)

    }
  }

  //Edit
  object Edit {

    val edit = exec(http("Edit")
      .get("/computers?p=1")
      .headers(headers_1))
      .pause(3)
      .exec(http("request_13")
        .get("/computers/new")
        .headers(headers_1))
      .pause(37)
      .exec(http("request_14")
        .post("/computers")
        .check(status.is(session => 200 + ThreadLocalRandom.current.nextInt(2)))
        .headers(headers_1)
        .formParam("name", "Lenovo Ideapad")
        .formParam("introduced", "2015-12-12")
        .formParam("discontinued", "2017-04-10")
        .formParam("company", "36"))
    //#tryMax-exitHereIfFailed
    val tryMaxEdit = tryMax(2) {
      exec(edit)
    }.exitHereIfFailed
      //#tryMax-exitHereIfFailed
      .pause(4)
      .exec(http("request_15")
        .get(uri2 + "")
        .headers(headers_0))

  }

  setUp(
    users.inject(rampUsers(2) over (10 seconds)),
    admins.inject(rampUsers(1) over (10 seconds))
  ).protocols(httpProtocol)
}