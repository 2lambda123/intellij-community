// "Replace with 'NewClass'" "true"
package ppp

@Deprecated("", ReplaceWith("NewClass"))
class OldClass<T, V>

class NewClass

fun foo() {
    <caret>OldClass<Int, String>()
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.replaceWith.DeprecatedSymbolUsageFix