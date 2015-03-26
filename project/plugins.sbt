logLevel := Level.Warn

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.2")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

libraryDependencies <+= sbtVersion(sv => "org.scala-sbt" % "scripted-plugin" % sv)
