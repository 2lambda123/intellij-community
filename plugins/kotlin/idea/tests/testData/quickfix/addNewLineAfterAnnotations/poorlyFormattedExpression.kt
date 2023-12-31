// "Add new line after annotations" "true"

@Target(AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
annotation class Ann

fun foo(y: IntArray) {
    // Currently it calls reformatting of base expression, but it seems to be a minor issue
    @Ann y [ 0 + 9 *   4]<caret> = y[y [1]]
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.AddNewLineAfterAnnotationsFix