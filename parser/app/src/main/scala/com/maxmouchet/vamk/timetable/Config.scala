package com.maxmouchet.vamk.timetable

import java.io.File
import java.net.URL

case class Config(foo: Int = -1, out: File = new File("."), xyz: Boolean = false,
                  libName: String = "", maxCount: Int = -1, verbose: Boolean = false, debug: Boolean = false,
                  mode: String = "", urls: Seq[String] = Seq(), prettyprint: Boolean = false)