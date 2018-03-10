// scalacOptions
// See `scalac -help`, `scalac -X`, or `scalac -Y`
lazy val commonScalacOptions = Seq(
  "-feature" // Emit warning and location for usages of features that should be imported explicitly.
  , "-deprecation" // Emit warning and location for usages of deprecated APIs.
  , "-unchecked" // Enable additional warnings where generated code depends on assumptions.
  , "-Xlint"
  , "-encoding" // Specify encoding of source files
  , "UTF-8"
  // , "-Xfatal-warnings"
  , "-language:_"
  , "-Ywarn-adapted-args" // Warn if an argument list is modified to match the receiver
  , "-Ywarn-dead-code" // Warn when dead code is identified.
  , "-Ywarn-inaccessible" // Warn about inaccessible types in method signatures.
  , "-Ywarn-infer-any" // Warn when a type argument is inferred to be `Any`.
  , "-Ywarn-nullary-override" // Warn when non-nullary `def f()' overrides nullary `def f'
  , "-Ywarn-nullary-unit" // Warn when nullary methods return Unit.
  , "-Ywarn-numeric-widen" // Warn when numerics are widened.
  , "-Ywarn-unused" // Warn when local and private vals, vars, defs, and types are unused.
  , "-Ywarn-unused-import" // Warn when imports are unused.
)

lazy val commonSettings = Seq(
  organization := "com.tiqwab",
  scalaVersion := "2.12.4",
  scalacOptions := commonScalacOptions,
  scalacOptions in (Compile, console) -= "-Ywarn-unused",
  scalacOptions in (Compile, console) -= "-Ywarn-unused-import",
  scalacOptions in (Compile, console) -= "-Xlint",
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % versions.logback,
    "com.typesafe.scala-logging" %% "scala-logging" % versions.scalaLogging,
    "org.scalatest" %% "scalatest" % versions.scalaTest % Test
  ),
)

lazy val versions = new {
    val logback = "1.2.3"
    val scalaLogging = "3.7.2"
    val scalaTest = "3.0.1"
}

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "sbt-s3-resolver-plugin-sample",
    version := "0.1.0",
  ).aggregate(releaseSample, resolveSample)

lazy val releaseSample = (project in file("release-sample"))
  .settings(
    commonSettings,
    name := "s3-release-sample",
    version := "0.1.0",
    publishMavenStyle := false,
    publishTo := {
      val prefix = if (isSnapshot.value) "snapshots" else "releases"
      Some(s3resolver.value(s"My ${prefix} S3 bucket", s3(s"${prefix}.tiqwab.com")) withIvyPatterns)
    }
  )

lazy val resolveSample = (project in file("resolver-sample"))
  .settings(
    commonSettings,
    name := "s3-resolver-sample",
    version := "0.1.0",
    resolvers ++= Seq[Resolver](
      s3resolver.value("Release resolver", s3("releases.tiqwab.com")) withIvyPatterns,
      s3resolver.value("Snapshot resolver", s3("snapshots.tiqwab.com")) withIvyPatterns
    ),
    libraryDependencies += "com.tiqwab" %% "s3-release-sample" % "0.1.0"
  )
