// "Remove parameter 'x'" "true"
fun <X, Y> bar(<caret>x: X, y: Y) where X : Number, Y : Number {}

fun test() {
    bar(1, 2)
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.RemoveUnusedFunctionParameterFix