//FILE: a/a.kt
package bar

class A {
    fun a() {
        val a = 5
    }
}
// PRODUCED_CLASS_NAMES: bar.A

//FILE: b/a.kt
package foo

class A {
    fun b() {
        val c = 1
        val d = 3
        val g = ""
    }
}
// PRODUCED_CLASS_NAMES: foo.A