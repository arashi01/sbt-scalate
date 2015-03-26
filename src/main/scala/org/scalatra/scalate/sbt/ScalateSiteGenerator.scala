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

import java.io.File

import SbtScalate.autoImport._
import sbt.Keys._
import sbt._

import collection.mutable.ArrayBuffer
import scala.language.reflectiveCalls


object ScalateSiteGenerator extends AutoPlugin {


  override val requires: Plugins = SbtScalate

  override val trigger: PluginTrigger = noTrigger

  override def projectSettings: Seq[Def.Setting[_]] = sitegenSettings

  val autoImport = Import

  import autoImport._

  def sitegenSettings = Seq(
    scalateAppDirectory := baseDirectory.value / "app",
    sourceDirectories in Scalate := Seq(scalateAppDirectory.value),
    target in scalate := target.value / "generated-site",
    scalate <<= siteGenTask,
    resourceGenerators in Compile <+= scalate
  )

  def siteGenTask: Def.Initialize[Task[Seq[sbt.File]]] = Def.task {
    generateSite(
      (managedClasspath in(Compile, scalateClasspaths)).value map (_.data),
      javaHome.value,
      scalateAppDirectory.value,
      scalateBootClass.value,
      (target in scalate).value,
      streams.value
    )
  }

  def generateSite(classpath: Seq[File],
                   javaHome: Option[File],
                   sourceDir: File,
                   bootClassName: Option[String],
                   targetDir: File,
                   streams: TaskStreams) = Util.runWithPathLoader(classpath, streams.log) {
    targetDir.mkdirs()
    streams.cacheDirectory.mkdirs()

    def args = {
      val buffer = ArrayBuffer("generate-site",
        s"--working-directory=${streams.cacheDirectory.getAbsolutePath}"
      )
      bootClassName foreach (b ⇒ buffer += s"--boot-class=$b")
      buffer ++ Array(sourceDir.getAbsolutePath, targetDir.getAbsolutePath)
    }

    Fork.java(Util.forkOpts(javaHome, classpath), args) //FIXME: Build successful even if pre-compilation failed.

    Util.collectFiles(targetDir, AllPassFilter)
  }

  object Import {
    val scalateAppDirectory = settingKey[File]("The directory in which Scalate template files are located.")
  }

}
