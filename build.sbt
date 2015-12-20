import java.util.jar.Attributes

val githubRepo = "osgirx"
val osgiVersion = "5.0.0"
val scalarxVersion = "0.2.8"
val akkaHttpVersion = "2.0-M2"
val akkaVersion = "2.3.14"

lazy val commonSettings = Seq(
  organization := "com.github.maprohu",
  version := "0.1.1-SNAPSHOT",
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some(sbtglobal.SbtGlobals.devops)
//      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  credentials += sbtglobal.SbtGlobals.devopsCredentials,
  resolvers += sbtglobal.SbtGlobals.devops,
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


lazy val core = project
  .enablePlugins(SbtOsgi)
  .settings(
    osgiSettings,
    commonSettings,
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
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-osgi" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion
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
