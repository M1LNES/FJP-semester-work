grammar Ligma;

// === LEXER RULES ===

// === KEYWORDS ===
CONST: 'const';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
FUNCTION: 'func';
RETURN: 'return';

// === DATA TYPES ===
INT: 'int';
FLOAT: 'float';
BOOLEAN: 'boolean';

// === LITERALS AND IDENTIFIERS ===
BOOLEAN_LITERAL: 'true' | 'false';
INTEGER_LITERAL: [0-9]+;
FLOAT_LITERAL: [0-9]+([.][0-9]*)?|[.][0-9]+;
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
MOD: '%';
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
    : (statement | functionDefinition)* EOF
    ;

statement
    : variableDefinition
    | constantDefinition
    | assignment
    | ifStatement
    | whileStatement
    | functionCall SEMICOLON
    ;

functionDefinition
    : FUNCTION dataType IDENTIFIER LPAREN parameterList? RPAREN LBRACE functionBody RBRACE
    ;

functionBody
    : statement* RETURN expression SEMICOLON
    ;

parameterList
    : parameter (COMMA parameter)*
    ;

parameter
    : dataType IDENTIFIER
    ;

dataType
    : INT
    | FLOAT
    | BOOLEAN
    ;

literal
    : INTEGER_LITERAL
    | FLOAT_LITERAL
    | BOOLEAN_LITERAL
    ;

variableDefinition
    : dataType IDENTIFIER ASSIGN expression SEMICOLON
    ;

constantDefinition
    : CONST variableDefinition
    ;

assignment
    : IDENTIFIER ASSIGN expression SEMICOLON
    ;

ifStatement
    : IF LPAREN expression RPAREN LBRACE ifElseBody RBRACE (ELSE LBRACE ifElseBody RBRACE)?
    ;

ifElseBody
    : statement*
    ;

whileStatement
    : WHILE LPAREN expression RPAREN LBRACE statement* RBRACE
    ;

functionCall
    : IDENTIFIER LPAREN argumentList? RPAREN
    ;

argumentList
    : expression (COMMA expression)*
    ;

// Expression with proper operator precedence
expression
    : expression POW expression                                  # powerExpression
    | SUB expression                                             # unaryMinusExpression
    | ADD expression                                             # unaryPlusExpression
    | NOT expression                                             # notExpression
    | expression op=(MUL | DIV | MOD) expression                 # multiplicativeExpression
    | expression op=(ADD | SUB) expression                       # additiveExpression
    | expression op=(EQ | NEQ | GT | LT | GTE | LTE) expression  # comparisonExpression
    | expression op=(AND | OR) expression                        # logicalExpression
    | LPAREN expression RPAREN                                   # parenthesizedExpression
    | IDENTIFIER                                                 # identifierExpression
    | literal                                                    # literalExpression
    | functionCall                                               # functionCallExpression
    ;
