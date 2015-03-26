val verify = taskKey[Unit]("")

enablePlugins(ScalateSiteGenerator)

verify := {
  def f = (target in (scalate)).value / "index.html"
  assert(f.exists, s"Generated file `$f` does not exist!")
}

