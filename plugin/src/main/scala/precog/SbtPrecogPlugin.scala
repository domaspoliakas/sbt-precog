/*
 * Copyright 2021 Precog Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package precog

import scala.collection.immutable.Seq

import sbt.Keys._
import sbt._

object SbtPrecogPlugin extends SbtPrecogBase {

  override def requires = super.requires

  object autoImport extends autoImport {

    lazy val noPublishSettings =
      Seq(publish := {}, publishLocal := {}, publishArtifact := false, skip in publish := true)
  }

  import autoImport._

  override def projectSettings =
    super.projectSettings ++
      addCommandAlias("releaseSnapshot", "; project /; reload; checkLocalEvictions; publish") ++
      Seq(sbtPlugin := true, publishMavenStyle := true)

  override def buildSettings =
    super.buildSettings ++
      Seq(
        secrets += file("credentials.yml.enc"),
        transferPublishAndTagResources := {
          transferToBaseDir("plugin", (ThisBuild / baseDirectory).value, "credentials.yml.enc")
          transferPublishAndTagResources.value
        }
      )

  protected val autoImporter = autoImport
}
