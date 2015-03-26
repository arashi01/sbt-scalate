val verify = taskKey[Unit]("")

enablePlugins(ScalatePrecompiler)

verify := {
  def f = (target in (Compile, scalate)).value / "index_jade.scala"
  assert(f.exists, s"Generated file `$f` does not exist!")
}

