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
import classpath.ClasspathUtilities


private[sbt] object Util {

  final val toolClassName = "org.fusesource.scalate.tool.ScalateMain"

  def forkOpts(javaHome: Option[File], cp: Seq[File], opts: String*): ForkOptions = ForkOptions(
    javaHome,
    runJVMOptions = Seq("-cp", cp.mkString(java.io.File.pathSeparator), Util.toolClassName) ++ opts
  )

  def runWithPathLoader[A](classpath: Seq[File], log: Logger)(f: ⇒ A): A = {
    val loader = Thread.currentThread.getContextClassLoader
    try {
      val forPath = ClasspathUtilities.toLoader(classpath)
      Thread.currentThread.setContextClassLoader(forPath)
      f
    } finally {
      Thread.currentThread.setContextClassLoader(loader)
    }
  }

  def collectFiles(target: File, filter: FileFilter): Seq[File] = {
    (target ** filter).get map (_.getAbsoluteFile) filter (_ != target)
  }

}
