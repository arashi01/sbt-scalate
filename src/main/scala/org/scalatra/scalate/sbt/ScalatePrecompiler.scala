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

import SbtScalate.autoImport._
import sbt.Keys._
import sbt._

import collection.mutable.ArrayBuffer
import scala.language.reflectiveCalls


object ScalatePrecompiler extends AutoPlugin {


  override val requires: Plugins = SbtScalate

  override val trigger: PluginTrigger = noTrigger

  override def projectSettings: Seq[Def.Setting[_]] = precompilerSettings

  val autoImport = Import

  import autoImport._

  def precompilerBaseSettings: Seq[Setting[_]] = Seq(
    sourceDirectories in scalate := Seq(sourceDirectory.value / "scalate"),
    // scalateBindings := Nil,
    scalate <<= scalatePreCompileTask,
    sourceGenerators <+= scalate
  )

  def precompilerSettings: Seq[Setting[_]] = inConfig(Compile)(precompilerBaseSettings) ++
    inConfig(Test)(precompilerBaseSettings) ++ Seq(
    scalatePackagePrefix := "",
    scalateTemplateImports := Seq("_root_.org.fusesource.scalate.support.TemplateConversions._",
      "_root_.org.fusesource.scalate.util.Measurements._"),
    scalateEscapeMarkup := true
  )

  def scalatePreCompileTask: Def.Initialize[Task[Seq[File]]] = Def.task {
    precompile(
      (managedClasspath in scalateClasspaths).value map (_.data),
      javaHome.value,
      (sources in scalate).value,
      (sourceDirectories in scalate).value,
      scalateEscapeMarkup.value,
      scalateTemplateImports.value,
      scalateBootClass.value,
      scalatePackagePrefix.value,
      (target in scalate).value,
      streams.value
    )
  }

  def precompile(classpath: Seq[File],
                 javaHome: Option[File],
                 sources: Seq[File],
                 sourceDirectories: Seq[File],
                 escapeMarkup: Boolean,
                 templateImports: Seq[String],
                 bootClassName: Option[String],
                 packagePrefix: String,
                 targetDir: File,
                 streams: TaskStreams): Seq[File] = Util.runWithPathLoader(classpath, streams.log) {
    targetDir.mkdirs()
    streams.cacheDirectory.mkdirs()


    sources foreach { src ⇒

      if (!src.isDirectory) {
        val origDir = sourceDirectories.find(p ⇒ src.toPath.startsWith(p.toPath)).get
        val dest = {
          def rel = origDir.relativize(src).map(_.getPath.replaceAll("[.]", "_")).get
          targetDir / s"$rel.scala"
        }

        if (src.newerThan(dest)) {
          try {
            def opts = Util.forkOpts(javaHome, classpath)
            def args = {
              val buffer = ArrayBuffer(
                "generate-scala",
                s"--working-directory=${streams.cacheDirectory.getAbsolutePath}",
                s"--escape-markup=${escapeMarkup.toString}"
              )
              bootClassName foreach (b ⇒ buffer += s"--boot-class=$b")
              if (packagePrefix.nonEmpty) buffer += s"--package-prefix=$packagePrefix"
              templateImports.foreach(i ⇒ buffer += s"--template-imports=$i")
              buffer ++ Array(src.getAbsolutePath, dest.toString)
            }
            Fork.java(opts, args) // FIXME: Build successful even if pre-compilation failed.
          } catch {
            case e: Throwable ⇒ throw new RuntimeException(s"Error during Scala source generation for $src.", e)
          }
        }
      }
    }
    Util.collectFiles(targetDir, "**.scala")


  }

  object Import {
    val scalateEscapeMarkup = settingKey[Boolean]("Determines whether sensitive markup characters are escaped for HTML/XML elements.")
    val scalateTemplateImports = settingKey[Seq[String]]("Names of members to be imported by generated Scala sources.")
    val scalatePackagePrefix = settingKey[String]("Package prefix for generated Scala sources.")
  }

}
