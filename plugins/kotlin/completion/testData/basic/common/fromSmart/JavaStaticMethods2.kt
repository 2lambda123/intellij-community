fun foo(): java.util.Calendar = Ca<caret>

// IGNORE_K2
// EXIST_JAVA_ONLY: { lookupString:"getInstance", itemText:"Calendar.getInstance", tailText:"() (java.util)", typeText:"Calendar!" }
// EXIST_JAVA_ONLY: { lookupString:"getInstance", itemText:"Calendar.getInstance", tailText:"(TimeZone!) (java.util)", typeText:"Calendar!" }
// EXIST_JAVA_ONLY: { lookupString:"getInstance", itemText:"Calendar.getInstance", tailText:"(TimeZone!, Locale!) (java.util)", typeText:"Calendar!" }
