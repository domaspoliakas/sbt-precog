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

import java.nio.file.Path

import sbt.internal.util.codec.JValueFormats
import sbt.util.FileBasedStore
import sjsonnew.BasicJsonProtocol
import sjsonnew.shaded.scalajson.ast.unsafe.JField
import sjsonnew.shaded.scalajson.ast.unsafe.JObject
import sjsonnew.shaded.scalajson.ast.unsafe.JString
import sjsonnew.shaded.scalajson.ast.unsafe.JValue

final class ManagedVersions private (path: Path) extends BasicJsonProtocol with JValueFormats {

  private[this] lazy val store: FileBasedStore[JValue] =
    new FileBasedStore(path.toFile)

  def apply(key: String): String =
    get(key).getOrElse(sys.error(s"unable to find string -> string mapping for key '$key'"))

  def get(key: String): Option[String] = {
    safeRead() match {
      case JObject(values) =>
        values.find(_.field == key) match {
          case Some(JField(_, JString(value))) => Some(value)
          case _ => None
        }

      case _ =>
        sys.error(s"unable to parse managed versions store at $path")
    }
  }

  def update(key: String, version: String): Unit = {
    safeRead() match {
      case JObject(values) =>
        var i = 0
        var done = false
        while (i < values.length && !done) {
          if (values(i).field == key) {
            values(i) = JField(key, JString(version))
            done = true
          }

          i += 1
        }

        val values2 = if (!done) {
          val values2 = new Array[JField](values.length + 1)
          System.arraycopy(values, 0, values2, 0, values.length)
          values2(values.length) = JField(key, JString(version))

          values2
        } else {
          values
        }

        store.write(JObject(values2))

      case _ =>
        sys.error(s"unable to parse managed versions store at $path")
    }
  }

  private[this] def safeRead(): JValue = {
    try {
      store.read[JValue]()
    } catch {
      case _: sbt.internal.util.EmptyCacheError =>
        val back = JObject(Array[JField]())
        store.write(back)
        back
    }
  }

  override def toString: String = s"ManagedVersions($path)"
}

object ManagedVersions {
  def apply(path: Path): ManagedVersions =
    new ManagedVersions(path)
}
