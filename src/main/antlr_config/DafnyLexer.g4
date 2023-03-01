lexer grammar DafnyLexer;

METHOD: 'method'

BLOCK_COMMENT: '/*' (.)+? '*/' -> skip;
COMMENT: '//' ~[\n\r]*  EOL -> skip ;

LPAREN: '(' ;
RPAREN: ')' ;
LCURLY: '{' ;
RCURLY: '}' ;
COMMA: ',' ;

ASSIGN: ':=';
VAR: 'var';
TYPEINDICATOR: ':'
RETURN: 'return';
RETURNS: 'returns';


IF: 'if';
THEN: 'then';
ELSE: 'else';

INT: 'int' ;
BOOL: 'bool' ;
CHAR: 'char' ;
STRING: 'string' ;

PRINT: 'print' ;

SEMICOLON: ';' ;

IFF: '<==>';
IMPLIES: '==>'
REVERSEIMPLIES: '<=='
MINUS: '-' ;
MUL: '*' ;
DIV: '/' ;
MOD: '%' ;
PLUS: '+' ;
GREATER: '>';
GREATEREQUAL: '>=';
LESS: '<';
LESSEQUAL: '<=';
EQUAL: '==';
NOTEQUAL: '!=';
AND: '&' ;
CONJUNCT: '&&';
DISJUNCT: '||' ;
LSHIFT: '<<';
RSHIFT: '>>';
OR: '|' ;
XOR: '^';

WS: [ \t\n\r]+ -> skip ;

INT_LITER: DIGIT+ | '0b' BIN_DIGIT+ | '0c' OCT_DIGIT+ | '0x' HEX_DIGIT+ ;

fragment DIGIT: '0'..'9' ;
fragment BIN_DIGIT: '0' | '1' ;
fragment OCT_DIGIT: '0'..'7' ;
fragment HEX_DIGIT: '0'..'9' | 'A'..'F' ;

BOOL_LITER: TRUE | FALSE ;
fragment TRUE: 'true';
fragment FALSE: 'false';

CHAR_LITER: SINGLE_QUOTE CHARACTER SINGLE_QUOTE ;
fragment SINGLE_QUOTE: '\'';

STRING_LITER: DOUBLE_QUOTE CHARACTER* DOUBLE_QUOTE ;
fragment DOUBLE_QUOTE: '"';

fragment CHARACTER: ~['"\\] | ESCAPE_CHAR ;
//escaped characters
fragment ESCAPE_CHAR: ESCAPE_BACKSLASH (ESCAPE_ZERO | ESCAPE_BACKSPACE | ESCAPE_TAB | ESCAPE_NEWLINE | ESCAPE_FORMFEED
| ESCAPE_CARRIAGERETURN | DOUBLE_QUOTE | SINGLE_QUOTE | ESCAPE_BACKSLASH) ;
fragment ESCAPE_ZERO : '0' ;
fragment ESCAPE_BACKSPACE : 'b' ;
fragment ESCAPE_TAB : 't' ;
fragment ESCAPE_NEWLINE : 'n' ;
fragment ESCAPE_FORMFEED : 'f' ;
fragment ESCAPE_CARRIAGERETURN : 'r' ;
fragment ESCAPE_BACKSLASH : '\\' ;

METHODIDENT: (LOWERCASE | UPPERCASE) (UNDERSCORE | LOWERCASE | UPPERCASE | DIGIT)* ;
IDENT: LOWERCASE (UNDERSCORE | LOWERCASE | UPPERCASE | DIGIT)* ;
fragment LOWERCASE : 'a'..'z' ;
fragment UPPERCASE : 'A'..'Z' ;
fragment UNDERSCORE: '_' ;

EOL: '\n' | '\r' ;

ERROR: .;