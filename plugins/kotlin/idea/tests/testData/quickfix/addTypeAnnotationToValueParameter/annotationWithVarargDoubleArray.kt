// "Add type 'Double' to parameter 'value'" "true"

class CollectionDefault(vararg val value = doubleArrayOf(1.0, 2.2)<caret>)
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.AddTypeAnnotationToValueParameterFix