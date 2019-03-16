package com.kyleu.projectile.services.typescript

import better.files.File

case class ServiceParams(
    root: File,
    cache: File,
    path: String,
    sourcecode: String,
    depth: Int,
    parseRefs: Boolean,
    forceCompile: Boolean,
    encountered: Set[String],
    messages: Seq[String]
) {
  val file = root / path
}