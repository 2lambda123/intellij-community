// "Add new line after annotations" "true"

@Target(AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
annotation class Ann

@Target(AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
annotation class Ann2(val x: String)

fun foo(y: Int) {
    var x = 1
    @Ann    @Ann2("") x<caret> += 2
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.AddNewLineAfterAnnotationsFix