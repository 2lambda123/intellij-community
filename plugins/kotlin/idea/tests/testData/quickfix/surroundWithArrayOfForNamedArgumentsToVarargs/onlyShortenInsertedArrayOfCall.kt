// "Surround with *arrayOf(...)" "true"

package foo.bar

class A

fun foo(vararg a: A) {}

fun test() {
    foo(a = <caret>foo.bar.A())
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.SurroundWithArrayOfWithSpreadOperatorInFunctionFix