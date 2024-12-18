grammar Ligma;

// === LEXER RULES ===

NUMBER: [0-9]+;                       // Matches integers
FLOAT: [0-9]+([.][0-9]*)?|[.][0-9]+;  // Matches floating-point numbers
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;   // Matches identifiers
WHITESPACE: [ \t\r\n]+ -> skip;       // Matches whitespace and skips it

// === PARSER RULES ===

program
    : statement+ EOF
    ;

statement
    : variableDefinition
    | constantDefinition
    | assignment
    | expression
    | ifStatement
    | whileStatement
    | procedureDefinition
    | procedureCall
    ;

variableDefinition
    : 'int' IDENTIFIER '=' expression ';'
    ;

constantDefinition
    : 'const' IDENTIFIER '=' NUMBER ';'
    ;

assignment
    : IDENTIFIER '=' expression ';'
    ;

ifStatement
    : 'if' '(' expression ')' '{' statement+ '}'
    ;

whileStatement
    : 'while' '(' expression ')' '{' statement+ '}'
    ;

procedureDefinition
    : 'func' IDENTIFIER '(' parameterList? ')' '{' statement+ '}'
    ;

procedureCall
    : IDENTIFIER '(' argumentList? ')' ';'
    ;

parameterList
    : parameter (',' parameter)*
    ;

parameter
    : dataType IDENTIFIER
    ;

dataType
    : 'int'
    ;

argumentList
    : expression (',' expression)*
    ;

// Expression with proper operator precedence
expression
    : expression '^' expression
    | '-' expression
    | '+' expression
    | '!' expression
    | expression ('*' | '/') expression
    | expression ('+' | '-') expression
    | expression ('&&' | '||') expression
    | expression ('==' | '!=' | '<' | '<=' | '>' | '>=') expression
    | '(' expression ')'
    | IDENTIFIER
    | NUMBER
    ;
