// "Create class 'A!u00A0'" "true"
fun test() {
    val t = <caret>`A!u00A0`(1)
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.createFromUsage.createClass.CreateClassFromUsageFix