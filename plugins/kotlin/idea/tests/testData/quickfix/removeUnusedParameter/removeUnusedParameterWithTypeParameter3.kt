// "Remove parameter 'x'" "true"
fun <X, Y> foo(<caret>x: X, y: Y) {}

fun test() {
    foo(x = 1, y = 2)
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.RemoveUnusedFunctionParameterFix