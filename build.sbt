name := "Twony"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" % "log4j-core"                         % "2.8",
  "org.slf4j"                %  "slf4j-api"                         % "1.7.21",
  "org.postgresql"           %  "postgresql"                        % "9.3-1100-jdbc4",
  "org.flywaydb"             %  "flyway-core"                       % "4.0.3",
  "com.typesafe.akka"        %% "akka-http"                         % "10.0.3",
  "com.typesafe.akka"        %% "akka-http-spray-json"              % "10.0.3",
  "com.typesafe.akka"        %% "akka-stream"                       % "2.4.16",
  "com.typesafe.slick"       %% "slick"                             % "3.1.1",
  "com.typesafe.slick"       %% "slick-hikaricp"                    % "3.1.1",
  "org.scalatest"            %% "scalatest"                         % "3.0.1" % "test"
)

