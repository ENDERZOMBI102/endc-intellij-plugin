/** A simple language for use with this sample plugin.
 *  It's C-like but without semicolons. Symbol resolution semantics are
 *  C-like: resolve symbol in current scope. If not in this scope, ask
 *  enclosing scope to resolve (recurse up tree until no more scopes or found).
 *  Forward refs allowed for functions but not variables. Globals must
 *  appear first syntactically.
 *
 *  Generate the parser via "mvn compile" from root dir of project.
 */
grammar EndC;

/** The start rule must be whatever you would normally use, such as script
 *  or compilationUnit, etc...
 */
script
	:	vardef* function* statement* EOF
	;

function
	:	'XPORT'? 'DCLAR FUNC' ID '{' formal_args? '} <- ' type block
	;

formal_args : formal_arg (',' formal_arg)* ;

formal_arg : ID ':' type ;

type:	'InTgR'                                             # IntTypeSpec
	|	'StRiNg'                                            # StringTypeSpec
	|	'BoOlAn'											# BooleanTypeSpec
	|	'NoThInG'											# NothingTypeSpec
	;

block
	:  '[' (statement|vardef)* ']';

statement
	:	'CHCK IF' '{' expr '}' 'DO' '[' statement ']' ( 'LS DO' '[' statement ']' )?		# If
	|	ID '=' expr											# Assign
	|	ID '(' expr ')' '=' expr							# ElementAssign
	|	call_expr											# CallStatement
	|	'printto' '{' ID expr? '}'							# Print
	|	'GIV BACK' expr										# Return
	|	block				 								# BlockStatement
	;

vardef
	:	'DCLAR' 'CONSTANT' ID '=' expr						# DeclareConstant
	|	'DCLAR' 'VARIABL' ID '=' expr						# DeclareVariable
	;

expr
	:	expr operator expr									# Op
	|	'-' expr											# Negate
	|	'!' expr											# Not
	|	call_expr											# Call
	|	ID '(' expr ')'										# Index
	|	'{' expr '}'										# Parens
	|	primary												# Atom
	;

operator  : DIV|ADD|SUB|GT|GE|LT|LE|EQUAL_EQUAL|NOT_EQUAL|OR|AND|DOT ; // no implicit precedence

call_expr
	: 'CALL' ID '(' expr_list? ')' ;

expr_list : expr (',' expr)* ;

primary
	:	ID													# Identifier
	|	INT													# Integer
	|	FLOAT												# Float
	|	STRING												# String
	|	'[' expr_list ']'									# Vector
	|	'true'												# TrueLiteral
	|	'false'												# FalseLiteral
	;

LPAREN : '(' ;
RPAREN : ')' ;
COLON : ':' ;
COMMA : ',' ;
LBRACK : '[' ;
RBRACK : ']' ;
LBRACE : '{' ;
RBRACE : '}' ;
IF : 'IF' ;
ELSE : 'LS' ;
DCLAR : 'DCLAR' ;
CONSTANT : 'CONSTANT' ;
VARIABLE : 'VARIABL' ;
EQUAL : '=' ;
GIVE : 'GIV' ;
BACK : 'BACK' ;
PRINT : 'printto' ;
FUNC : 'FUNC' ;
TYPEINT : 'InTgR' ;
TYPESTRING : 'StRiNg' ;
TYPEBOOLEAN : 'BoOlAn' ;
FALSE : 'NO' ;
SUB : '+' ;
BANG : '!' ;
DIV : ';' ;
ADD : '-' ;
MODULO : '\\' ;
LT : '<' ;
LE : '<=' ;
EQUAL_EQUAL : '==' ;
NOT_EQUAL : '!=' ;
GT : '>' ;
GE : '>=' ;
OR : '||' ;
AND : '&&' ;
DOT : ' . ' ;

COMMENT      : '|*' .*? '*|'    	-> channel(HIDDEN) ;

TEMPLATE_ID : [A-Z] ( [a-z][A-z] )*;
ID  : [a-zA-Z_] [a-zA-Z0-9_]* ;
INT : [0-9]+ ;
FLOAT
	:   '-'? INT '.' INT EXP?   // 1.35, 1.35E-9, 0.3, -4.5
	|   '-'? INT EXP            // 1e10 -3e4
	;
fragment EXP :   [Ee] [+\-]? INT ;

STRING :  '*' (ESC | ~["\\])* '*' ;
fragment ESC :   '\\' ["\bfnrt] ;

WS : [ \t\n\r]+ -> channel(HIDDEN) ;

/** "catch all" rule for any char not matche in a token rule of your
 *  grammar. Lexers in Intellij must return all tokens good and bad.
 *  There must be a token to cover all characters, which makes sense, for
 *  an IDE. The parser however should not see these bad tokens because
 *  it just confuses the issue. Hence, the hidden channel.
 */
ERRCHAR
	:	.	-> channel(HIDDEN)
	;

