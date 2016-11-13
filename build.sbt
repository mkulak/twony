// give the user a nice default project!
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.xap4o",
      scalaVersion := "2.11.8"
    )),
    name := "Hello"
  )

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies += "co.fs2" %% "fs2-core" % "0.9.2"
libraryDependencies += "co.fs2" %% "fs2-io" % "0.9.2"
//libraryDependencies += "io.spray" %% "spray-can" % "1.3.x"
libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.5.9"
libraryDependencies += "org.typelevel" %% "cats" % "0.7.2"
