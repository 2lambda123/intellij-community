// IGNORE_K2
// NAME_COUNT_TO_USE_STAR_IMPORT: 2
package ppp

import foo.HashMap
import foo.ArrayList
import java.io.File
import java.io.PrintStream
import dependency.*

fun foo(c: C) {
    val v = HashMap<File, ArrayList<Int>>()
    "".extFun()
    "".extFun(1)
}
