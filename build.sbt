name := "Salutem"
version := "1.0"
scalaVersion := "2.11.11"

resolvers +=
"Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

mainClass in assembly := Some("work.payne.salutem.Boot")

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.0"
libraryDependencies += "com.pi4j" % "pi4j-core" % "1.0"
libraryDependencies ++= {
  val akkaV = "10.0.9"
//  val sprayV = "1.3.2"
  val awsV = "1.10.71"
  Seq(
    "com.typesafe.akka"   %% "akka-http-spray-json" % akkaV,
    "com.typesafe.akka"   %% "akka-http"      % akkaV
//    "com.typesafe.akka"   %%  "akka-actor"    % "2.5.4",
//    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test"
  )
}


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case x => MergeStrategy.first
}