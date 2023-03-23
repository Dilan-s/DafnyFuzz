parser grammar Dafny;

options { tokenVocab=DafnyLexer; }

translation_unit: program;
program: method* main method* EOF;

main: method;
method: methodDecl LCURLY stat* RCURLY;

methodDecl: METHOD (NAME | MAIN) LBRACKET methodArgs RBRACKET RETURNS LBRACKET returnParams RBRACKET;

stat: concreteStat SEMICOLON stat?;

concreteStat: printStat | returnStat | ifElseStat | assignStat;

printStat: PRINT exprList;
returnStat: RETURN exprList;
ifElseStat: IF LBRACKET expr RBRACKET LCURLY stat* RCURLY (ELSE LCURLY stat* RCURLY)?;
assignStat: VAR? variableDecl COLON EQUALS expr (COMMA variableDecl)*;

expr: LBRACKET expr RBRACKET | literal | callExpression | ifElseExpr | indexExpr | reassignSeqExpr | subsequenceExpr | NAME |
  expr op7 expr | expr op6 expr | expr op5 expr | expr op4 expr | expr op3 expr | expr op2 expr | expr op1 expr | sizeExpr;

exprList: (expr (COMMA expr)*)?;

sizeExpr: CARDINALITY expr CARDINALITY;

literal: boolLiteral | charLiteral | intLiteral | realLiteral | stringLiteral | arrayLiteral | setLiteral | multisetLiteral | seqLiteral ;
boolLiteral: TRUE | FALSE ;
charLiteral: SINGLE_QUOTE CHARACTER SINGLE_QUOTE ;
stringLiteral: DOUBLE_QUOTE CHARACTER* DOUBLE_QUOTE ;
intLiteral: (MINUS | PLUS)? (DIGIT+ | HEX HEX_DIGIT+) ;
realLiteral: DIGIT+ (DECIMAL_POINT DIGIT+)?;
arrayLiteral: NEW type LSQUARE RSQUARE LSQUARE exprList RSQUARE;
setLiteral: LCURLY exprList RCURLY;
multisetLiteral: MULTISET LBRACKET expr RBRACKET;
seqLiteral: LSQUARE exprList RSQUARE;

callExpression: NAME LBRACKET exprList RBRACKET;

ifElseExpr: IF LBRACKET expr RBRACKET THEN expr ELSE expr;

indexExpr: NAME LBRACKET expr RBRACKET;

op1: IFF;
op2: IMPLIES | REVERSE_IMPLIES;
op3: CONJUNCT | DISJUNCT;
op4: EQUALITY | DISEQUALITY | RANGLE | LANGLE | GTE | LTE | IN | NOT_IN | DISJOINT;
op5: SHIFT_LEFT | SHIFT_RIGHT;
op6: PLUS | MINUS ;
op7: MULT | DIV | MOD;

reassignSeqExpr: NAME LSQUARE NAME COLON EQUALS expr RSQUARE;
subsequenceExpr: NAME LSQUARE NAME SPREAD NAME RSQUARE;

methodArgs: (variableDecl (COMMA variableDecl)* )?;
returnParams: (variableDecl (COMMA variableDecl)* )?;

variableDecl: NAME (COLON type)?;

type: baseType | collectionType LANGLE type RANGLE;

collectionType: ARRAY | SET | MULTISET | SEQ;
baseType: BOOL | CHAR | INT | REAL;
