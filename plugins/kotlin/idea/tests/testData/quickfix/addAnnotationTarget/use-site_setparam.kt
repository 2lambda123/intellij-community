// "Add annotation target" "true"
@Target
annotation class SetParamAnn

class Bar {
    <caret>@setparam:SetParamAnn
    var foo = 1
        set(value) {}
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.AddAnnotationTargetFix