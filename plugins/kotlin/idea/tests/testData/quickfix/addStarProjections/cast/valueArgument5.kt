// "Change type arguments to <*, *>" "true"
fun test(a: Any) {
    foo(a as Map<Int, Boolean><caret>)
}

fun <T> foo(map: Map<T, *>) {}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.ChangeToStarProjectionFix