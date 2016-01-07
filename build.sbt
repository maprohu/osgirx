import java.util.jar.Attributes

val osgirxVersion = "0.1.3-SNAPSHOT"

val githubRepo = "osgirx"
val osgiVersion = "5.0.0"
val scalarxVersion = "0.2.8"
val akkaHttpVersion = "2.0-M2"
val akkaVersion = "2.3.14"
val refVersion = "0.1.0"

val repo = "http://localhost:38084"
val snapshots = "snapshots" at s"$repo/snapshots"
val releases = "releases" at s"$repo/releases"

lazy val commonSettings = Seq(
  organization := "com.github.maprohu",
  version := osgirxVersion,
  publishMavenStyle := true,
  publishTo := {
    if (isSnapshot.value)
      Some(snapshots)
    else
      Some(releases)
  },
  pomIncludeRepository := { _ => false },
  licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php")),
  homepage := Some(url(s"https://github.com/maprohu/${githubRepo}")),
  pomExtra := (
    <scm>
      <url>git@github.com:maprohu/{githubRepo}.git</url>
      <connection>scm:git:git@github.com:maprohu/{githubRepo}.git</connection>
    </scm>
      <developers>
        <developer>
          <id>maprohu</id>
          <name>maprohu</name>
          <url>https://github.com/maprohu</url>
        </developer>
      </developers>
    ),

  crossPaths := false,

  scalaVersion := "2.11.7",
  OsgiKeys.additionalHeaders ++= Map(
    "-noee" -> "true",
    Attributes.Name.IMPLEMENTATION_VERSION.toString -> version.value
  ),
  publishArtifact in packageDoc := false,
  OsgiKeys.exportPackage := Seq(organization.value + "." + name.value.replaceAll("-", ".")),
  OsgiKeys.privatePackage := OsgiKeys.exportPackage.value.map(_ + ".impl"),
  OsgiKeys.bundleActivator := Some(OsgiKeys.privatePackage.value(0) + ".Activator"),
  libraryDependencies ++= Seq(
    "org.osgi" % "org.osgi.core" % osgiVersion % Provided
  )
)


lazy val ref = project
  .enablePlugins(SbtOsgi)
  .settings(
    osgiSettings,
    commonSettings,
    name := "osgirx-ref",
    version := refVersion,
    libraryDependencies ++= Seq(
      "com.github.maprohu" % "scalarx" % scalarxVersion
    )
  )

lazy val core = project
  .enablePlugins(SbtOsgi)
  .dependsOn(ref)
  .settings(
    osgiSettings,
    commonSettings,
    name := "osgirx-core",
    libraryDependencies ++= Seq(
      "com.github.maprohu" % "scalarx" % scalarxVersion,
      "org.scala-sbt" %% "io" % "1.0.0-M3"
    )
  )

lazy val akkaApi = project
  .enablePlugins(SbtOsgi)
  .dependsOn(core)
  .settings(
    osgiSettings,
    commonSettings,
    name := "osgirx-akka-api",
    version := osgirxVersion,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-osgi" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion,
      "org.scala-lang.modules" %% "scala-async" % "0.9.5"
    )
  )

lazy val akka = project
  .enablePlugins(SbtOsgi)
  .dependsOn(akkaApi)
  .settings(
    osgiSettings,
    commonSettings,
    name := "osgirx-akka",
    libraryDependencies ++= Seq(
    )
  )

lazy val akkaHttp = project
  .enablePlugins(SbtOsgi)
  .dependsOn(akkaApi)
  .dependsOn(akka)
  .settings(
    osgiSettings,
    commonSettings,
    name := "osgirx-http",
    libraryDependencies ++= Seq(
    )
  )

lazy val root = (project in file("."))
  .aggregate(
    core,
    akka,
    akkaHttp,
    akkaApi
  )
  .settings(
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))
  )
