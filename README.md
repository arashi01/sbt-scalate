sbt-scalate
====================

An [sbt][1] plugin for pre-compiling [Scalate][2] templates.

###### Supported sbt Versions:
- 0.13.8+

###### Supported Scalate Versions:
- 1.7.1+

Integrating Into Your Project
-----------------------------
*sbt-scalate* is an sbt *AutoPlugin* for generating Scala source code from Scalate templates, which is then included for
compilation during project packaging. (for more information on enabling or disabling sbt plugins, refer to the [sbt plugins tutorial][3]):

###### Installation:
Add the following to your **plugin** project definition file (for example `<PROJECT_ROOT>/project/plugins.sbt`):
```scala
addSbtPlugin("org.scalatra.scalate" % "sbt-scalate" % "0.2.0-SNAPSHOT")
```

#### Configuring for Template Pre-compilation.
To configure a project for template pre-compilation, enable the `SbtScalate` AutoPlugin.

#### Configurable Settings
- `scalateAutoLibs`: Boolean determining whether Scalate dependencies are appended to project library-dependencies (set to *true* by default).
- `scalateBootClass`: Optional string defining the Scalate boot class name.
- `scalateEscapeMarkup`: Boolean determining whether sensitive markup characters are escaped for HTML/XML elements (set to *true* by default).
- `scalateImportStatements`: List of Strings of names members to be imported by generated Scala sources set to
  `Seq("_root_.org.fusesource.scalate.support.TemplateConversions._", "_root_.org.fusesource.scalate.util.Measurements._")` by default).
- `scalateOrganisation`: String allows specifying a custom Scalate library groupID (set to `org.scalatra.scalate` by default).
- `scalatePackagePrefix`: String defining the package prefix for generated Scala source (set to the empty String by default).
- `scalateVersion`: String defining the revision of Scalate used for template processing (set to `1.7.1-SNAPSHOT` by default).
- `sourceDirectories in (Scalate, scalate)`: List of File directories containing Scalate templates (set to `(sourceDirectory in Compile).value / "scalate"` - usually `src/main/scalate` - by default).


License
-------
*sbt-scalate* is open source software, released under the terms of the **Apache Software License, Version 2.0** (the "License"). A copy
of the License may be obtained at:
    http://www.apache.org/licenses/LICENSE-2.0 

[1]: http://www.scala-sbt.org
[2]: http://scalate.github.io/scalate
[3]: http://www.scala-sbt.org/0.13/docs/Plugins.html