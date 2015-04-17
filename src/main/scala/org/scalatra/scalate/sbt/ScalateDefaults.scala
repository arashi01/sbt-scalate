/*******************************************************************
 *  See the NOTICE file distributed with this work for additional  *
 *  information regarding Copyright ownership.  The author and/or  *
 *  authors license this file to you under the terms of the Apache *
 *  License Version 2.0 (the "License"); you may not use this file *
 *  except in compliance with the License.  You may obtain a copy  *
 *  of the License at:                                             *
 *                                                                 *
 *     http://www.apache.org/licenses/LICENSE-2.0                  *
 *                                                                 *
 *  Unless required by applicable law or agreed to in writing,     *
 *  software distributed under the License is distributed on an    *
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,   *
 *  either express or implied.  See the License for the specific   *
 *  language governing permissions and limitations under the       *
 *  License.                                                       *
 *******************************************************************/

package org.scalatra.scalate.sbt

import sbt._
import Keys._
import NameFilter._
import ScalateImport._

import collection.mutable.ArrayBuffer


object ScalateDefaults {

  val scalateSettings = configSettings ++ pathSettings ++ sourceSettings ++ taskSettings ++ Seq(
    scalateVersion := "1.7.2-SNAPSHOT",
    scalateOrganisation := "org.scalatra.scalate",
    scalateBootClass := None,
    scalateAutoLibs := true,
    scalatePackagePrefix := "",
    scalateTemplateImports := Seq("_root_.org.fusesource.scalate.support.TemplateConversions._",
      "_root_.org.fusesource.scalate.util.Measurements._"),
    scalateEscapeMarkup := true,
    ivyConfigurations += Scalate,
    libraryDependencies ++= {
      def conf = if (scalateAutoLibs.value) Compile else Scalate
      def core = scalateOrganisation.value %% "scalate-core" % scalateVersion.value % conf exclude(
        scalaOrganization.value, "scala-compiler") exclude("org.scala-lang", "scala-compiler")
      def compiler = scalaOrganization.value % "scala-compiler" % scalaVersion.value % Scalate
      def tool = core.copy(name = "scalate-tool", configurations = Some(Scalate.name))
      def slf4jNoOp = "org.slf4j" % "slf4j-nop" % "1.7.12" % Scalate
      if (scalateAutoLibs.value) Seq(tool, compiler, core, slf4jNoOp) else Seq(tool, slf4jNoOp)},
    resolvers += "FuseSource Maven" at "http://repo.fusesource.com/nexus/content/groups/public/" // FIXME: For now Scalate uses a custom Karaf version.
  )

  def pathSettings = inConfig(Scalate)(Seq(
    sourceDirectory := crossTarget.value / Scalate.name / ".config",
    sourceDirectories in scalate := Seq((sourceDirectory in Compile).value / Scalate.name),
    target in scalate := crossTarget.value / Scalate.name / Defaults.nameForSrc(configuration.value.name),
    managedSourceDirectories <+= (target in scalate)
  ))

  def sourceSettings = inConfig(Scalate)(Seq(
    watchSources in Defaults.ConfigGlobal <++= sources in scalate,
    includeFilter in scalate := { (name: String) ⇒
      name.endsWith(".jade") || name.endsWith(".scaml") || name.endsWith(".ssp") || name.endsWith(".mustach")},
    excludeFilter in scalate := HiddenFileFilter,
    sources in scalate <<= Defaults.collectFiles(
      sourceDirectories in scalate,
      includeFilter in scalate,
      excludeFilter in scalate)
  ))


  def taskSettings = inConfig(Scalate)(Seq(
    scalate <<= generateScalaTask
  )) ++ inConfig(Compile)(Seq(scalate <<= (scalate in Scalate)))


  def configSettings = inConfig(Scalate)(Defaults.configSettings :+ (sourceGenerators <+= scalate)) ++ Seq(
    mappings in(Compile, packageSrc) <++= (mappings in(Scalate, packageSrc)),
    mappings in(Compile, packageBin) <++= (mappings in(Scalate, packageBin))
  )

  def generateScalaTask = Def.task {
    val target = (Keys.target in scalate).value
    target.mkdirs()
    def mappings: Seq[(File, String)] = (sources in scalate).value collect {
      case src if !src.isDirectory ⇒
        val sourceDir = (sourceDirectories in scalate).value.find(p ⇒ src.toPath.startsWith(p.toPath)).get
        (src, src.relativeTo(sourceDir).map(_.getPath).get)
    }
    mappings foreach { p ⇒
        def forkOpts(javaHome: Option[File], cp: Seq[File], opts: String*): ForkOptions = ForkOptions(
          javaHome,
          runJVMOptions = Seq("-cp", cp.mkString(java.io.File.pathSeparator)) ++ opts
        )
        def opts = ForkOptions(javaHome.value,
          runJVMOptions = Seq("-cp", (dependencyClasspath in Scalate).value map (_.data) mkString java.io.File.pathSeparator))
        def args = {
          val buffer = ArrayBuffer(
            "org.fusesource.scalate.tool.ScalateMain",
            "generate-scala",
            s"--working-directory=${streams.value.cacheDirectory.getAbsolutePath}",
            s"--escape-markup=${scalateEscapeMarkup.value.toString}")
          scalateBootClass.value foreach (b ⇒ buffer += s"--boot-class=$b")
          Option(scalatePackagePrefix.value) foreach (p ⇒ if (p.nonEmpty) buffer += s"--package-prefix=$p")
          scalateTemplateImports.value.foreach(i ⇒ buffer += s"--template-imports=$i")
          buffer ++ Array(p._1.absolutePath, p._2, (target / (p._2.replaceAll("[.]", "_") + ".scala")).absolutePath)
        }

        Fork.java(opts, args) // FIXME: Build successful even if pre-compilation failed.
    }
    ((target ** "**.scala") --- target).get
  }


}
