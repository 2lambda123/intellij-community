// "Create type parameter 'X' in function 'foo'" "true"
// ERROR: Unresolved reference: _
class A<T : List<T>>

interface I : List<I>

fun foo(x: A<<caret>X>) {

}

fun test() {
    foo(A())
    foo(A<I>())
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.createFromUsage.createTypeParameter.CreateTypeParameterFromUsageFix