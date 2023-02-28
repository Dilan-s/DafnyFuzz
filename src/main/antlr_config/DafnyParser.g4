parser grammar DafnyParser;

options {
  tokenVocab=DafnyLexer
}
Stmt: (LABEL LabelName COLON)? NonLabeledStmt

# Assert,
NonLabeledStmt: BlockStmt
  | ReturnStmt
  | VarDeclStmt
  | IfStmt
  | WhileStmt
  | UpdateStmt;


BlockStmt: OPEN_SCOPE Stmt* CLOSE_SCOPE ;

UpdateStmt: Lhs ((COMMA Lhs)* (ASSIGN Rhs (COMMA Rhs)*))? SEMI_COLON ;

VarDeclStmt: VAR IdentTypeOptional (COMMA IdentTypeOptional)* (ASSIGN Rhs (COMMA Rhs)*)? SEMI_COLON ;

IfStmt: IF (AlternativeBlock | (BindingGuard | Guard) BlockStmt (ELSE (IfStmt | BlockStmt))?) ;
AlternativeBlock: AlternativeBlockCase* | OPEN_SCOPE AlternativeBlockCase* CLOSE_SCOPE ;
AlternativeBlockCase: CASE (BindingGuard | Expression) RIGHT_ARROW Stmt

# WhileStmt: WHILE (LoopSpec AlternativeBlock | Guard LoopSpec BlockStmt?)
# LoopSpec:


IdentTypeOptional: IDENT (COLON Type)?

ReturnStmt: RETURN (Rhs (COMMA Rhs)*)? ;

LabelName: IDENT ;

BindingGuard: UNUSED;
Guard: UNUSED;
