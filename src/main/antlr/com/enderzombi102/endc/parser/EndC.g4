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
	:	import_statement* ( vardef | function | statement | template )+ EOF
	;

// IMPORT
import_statement
	:	OWN importables FROM MODULENAME '/'
	;

importables : importable ( '.' importable )* ;

importable : ID ;

// DECLARATIONS
function
	:	EXPORT? DECLARE SUBRUTINE ID func_args ARROW type func_block
	;

method
	:	DECLARE BEHAVIOR ID func_args ARROW type func_block
	;

template
	:	EXPORT? DECLARE TEMPLATE ID template_block
	;

template_initializer
	: DECLARE INITIALIZER func_args func_block
	;

template_deinitializer
	: DECLARE DEINITIALIZER func_args func_block
	;

vardef
	:	DECLARE CONSTANT type ID '=' expr '/'					# DeclareConstant
	|	DECLARE VARIABLE type ID '=' expr '/'					# DeclareVariable
	;

// ARGUMENTS
func_args : '{' formal_args? '}' ;

formal_args : formal_arg ('.' formal_arg)* ;

formal_arg
	:	type ID
	|	type '()' ID
	;

arguments : '{' expr_list? '}' ;

// BLOCKS
template_block
	:	 '[' ( vardef |	method | template_initializer | template_deinitializer )* ']'
	;

func_block
	:  '[' (statement|vardef)* ']'
	;

statement
	:	'CHCK' 'IF' '{' expr '}' 'DO' '[' statement ']' ( 'LS' 'DO' '[' statement ']' )?	# If
	|	qualifiedName '=' expr '/'															# Assign
	|	qualifiedName '(' expr ')' '=' expr '/'												# ElementAssign
	|	call_expr '/'																		# CallStatement
	|	'GIV' 'BACK' (expr) '/'																# Return
	|	func_block '/'				 														# BlockStatement
	;

// EXPRESSIONS
expr
	:	expr operator expr									# Op
	|	'-' expr											# Negate
	|	'!' expr											# Not
	|	call_expr											# Call
	|	qualifiedName '(' expr ')'							# Index
	|	'{' expr '}'										# Parens
	|	primary												# Atom
	;

call_expr
	: CALL BUILD ID arguments									# TemplateInstantiation
	| CALL '{' ( call_expr ) '}' ',' qualifiedName arguments		# NestedCall
	| CALL qualifiedName arguments								# SimpleCall
	| CALL SUBRUTINE func_args ARROW type func_block arguments		# DirectCall
	;

expr_list : expr ('.' expr)* ;

// OTHA
primary
	:	qualifiedName										# Identifier
	|	INT													# Integer
	|	FLOAT												# Float
	|	STRING												# String
	|	'(' expr_list ')'									# Vector
	|	'NO'												# FalseLiteral
	|	'M'													# MetaConstant
	|	'STDOUT'											# StreamConstant
	|	'STDIN'												# StreamConstant
	|	'STDERR'											# StreamConstant
	;

type
	:	'InTgR'                                             # IntTypeSpec
	|	'StRiNg'                                            # StringTypeSpec
	|	'BoOlAn'											# BooleanTypeSpec
	|	'NoThInG'											# NothingTypeSpec
	|	'()'												# ArrayTypeSpec
	|	ID													# CustomTypeSpec
	;

qualifiedName
    :   ID ( ',' ID )*
    ;

operator  : DIV|ADD|SUB|GT|GE|LT|LE|EQUAL_EQUAL|NOT_EQUAL|OR|AND|COMMA ; // no implicit precedence

// SYMBOLS
LPAREN : '(' ;
RPAREN : ')' ;
COLON : ':' ;
COMMA : ',' ;
LBRACK : '[' ;
RBRACK : ']' ;
LBRACE : '{' ;
RBRACE : '}' ;
EQUAL : '=' ;
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
DOT : '.' ;
ARROW : '<-' ;

// KEYWORDS
IF : 'IF' ;
ELSE : 'LS' ;
DECLARE : 'DCLAR' ;
CONSTANT : 'CONSTANT' ;
VARIABLE : 'VARIABL' ;
GIVE : 'GIV' ;
BACK : 'BACK' ;
SUBRUTINE : 'SUBROUTIN' ;
CALL : 'CALL' ;
EXPORT : 'XPORT' ;
TEMPLATE : 'TMPLAT' ;
BEHAVIOR : 'BHAVIOR' ;
BUILD : 'BUILD' ;
OWN : 'OWN' ;
FROM : 'FROM' ;
INITIALIZER : 'INITIALIZR' ;
DEINITIALIZER : 'DINITIALIZR' ;

// CONSTANTS
CONSTANTME : 'M' ;
FALSE : 'NO' ;

// DYNAMIC NAMES
COMMENT : '|*' .*? '*|' -> channel(HIDDEN) ;

ID : [a-zA-Z_]([a-zA-Z0-9_])+ ;
MODULENAME : [a-zA-Z]+ ;
INT : [0-9]+ ;
FLOAT
	:   '-'? INT '.' INT EXP?   // 1.35, 1.35E-9, 0.3, -4.5
	|   '-'? INT EXP            // 1e10 -3e4
	;
fragment EXP :   [Ee] [+\-]? INT ;

STRING :  '*' ( '\\*' | . )*? '*' ;
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
