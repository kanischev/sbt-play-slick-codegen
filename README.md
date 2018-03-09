# Play Slick Codegen plugin
This plugin runs Code Generation for slick tables in PlayFramework project.
It starts fake Play application with migrations applied to in-memory database and generates Slick code via this tables structure

Actually, I tried to add the whole code to separate project's module build.sbt like that:

```$scala
import java.nio.file.FileSystems

import Dependencies._
import com.typesafe.config.ConfigFactory
import play.api
import play.api.test.Helpers
import play.api.{Environment, Mode}
import play.api.db.Databases
import play.api.db.evolutions.{EnvironmentEvolutionsReader, Evolutions, ResourceEvolutionsReader}
import play.api.inject.guice.GuiceApplicationBuilder
import slick.codegen.SourceCodeGenerator

import scala.compat.Platform

libraryDependencies ++= slickModuleDependencies

lazy val generateSchema = taskKey[Seq[File]]("Generates slick representation classes for actual schema (runned on embedded database)")

generateSchema := {
  val outputDir = "src/main/scala"
  val pkg = "models"

/*
  println(s"Trying to use configuration under ${baseDirectory.value.getParentFile.getParentFile.getCanonicalPath}")

  val env = new Environment(
    baseDirectory.value.getParentFile.getParentFile,
    classOf[SourceCodeGenerator].getClassLoader,
    Mode.Test
  )

  val cfg = api.Configuration.load(env)
  val appBuilder = (env, cfg)
*/
  val app = new GuiceApplicationBuilder()
    .in(new Environment(baseDirectory.value.getParentFile.getParentFile / "conf" / "dev.conf",
      classOf[GuiceApplicationBuilder].getClassLoader,
      Mode.Test)
    ).build()
  Helpers.running(new GuiceApplicationBuilder().build()){
    Databases.withInMemory(urlOptions = Map(
      "MODE" -> "PostgreSQL"
    )) { database =>
      Evolutions.withEvolutions(database, new ResourceEvolutionsReader{
        def loadResource(db: String, revision: Int) = {
          Option(new File(
            baseDirectory.value.getParentFile.getParentFile, "conf/evolutions/default")).filter(_.exists()).map(f => java.nio.file.Files.newInputStream(f.toPath))
        }
      }) {
        println("Evolutions applied!")
        SourceCodeGenerator.main(
          Array(
            "slick.jdbc.PostgresProfile",
            "org.h2.Driver",
            database.url,
            outputDir,
            pkg,
            "",
            "")
        )
      }
    }
  }
  println("Tables source generated")
  val fname = outputDir + s"/${pkg.replace('.', '/')}/Tables.scala"
  Seq(file(fname))
}

sourceGenerators in Compile += generateSchema.taskValue
```
 but with no luck! com.typesafe.config used by sbt is quite old and does not work with modern play version!
 So, I decided to make it a separate plugin. The solution was inspired with
 [Tototoshi's plugin](https://github.com/tototoshi/sbt-slick-codegen)
 
 Not yet published anywhere - use publishLocal / publishM2 and local resolver for now
 Does not work correctly with the last PlayFramework version!