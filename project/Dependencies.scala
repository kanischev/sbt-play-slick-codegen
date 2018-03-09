import sbt._

object Dependencies {
  val SlickVersion = "3.2.1"
  val SlickCodegenVersion = "3.2.2"
  val PlaySlickVersion = "3.0.1"
  val PlayVersion = "2.6.10"
  val ScalaLoggingVersion = "3.8.0"
  val SlickPgVersion = "0.16.0"
  val H2Version = "1.4.196"
  val HikariCpVersion = "2.7.4"
  val PlayScalatestPlusVersion = "3.1.2"

  // Libraries
  val dPlayGuice = "com.typesafe.play" %% "play-guice" % PlayVersion
  val dTypesafeConfig = "com.typesafe" % "config" % "1.3.2"
  val dPlay = "com.typesafe.play" %% "play" % PlayVersion
  val dPlayJDBC = "com.typesafe.play" %% "play-jdbc" % PlayVersion
  val dPlayTest = "com.typesafe.play" %% "play-test" % PlayVersion
  val dPlayEvolutions = "com.typesafe.play" %% "play-jdbc-evolutions" % PlayVersion
  val dPlaySlick = "com.typesafe.play" %% "play-slick" % PlaySlickVersion
  val dPlaySlickEvolutions = "com.typesafe.play" %% "play-slick-evolutions" % PlaySlickVersion
  val dScalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion
  val dSlickCodegen = "com.typesafe.slick" %% "slick-codegen" % SlickCodegenVersion
  val dH2 = "com.h2database" % "h2" % H2Version
  val dHikariCP = "com.zaxxer" % "HikariCP" % HikariCpVersion
  val dPlayScalaTestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % PlayScalatestPlusVersion

  lazy val playSlickCodegenDependencies = Seq(
    dTypesafeConfig,
    dPlayGuice,
    dPlay,
    dPlayJDBC,
    dPlayTest,
    dPlayEvolutions,
    dPlaySlick,
    dPlaySlickEvolutions,
    dSlickCodegen,
    dScalaLogging,
    dPlayScalaTestPlus,
    dH2,
    dPlayTest
  )

}
