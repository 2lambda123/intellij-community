// IGNORE_K1
package foo.bar.baz

class AA {
    companion object
}

fun AA.Companion.foo() {}

fun test() {
    A<caret>A::foo // FE1.0 won't resolve this
}

// REF: companion object of (foo.bar.baz).AA
