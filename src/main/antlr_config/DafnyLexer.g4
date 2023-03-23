lexer grammar DafnyLexer;

RETURNS: 'returns';
MAIN: 'Main';
METHOD: 'method';

IFF: '<==>';
IMPLIES: '==>';
REVERSE_IMPLIES: '<==';
CONJUNCT: '&&';
DISJUNCT: '||';
MINUS: '-';
PLUS: '+';
EQUALITY: '==';
DISEQUALITY: '!=';
LTE: '<=';
GTE: '>=';
IN: 'in';
NOT_IN: '!in';
DISJOINT: '!!';
SHIFT_LEFT: '<<';
SHIFT_RIGHT: '>>';
MULT: '*';
DIV: '/';
MOD: '%';

SPREAD: '..';

CARDINALITY: '|';

NEW: 'new';
TRUE: 'true';
FALSE: 'false';

SEMICOLON: ';';
VAR: 'var';
EQUALS: '=';

IF: 'if';
THEN: 'then';
ELSE: 'else';

PRINT: 'print';
RETURN: 'return';

ARRAY: 'array';
SET: 'set';
MULTISET: 'multiset';
SEQ: 'seq';
BOOL: 'bool';
CHAR: 'char';
INT: 'int';
REAL: 'real';

LANGLE: '<';
RANGLE: '>';

COLON: ':';

LBRACKET: '(';
RBRACKET: ')';

COMMA: ',';

LCURLY: '{';
RCURLY: '}';

LSQUARE: '[';
RSQUARE: ']';

WS: [ \t\n\r]+ -> skip ;

SINGLE_QUOTE: '\'';
DOUBLE_QUOTE: '"';

NAME: LOWERCASE (LOWERCASE | UPPERCASE | UNDERSCORE | DIGIT)*;

LOWERCASE: 'a'..'z';
UPPERCASE: 'A'..'Z';
UNDERSCORE: '_';

HEX: '0x';
DECIMAL_POINT: '.';

DIGIT: '0'..'9';
BIN_DIGIT: '0' | '1' ;
OCT_DIGIT: '0'..'7' ;
HEX_DIGIT: '0'..'9' | 'A'..'F' ;

CHARACTER: ~['"\\] | ESCAPE_CHAR ;

fragment ESCAPE_CHAR: ESCAPE_BACKSLASH (ESCAPE_ZERO | ESCAPE_BACKSPACE | ESCAPE_TAB | ESCAPE_NEWLINE | ESCAPE_FORMFEED
| ESCAPE_CARRIAGERETURN | DOUBLE_QUOTE | SINGLE_QUOTE | ESCAPE_BACKSLASH) ;
fragment ESCAPE_ZERO : '0' ;
fragment ESCAPE_BACKSPACE : 'b' ;
fragment ESCAPE_TAB : 't' ;
fragment ESCAPE_NEWLINE : 'n' ;
fragment ESCAPE_FORMFEED : 'f' ;
fragment ESCAPE_CARRIAGERETURN : 'r' ;
fragment ESCAPE_BACKSLASH : '\\' ;


ERROR: .;