// ERROR: Unresolved reference: somethingElse
fun foo(o: Any) {
    if (o is String) {
        val l = o.length
    }
    somethingElse()
}