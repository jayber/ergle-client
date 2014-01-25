scalacOptions += "-target:jvm-1.7"

unmanagedJars in Compile += Attributed.blank(file(scala.util.Properties.javaHome) / "lib" / "jfxrt.jar")

libraryDependencies ++= Seq(
    "com.ergle" %% "file-synch" % "latest.snapshot"
)