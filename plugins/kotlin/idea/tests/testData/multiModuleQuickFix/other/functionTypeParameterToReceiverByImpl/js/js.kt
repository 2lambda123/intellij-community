// "Convert '(Int) -> Int' to 'Int.() -> Int'" "true"
// IGNORE_K2

actual fun foo(n: Int, action: (<caret>Int) -> Int) = action(n)

fun test1() {
    foo(1) { n -> n + 1 }
}