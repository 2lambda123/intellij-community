// "Replace with 'test.Bar'" "true"

package test

@Deprecated("Replace with bar", ReplaceWith("test.Bar"))
annotation class Foo

annotation class Bar

@Foo<caret> class C {}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.replaceWith.DeprecatedSymbolUsageFix