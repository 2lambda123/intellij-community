// "Create extension function 'A.foo'" "true"
// WITH_STDLIB
fun bar(b: Boolean) {

}

class A(val n: Int)

fun test() {
    with(A(1)) {
        bar(<caret>foo(n))
    }
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.createFromUsage.createCallable.CreateExtensionCallableFromUsageFix