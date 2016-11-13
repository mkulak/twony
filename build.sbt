name := "Hello"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.11"

libraryDependencies ++= Seq(
  "ch.qos.logback"    %   "logback-classic"                   % "1.1.7",
  "ch.qos.logback"    %   "logback-core"                      % "1.1.7",
  "org.slf4j"         %   "slf4j-api"                         % "1.7.21",
  "com.typesafe.akka" %%  "akka-http-core"                    % akkaVersion,
  "com.typesafe.akka" %%  "akka-http-experimental"            % akkaVersion,
  "com.typesafe.akka" %%  "akka-http-jackson-experimental"    % akkaVersion,
  "com.typesafe.akka" %%  "akka-http-spray-json-experimental" % akkaVersion
//  "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion,
//  "com.typesafe.akka" %% "akka-http-xml-experimental" % akkaVersion
)

//resolvers += "spray repo" at "http://repo.spray.io"

