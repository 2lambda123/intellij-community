// WITH_STDLIB
package my.simple.name

fun main() {
    val a = kotlin.Int.MAX_VALUE
    val b = kotlin<caret>.Int.Companion.MAX_VALUE
    val c = kotlin.Int.Companion::MAX_VALUE
}

// IGNORE_K2