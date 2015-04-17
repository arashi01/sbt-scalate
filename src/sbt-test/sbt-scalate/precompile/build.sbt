val verify = taskKey[Unit]("")

enablePlugins(SbtScalate)

scalatePackagePrefix := "foo.bar"

scalateBootClass := Some("org.scalatra.scalate.sbt.test.precompile.Boot")

verify := {
  def f = (target in (Scalate, scalate)).value / "index_jade.scala"
  def cl = crossTarget.value / "scalate-classes" / "foo" / "bar" / "$_scalate_$index_jade.class"
  assert(f.exists, s"$f does not exist.")
  assert(cl.exists, s"$cl does not exist.")
}