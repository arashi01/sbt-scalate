sbt-scalate
====================

An [sbt][1] plugin for pre-compiling [Scalate][2] templates; and generating static websites.

###### Supported sbt Versions:
- 0.13.8+

###### Supported Scalate Versions:
- 1.7.1+

Integrating Into Your Project
-----------------------------
*sbt-scalate* provides the following *AutoPlugins* (for more information on enabling and disabling plugins,
refer to the [sbt plugins tutorial][4]):
- `ScalatePrecompiler`: Generates Scala source code from Scalate templates for compilation during project build.
- `ScalateSiteGenerator`: Generates a static website from Scala templates.

###### Installation:
Add the following to your **plugin** project definition file (for example `<PROJECT_ROOT>/project/plugins.sbt`):
```scala
addSbtPlugin("org.scalatra.scalate" % "sbt-scalate" % "0.1.0-SNAPSHOT")
```

#### Configuring for Template Pre-compilation.
To configure a project for template pre-compilation, enable the `ScalatePrecompiler` AutoPlugin.

#### Configuring for Template Pre-compilation.
To configure a project for static website generation, enable the `ScalateSiteGenerator` AutoPlugin.

#### Configuration Settings
- `scalateVersion`: String defining the revision of Scalate used for template processing (set to *1.7.1-SNAPSHOT* by default).
- `scalateBootClass`: Optional string defining the Scalate boot class name.
- `scalateAutoLibs`: Boolean determining whether Scalate dependencies are appended to project library-dependencies (set to *true* by default).

###### *ScalatePrecompiler* Specific Settings
- `scalateEscapeMarkup`: Boolean determining whether sensitive markup characters are escaped for HTML/XML elements (set to *true* by default).
- `scalateImportStatements`: List of Strings of names members to be imported by generated Scala sources set to
  `Seq("_root_.org.fusesource.scalate.support.TemplateConversions._", "_root_.org.fusesource.scalate.util.Measurements._")` by default).
- `scalatePackagePrefix`: String defining the package prefix for generated Scala source (set to the empty String by default).
- `sourceDirectories in (Compile, scalate)`: List of File directories containing Scalate templates (set to `(sourceDirectory in Compile).value / "scalate"` - usually `src/main/scalate` - by default).

###### *ScalateSiteGenerator* Settings
- `scalateAppDirectory`: File directory in which Scalate template files are located (set to `baseDirectory.value / "app"` - usually `<PROJECT_ROOT>/app` - by default).
- `target in scalate`: File directory in which static site is to be generated (set to `target.value / "generated-site"` - usually `target/generated-site` - by default).


License
-------
*sbt-scalate* is open source software, released under the terms of the **Apache Software License, Version 2.0** (the "License"). A copy
of the License may be obtained at:
    http://www.apache.org/licenses/LICENSE-2.0 

[1]: http://www.scala-sbt.org
[2]: http://scalate.github.io/scalate
[3]: https://github.com/scalate/scalate