import sbt._
import Dependencies._

name := "sbt-play-slick-codegen"
organization := "com.github.kanischev.sbt.slick"
version := "0.9.1"

scalaVersion in Global := "2.12.4"

sbtPlugin := true

lazy val root = project in file(".")

resolvers ++= Seq(
  Resolver.mavenLocal,
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")

libraryDependencies ++= playSlickCodegenDependencies
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % PlayVersion)
