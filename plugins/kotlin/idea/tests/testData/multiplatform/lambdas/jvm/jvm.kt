package sample

actual class <!LINE_MARKER("descr='Has expects in common module'")!>A<!> {
    fun foo() {}
}

fun test() {
    useA {
        foo()
    }

    anotherUseA {
        <!UNRESOLVED_REFERENCE!>foo<!>()
    }

    anotherUseA {
        it.foo()
    }

    anotherUseA { a ->
        a.foo()
    }
}
