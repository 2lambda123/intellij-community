// "Change type arguments to <*>" "true"
fun isStringList(list : Any?) = list is (List<<caret>String>)
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.ChangeToStarProjectionFix