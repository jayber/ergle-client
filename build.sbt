
scalacOptions += "-target:jvm-1.7"

//unmanagedJars in Compile += Attributed.blank(file(scala.util.Properties.javaHome) / "lib" / "jfxrt.jar")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.springframework" % "spring-context" % "3.2.2.RELEASE",
  "com.typesafe.play" %% "play" % "2.2.1",
  "org.specs2" %% "specs2" % "2.3.7" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
  "javax.inject" % "javax.inject" % "1",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  "com.typesafe.akka" %% "akka-actor" % "2.2.3"
)
