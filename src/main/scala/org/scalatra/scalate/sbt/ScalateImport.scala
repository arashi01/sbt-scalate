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

/** Contains setting keys and values automatically imported to `.sbt` files. */
object ScalateImport {

  val Scalate: Configuration = config("scalate")
    .hide
    .extend(Compile)

  val scalate: TaskKey[Seq[File]] = taskKey[Seq[File]]("Process Scalate templates.")

  val scalateAutoLibs: SettingKey[Boolean] = settingKey[Boolean]("Append core Scalate dependencies to project library-dependencies if true.")

  val scalateBootClass: SettingKey[Option[String]] = settingKey[Option[String]]("The Scalate boot class name.")

  val scalateEscapeMarkup: SettingKey[Boolean] = settingKey[Boolean]("Determines whether sensitive markup characters are escaped for HTML/XML elements.")

  val scalateOrganisation: SettingKey[String] = settingKey[String]("Allows specifying a custom groupID for Scalate dependency resolution.")

  val scalatePackagePrefix: SettingKey[String] = settingKey[String]("Package prefix for generated Scala sources.")

  val scalateTemplateImports: SettingKey[Seq[String]] = settingKey[Seq[String]]("Names of members to be imported by generated Scala sources.")

  val scalateVersion: SettingKey[String] = settingKey[String]("The revision of Scalate used for compilation.")

}
