// "Replace deprecated '@Experimental' annotation with '@RequiresOptIn'" "true"
// COMPILER_ARGUMENTS: -Xopt-in=kotlin.RequiresOptIn
// WITH_STDLIB 1.7.0

@Experimental<caret>(Experimental.Level.ERROR)
annotation class Marker

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.migration.MigrateExperimentalToRequiresOptInFix