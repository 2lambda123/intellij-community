// "Create member property 'A.Companion.foo'" "true"
// ERROR: Property must be initialized or be abstract

class A<T>(val n: T) {
    companion object {

    }
}

fun test() {
    val a: Int = A.<caret>foo
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.createFromUsage.createCallable.CreateCallableFromUsageFix