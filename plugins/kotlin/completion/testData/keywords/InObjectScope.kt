// FIR_IDENTICAL
// FIR_COMPARISON
// COMPILER_ARGUMENTS: -XXLanguage:+SealedInterfaces -XXLanguage:+MultiPlatformProjects

object Test {
    <caret>
}

// EXIST:  abstract
// EXIST:  class
// EXIST:  enum class
// EXIST:  final
// EXIST:  fun
// EXIST:  internal
// EXIST:  object
// EXIST:  open
// EXIST:  override
// EXIST:  private
// EXIST:  public
// EXIST:  interface
// EXIST:  val
// EXIST:  var
// EXIST:  init
// EXIST:  operator
// EXIST:  infix
// EXIST:  sealed class
// EXIST:  sealed interface
// EXIST:  lateinit var
// EXIST:  data class
// EXIST:  inline
// EXIST:  value
// EXIST:  tailrec
// EXIST:  external
// EXIST:  annotation class
// EXIST:  const val
// EXIST:  suspend fun
// EXIST:  typealias
// EXIST:  expect
// EXIST:  actual
// EXIST: data object
// NOTHING_ELSE
