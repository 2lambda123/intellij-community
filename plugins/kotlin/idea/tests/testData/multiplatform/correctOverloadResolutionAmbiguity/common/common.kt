// KT-34027
expect class <!LINE_MARKER("descr='Has actuals in js module'")!>A<!><T> {
    fun <!LINE_MARKER("descr='Has actuals in js module'")!>foo<!>(x: T)
}

fun bar(): A<String> = null!!
