// ERROR: Null can not be a value of a non-null type Iterator<String>
// ERROR: Null can not be a value of a non-null type Iterator<String>
package demo

internal class Test : Iterable<String?> {
    override fun iterator(): Iterator<String> {
        return null
    }

    fun push(i: Iterator<String>): Iterator<String> {
        val j = i
        return j
    }
}

internal class FullTest : Iterable<String?> {
    override fun iterator(): Iterator<String> {
        return null
    }

    fun push(i: Iterator<String>): Iterator<String> {
        val j = i
        return j
    }
}
