// "Specify override for 'size: Int' explicitly" "true"
// WITH_STDLIB

import java.util.*

class <caret>B(private val f: MutableList<String>): ArrayList<String>(), MutableList<String> by f {
    override fun isEmpty(): Boolean {
        return f.isEmpty()
    }
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.SpecifyOverrideExplicitlyFix