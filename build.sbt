name := "Hello"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.11"

libraryDependencies ++= Seq(
  "ch.qos.logback"     %  "logback-classic"                   % "1.1.7",
  "ch.qos.logback"     %  "logback-core"                      % "1.1.7",
  "org.slf4j"          %  "slf4j-api"                         % "1.7.21",
  "org.postgresql"     %  "postgresql"                        % "9.3-1100-jdbc4",
  "org.flywaydb"       %  "flyway-core"                       % "4.0.3",
  "com.typesafe.akka"  %% "akka-http-core"                    % akkaVersion,
  "com.typesafe.akka"  %% "akka-http-experimental"            % akkaVersion,
  "com.typesafe.akka"  %% "akka-http-jackson-experimental"    % akkaVersion,
  "com.typesafe.akka"  %% "akka-http-spray-json-experimental" % akkaVersion,
  "com.typesafe.slick" %% "slick"                             % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp"                    % "3.1.1",
  "io.monix"           %% "monix"                             % "2.1.2",
  "org.scalatest"      %% "scalatest"                         % "3.0.1" % "test"
)

