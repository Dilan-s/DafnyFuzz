lexer grammar DafnyLexer;

LABEL: 'label' ;
COLON: ':' ;

SEMICOLON: ';' ;
OPEN_SCOPE: '{' ;
CLOSE_SCOPE: '}' ;

COMMA: ',' ;
ASSIGN: ':=' ;
RETURN: 'return' ;
VAR: 'var' ;
IF: 'if' ;
CASE: "case"
RIGHT_ARROW: "=>"
WHILE: "while"

IDENT: (LOWERCASE | UPPERCASE) (UNDERSCORE | LOWERCASE | UPPERCASE | DIGIT)* ;
fragment LOWERCASE : 'a'..'z' ;
fragment UPPERCASE : 'A'..'Z' ;
fragment UNDERSCORE: '_' ;
fragment DIGIT: '0'..'9' ;

framgent LETTER = LOWERCASE | UPPERCASE ;



fragment BIN_DIGIT: '0' | '1' ;
fragment OCT_DIGIT: '0'..'7' ;
fragment HEX_DIGIT: '0'..'9' | 'A'..'F' ;


UNUSED: .* ;