
name := "SimpleGatlingScala_2.11.8"

version := "0.1"

scalaVersion := "2.11.8"

enablePlugins(GatlingPlugin)

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2" % "test"

libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.2.2" % "test"