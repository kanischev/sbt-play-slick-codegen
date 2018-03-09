package com.github.kanischev.sbt.slick

import play.api
import play.api.Environment
import play.api.db.Databases
import play.api.db.evolutions.{Evolutions, ResourceEvolutionsReader}
import play.api.test.Helpers
import play.sbt.PlayScala
import sbt.Keys._
import sbt.{Def, _}
import slick.codegen.SourceCodeGenerator
import slick.jdbc.{JdbcProfile, PostgresProfile}

object PlaySlickCodegen extends AutoPlugin {
  override def requires = PlayScala
  override def trigger = allRequirements

  object autoImport {
    lazy val slickCodegenerator: TaskKey[Seq[File]] = taskKey[Seq[File]]("Command to run codegen")
    
    lazy val slickDatabases: SettingKey[Seq[String]] = settingKey[Seq[String]]("List of databases to be generated. \"default\" by default")
    lazy val slickDialectProfile: SettingKey[JdbcProfile] = settingKey[JdbcProfile]("The slick Profile for SQL-dialect")
    lazy val h2UrlOptions: SettingKey[Map[String, String]] = settingKey[Map[String, String]]("The map with h2 jdbc url properties")
    lazy val slickGenOutputDir: SettingKey[File] = settingKey[File]("The directory for Scala sources")
    lazy val tablesPackage: SettingKey[String] = settingKey[String]("The package to generate Tables source to")
    lazy val tablesFilePostfix: SettingKey[String] = settingKey[String]("Generated file's name postfix")

  }

  import autoImport._

  private def gen(
                   evolutionsDirectory: File,
                   databases: Seq[String],
                   slickDialectProfile: String,
                   h2UrlOptions: Map[String, String],
                   outputDir: String,
                   pkg: String,
                   fileNamePostfix: String,
                   s: TaskStreams): Seq[File] = {

    s.log.info(s"Generate source code with slick-codegen in embedded database")

    databases.flatMap(dbName => {
      val env = Environment.simple(new File("."))
      val configuration = api.Configuration.load(env, Map("config.resource" -> "application.conf"))
      Helpers.running(Helpers.baseApplicationBuilder.loadConfig(_ => configuration).build()){
        Databases.withInMemory(urlOptions = h2UrlOptions) { database =>
          Evolutions.withEvolutions(database, new ResourceEvolutionsReader{
            def loadResource(db: String, revision: Int) = {
              Option(evolutionsDirectory / dbName).filter(
                _.exists()).map(f => java.nio.file.Files.newInputStream(f.toPath)
              )
            }
          }) {
            s.log.info(s"Evolutions applied for $dbName!")
            SourceCodeGenerator.main(
              Array(
                slickDialectProfile,
                classOf[org.h2.Driver].getCanonicalName,
                database.url,
                outputDir,
                pkg,
                "",
                "")
            )
          }
        }
      }

      s.log.info(s"Tables source for $dbName generated")
      val fname = outputDir + s"/${pkg.replaceAllLiterally(".", "/")}/${Option(dbName).map(_.trim).filter(_ != "default").map(_.capitalize).getOrElse("")}Tables.scala"
      Seq(file(fname))
    })
  }

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    slickDialectProfile := PostgresProfile,
    slickDatabases := Seq("default"),
    h2UrlOptions := Map("MODE" -> "PostgreSQL"),
    slickGenOutputDir := (sourceManaged in Compile).value,
    tablesPackage := "models",
    tablesFilePostfix := "Tables.scala",

    slickCodegenerator := {
      val outDir = {
        val folder = slickGenOutputDir.value
        if (folder.exists()) {
          require(folder.isDirectory, s"file :[$folder] is not a directory")
        } else {
          folder.mkdir()
        }
        folder.getPath
      }
      gen(
        baseDirectory.value / "conf" / "evolutions",
        slickDatabases.value,
        slickDialectProfile.value.getClass.getCanonicalName,
        h2UrlOptions.value,
        outDir,
        tablesPackage.value,
        tablesFilePostfix.value,
        streams.value
      )
    },
    (managedSources in Compile) := (managedSources in Compile).value ++ slickCodegenerator.value
  )
}
