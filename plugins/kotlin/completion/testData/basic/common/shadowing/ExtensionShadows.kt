// FIR_COMPARISON
// FIR_IDENTICAL
class Shadow {
    fun shade() {}
}

fun <X> Shadow.shade() {}

fun context() {
    Shadow().sha<caret>
}

// EXIST: { lookupString: "shade", itemText: "shade", tailText: "()", typeText: "Unit", icon: "Method"}
// EXIST: { lookupString: "shade", itemText: "shade", tailText: "() for Shadow in <root>", typeText: "Unit", icon: "Function"}
// NOTHING_ELSE