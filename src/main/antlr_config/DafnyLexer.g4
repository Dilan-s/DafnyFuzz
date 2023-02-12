
fragment letter: 'a'..'z' | 'a'..'z' ;

fragment digit: '0'..'9' ;
fragment hexdigit: '0'..'9' | 'a'..'f' | 'a'..'f' ;

fragment underscore: '_'
fragment digits: digit (underscore? digit)*
fragment hexdigits: '0x' hexdigit (underscore? hexdigit)*
fragment decimaldigits: digits '.' digits

fragment escape_char: escape_backslash (escape_zero | escape_backspace | escape_tab | escape_newline | escape_formfeed
| escape_carriagereturn | double_quote | single_quote | escape_backslash | 'u' hexdigit hexdigit hexdigit hexdigit | 'U{' hexdigit hexdigit*) '}') ;
fragment escape_zero : '0' ;
fragment escape_backspace : 'b' ;
fragment escape_tab : 't' ;
fragment escape_newline : 'n' ;
fragment escape_formfeed : 'f' ;
fragment escape_carriagereturn : 'r' ;
fragment escape_backslash : '\\' ;

