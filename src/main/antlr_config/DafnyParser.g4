parser grammar DafnyParser;

options {
  tokenVocab=DafnyLexer
}

prog: EOF
;

method: METHOD METHODIDENT LPAREN param_list RPAREN RETURNS LPAREN param_list RPAREN LCURLY stat RCURLY
;

param_list: (variable (COMMA variable)*)?
;

stat: (returnStat
| printStat
| ifElseStat
| assignmentStat
| stat stat) SEMICOLON
;

returnStat: RETURN (expression (COMMA expression)*)?
;

printStat: PRINT expression (COMMA expression)*
;

ifElseStat: IF expression (LCURLY)? stat (RCURLY)? elseStat?
;

elseStat: ELSE (LCURLY)? stat (RCURLY)?
;

assignStat: VAR variable (COMMA variable)* ASSIGN expression (COMMA expression)*
;

expression: (MINUS | PLUS)? INT_LITER
| BOOL_LITER
| CHAR_LITER
| STRING_LITER
| ident
| LPAREN expr RPAREN
| ifElseExpression
| operatorExpression
| callExpression
;

ifElseExpression: IF expression THEN expression ELSE expression
;

operatorExpression: expr binary_operP1 expr
| expr binary_operP2 expr
| expr binary_operP3 expr
| expr binary_operP4 expr
| expr binary_operP5 expr
| expr binary_operP6 expr
| expr binary_operP7 expr
| expr binary_operP8 expr
// | expr binary_operP9 expr
// | expr binary_operP10 expr
;

binary_operP1: IFF
;

binary_operP2: IMPLIES | REVERSEIMPLIES
;

binary_operP3: CONJUNCT | DISJUNCT
;

binary_operP4: EQUAL | NOTEQUAL | LESS | LESSEQUAL | GREATER | GREATEREQUAL
;

binary_operP5: LSHIFT | RSHIFT
;

binary_operP6: PLUS | MINUS
;

binary_operP7: MUL | DIV | MOD
;

binary_operP8: AND | OR | XOR;
;

callExpression: METHODIDENT LPAREN expression* RPAREN
;





variable: IDENT TYPEINDICATOR type
;