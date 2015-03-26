name := "sbt-scalate"

organization := "org.scalatra.scalate"

version := "0.1.0-SNAPSHOT"

description := "An sbt plugin for pre-compiling Scalate templates; and generating static websites."

homepage := Some(url("http://scalate.github.io/scalate"))

licenses += "The Apache Software License, Version 2.0" → url("http://www.apache.org/licenses/LICENSE-2.0")

scmInfo := Some(ScmInfo(url("https://github.com/scalate/sbt-scalate"),
  "scm:git:ssh://git@github.com:scalate/sbt-scalate.git"))

startYear := Some(2015)

sbtPlugin := true

scalacOptions in (Compile, compile) ~= (_ ++ Seq(Opts.compile.deprecation, "-Xlint"))

publish <<= PgpKeys.publishSigned

publishLocal <<= PgpKeys.publishLocalSigned

sonatypeSettings

scriptedSettings

scriptedRun <<= scriptedRun.dependsOn(publishLocal)

scriptedLaunchOpts <+= version { "-Dproject.version=" + _ }

developers := (
  Developer("arashi01", "Ali Salim Rashid", "a.rashid at zantekk dot com", url("https://github.com/arashi01")) :: Nil
  )