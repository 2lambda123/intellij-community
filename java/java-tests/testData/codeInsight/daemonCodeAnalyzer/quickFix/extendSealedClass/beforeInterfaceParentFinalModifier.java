// "Make 'Child' extend 'Parent'|->final" "false"
sealed interface Parent permits C<caret>hild {}

interface Child {}