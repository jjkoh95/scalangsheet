name := """scalangsheet"""
organization := "jjkoh.com"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "jjkoh.com.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "jjkoh.com.binders._"

// https://mvnrepository.com/artifact/org.json/json
libraryDependencies += "org.json" % "json" % "20190722"
