package kmmApplication

expect class <!LINE_MARKER("descr='Has actuals in [project.jvmMain, project.iosMain] modules'; targets=[(text=project.jvmMain); (text=project.iosMain)]")!>CommonMainExpect<!> {
    fun <!LINE_MARKER("descr='Has actuals in [project.jvmMain, project.iosMain] modules'; targets=[(text=project.jvmMain); (text=project.iosMain)]")!>commonMainApi<!>()
}

fun consumeCommonMainExpect(e: CommonMainExpect?) { }
fun produceCommonMainExpect(): CommonMainExpect? = null

fun stdlibExpectLikeClass(): kotlin.RuntimeException? = null

internal fun commonMainInternal() { }
