// "Replace with 'newFun<kotlin.Int>()'" "true"

@Deprecated("", ReplaceWith("newFun<T>()"))
fun <T> oldFun() {
    newFun<T>()
}

fun <T> newFun(){}

fun foo() {
    <caret>oldFun<kotlin.Int>()
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.replaceWith.DeprecatedSymbolUsageFix