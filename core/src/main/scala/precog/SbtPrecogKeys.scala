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

import sbt._
import sbt.librarymanagement.ModuleFilter

import scala.{Boolean, Unit}

trait SbtPrecogKeys {

  lazy val managedVersions = settingKey[ManagedVersions](
    "The trickle-managed versions repository")

  lazy val exportSecretsForActions = taskKey[Unit](
    "Uses the GitHub Actions echo syntax and the openssl command to decrypt and export all configured secrets")

  lazy val decryptSecret = inputKey[Unit](
    "Delegates to openssl to decrypt a given file that was encrypted using $ENCRYPTION_PASSWORD. The name should correspond to the *decrypted* file, while the encrypted file is suffixed with `.enc`. Paths are relative to the base of the project")

  lazy val encryptSecret = inputKey[Unit](
    "Delegates to openssl to encrypt a given file using $ENCRYPTION_PASSWORD. The name should correspond to the *decrypted* file, while the encrypted file is suffixed with `.enc`. Paths are relative to the base of the project")

  lazy val secrets = settingKey[Seq[File]](
    "The set of all encrypted yaml files whos values are to be decrypted and exported")

  lazy val checkLocalEvictions = taskKey[Unit](
    "Checks for the existence of local evictions in the build and fails if they are found")

  lazy val transferPublishAndTagResources = taskKey[Unit](
    "Transfers publishAndTag script and associated resources")

  lazy val transferCommonResources = taskKey[Unit](
    "Transfers common resources not used in publication")

  lazy val scalacStrictMode = settingKey[Boolean](
    "Include stricter warnings and WartRemover settings")

  lazy val checkHeaders = taskKey[Unit]("Fail the build if createHeaders is not up-to-date")

  lazy val publishAsOSSProject = settingKey[Boolean](
    "Determines if project should be released publicly both to bintray and maven or only to a private bintray repository")

  lazy val synchronizeWithSonatypeStaging = taskKey[Unit]("Synchronize artifacts published on bintray sonatype staging repository")
  lazy val releaseToMavenCentral = taskKey[Unit]("Close the sonatype staging repository")
  lazy val performMavenCentralSync = settingKey[Boolean]("If true, then project will be sync'd from maven-public to Maven Central")

  /* Unsafe eviction check */
  lazy val unsafeEvictionsConf = settingKey[Seq[(ModuleFilter, VersionNumberCompatibility)]]("List of evictions deemed unsafe")
  lazy val unsafeEvictionsFatal = settingKey[Boolean]("Unsafe evictions are fatal if true")
  lazy val unsafeEvictionsCheck = taskKey[UpdateReport]("Resolves and optionally retrieves dependencies, producing a report whilst checking for unsafe evictions.")

}

object SbtPrecogKeys extends SbtPrecogKeys
