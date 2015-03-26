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
import plugins.JvmPlugin


object SbtScalate extends AutoPlugin {
  override val requires: Plugins = JvmPlugin
  override val trigger: PluginTrigger = noTrigger

  val autoImport = Import

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = scalateSettings

  def scalateBaseSettings: Seq[Setting[_]] = Seq(
    sources in scalate <<= Defaults.collectFiles(
      sourceDirectories in scalate,
      includeFilter in scalate,
      excludeFilter in scalate),
    watchSources in Defaults.ConfigGlobal ++= (sources in scalate).value,
    target in scalate := crossTarget.value / "scalate" / Defaults.nameForSrc(configuration.value.name),
    scalateClasspaths <<= classpathTask,
    managedClasspath in scalateClasspaths <<= (classpathTypes, update) map { (ct, report) =>
      Classpaths.managedJars(Scalate, ct, report)
    }
  )

  def scalateSettings: Seq[Setting[_]] = inConfig(Compile)(scalateBaseSettings) ++
    inConfig(Test)(scalateBaseSettings) ++ scalateDependencies ++ Seq(
    includeFilter in scalate := AllPassFilter, //TODO: Allow only Scalate resources
    excludeFilter in scalate := HiddenFileFilter,
    scalateBootClass := None,
    scalateVersion := "1.7.1-SNAPSHOT",
    scalateAutoLibs := true
  )

  def scalateDependencies: Seq[Setting[_]] = Seq(
    ivyConfigurations += Scalate,
    libraryDependencies ++= {
      def core = "org.scalatra.scalate" %% "scalate-core" % scalateVersion.value exclude(
        scalaOrganization.value, "scala-compiler")
      def compiler = scalaOrganization.value % "scala-compiler" % scalaVersion.value % Scalate
      def tool = core.copy(name = "scalate-tool", configurations = Some("Scalate"))
      if (scalateAutoLibs.value) Seq(tool, compiler, core)
      else
        Seq(tool)
    }
  )

  def classpathTask = (fullClasspath in Runtime, managedClasspath in scalateClasspaths) map { (p1, p2) ⇒
    ScalateClasspaths(p1 map (_.data), p2 map (_.data))
  }

}

// Copied from xsbt-scalate-generate
final case class ScalateClasspaths(classpath: PathFinder, scalateClasspath: PathFinder)

object Import {

  val Scalate = config("Scalate").hide

  val scalateVersion = settingKey[String]("The revision of Scalate used for compilation.")
  val scalate = taskKey[Seq[File]]("Process Scalate templates.")
  val scalateBootClass = settingKey[Option[String]]("The Scalate boot class name.")

  // Copied from xsbt-scalate-generate
  val scalateClasspaths = taskKey[ScalateClasspaths]("Obtain Scalate classpath.")

  val scalateAutoLibs = settingKey[Boolean]("Append core Scalate dependencies to project library-dependencies if true.")

}
