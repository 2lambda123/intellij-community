// "Create extension function 'List<Int>.foo'" "true"

open class A

fun main(args: Array<String>) {
    val list = listOf(1, 2, 4, 5)
    list.<caret>foo { object : A() {} }
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.createFromUsage.createCallable.CreateExtensionCallableFromUsageFix