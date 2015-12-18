import java.util.jar.Attributes

val githubRepo = "osgirx"
val osgiVersion = "5.0.0"
val scalarxVersion = "0.2.8"

lazy val commonSettings = Seq(
  organization := "com.github.maprohu",
  version := "0.1.0-SNAPSHOT",
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
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


lazy val osgirx = (project in file("."))
  .enablePlugins(SbtOsgi)
  .settings(
    osgiSettings,
    commonSettings,
    libraryDependencies ++= Seq(
      "com.github.maprohu" %% "scalarx" % scalarxVersion,
      "org.scala-sbt" %% "io" % "1.0.0-M3"
    )


  )