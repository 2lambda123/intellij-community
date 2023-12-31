// "Use fully qualified call" "true"

package one.two.three

object Test1 {
    fun <T> foo(f: () -> T): T = f()
    fun bar(): Int = 0

    object Scope {
        fun bar(x: Int = 0): String = ""

        fun test() {
            val result = foo(::<caret>bar)
        }
    }
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.UseFullyQualifiedCallFix