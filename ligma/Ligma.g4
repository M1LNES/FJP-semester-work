grammar Ligma;

// === LEXER RULES ===

// === KEYWORDS ===
CONST: 'const';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
FUNCTION: 'func';

// === DATA TYPES ===
BOOLEAN: 'boolean';
INT: 'int';

// === LITERALS AND IDENTIFIERS ===
TRUE: 'true';
FALSE: 'false';
NUMBER: [0-9]+;
FLOAT: [0-9]+([.][0-9]*)?|[.][0-9]+;
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
COMMENT: '//' ~[\r\n]* -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
WHITESPACE: [ \t\r\n]+ -> skip;

// === BRACKETS ===
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';

// === DELIMITERS ===
SEMICOLON: ';';
COMMA: ',';

// === ASSIGNMENT OPERATORS ===
ASSIGN: '=';

// === ARITHMETIC OPERATORS ===
ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
POW: '^';

// === RELATIONAL OPERATORS ===
EQ: '==';
NEQ: '!=';
GT: '>';
LT: '<';
GTE: '>=';
LTE: '<=';

// === LOGICAL OPERATORS ===
AND: '&&';
OR: '||';
NOT: '!';

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
    | functionDefinition
    | functionCall
    ;

dataType
    : INT
    | BOOLEAN
    ;

variableDefinition
    : dataType IDENTIFIER ASSIGN expression SEMICOLON
    ;

constantDefinition
    : CONST IDENTIFIER ASSIGN NUMBER SEMICOLON
    ;

assignment
    : IDENTIFIER ASSIGN expression SEMICOLON
    ;

ifStatement
    : IF LPAREN expression RPAREN LBRACE statement* RBRACE // (ELSE LBRACE statement* RBRACE)?
    ;

whileStatement
    : WHILE LPAREN expression RPAREN LBRACE statement* RBRACE
    ;

functionDefinition
    : FUNCTION IDENTIFIER LPAREN parameterList? RPAREN LBRACE statement* RBRACE
    ;

functionCall
    : IDENTIFIER LPAREN argumentList? RPAREN SEMICOLON
    ;

parameterList
    : parameter (COMMA parameter)*
    ;

parameter
    : dataType IDENTIFIER
    ;

argumentList
    : expression (COMMA expression)*
    ;

// Expression with proper operator precedence
// TODO: add support for function calls
expression
    : expression POW expression                               # powerExpression
    | SUB expression                                          # unaryMinusExpression
    | ADD expression                                          # unaryPlusExpression
    | NOT expression                                          # notExpression
    | expression (MUL | DIV) expression                       # multiplicativeExpression
    | expression (ADD | SUB) expression                       # additiveExpression
    | expression (EQ | NEQ | GT | LT | GTE | LTE) expression  # comparisonExpression
    | expression (AND | OR) expression                        # logicalExpression
    | LPAREN expression RPAREN                                # parenthesizedExpression
    | IDENTIFIER                                              # identifierExpression
    | NUMBER                                                  # numberExpression
    | (TRUE | FALSE)                                          # booleanExpression
    ;
