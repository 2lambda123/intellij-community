// "Create member property 'A.foo'" "true"
// ERROR: Property must be initialized or be abstract

class A

fun test() {
    println("a = ${A().<caret>foo}")
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.createFromUsage.createCallable.CreateCallableFromUsageFix