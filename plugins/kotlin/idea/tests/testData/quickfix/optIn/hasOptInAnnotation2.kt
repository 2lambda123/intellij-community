// "Opt in for 'A' on 'root'" "true"
// WITH_STDLIB
@RequiresOptIn
annotation class A

@A
fun f1() {}

@OptIn()
fun root() {
    <caret>f1()
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.OptInFixesFactory$HighPriorityUseOptInAnnotationFix