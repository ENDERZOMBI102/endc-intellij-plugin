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
	:	import_statement* vardef* function* statement* template* EOF
	;

import_statement
	:	'OWN' importables 'FROM' MODULENAME '/'
	;

importables : importable ( '.' importable )* ;

importable
	: FUNCID
	| TYPEID
	;

function
	:	'XPORT'? 'DCLAR' 'FUNC' FUNCID '{' formal_args? '}' '<-' type func_block
	;

template
	:	'XPORT'? 'DCLAR' 'TMPLAT' TYPEID '[' template_body ']'
	;

template_body
	:	vardef
	|	function
	|	'DCLAR' 'FUNC' ('init'|'uninit') '{' formal_args? '}' func_block
	;

formal_args : formal_arg ('.' formal_arg)* ;

formal_arg
	:	type VARID
	|	type '()' VARID
	;

type:	'InTgR'                                             # IntTypeSpec
	|	'StRiNg'                                            # StringTypeSpec
	|	'BoOlAn'											# BooleanTypeSpec
	|	'NoThInG'											# NothingTypeSpec
	|	'()'												# ArrayTypeSpec
	|	TYPEID												# CustomTypeSpec
	;

func_block
	:  '[' (statement|vardef)* ']';

statement
	:	'CHCK' 'IF' '{' expr '}' 'DO' '[' statement ']' ( 'LS' 'DO' '[' statement ']' )?	# If
	|	VARID '=' expr '/'																	# Assign
	|	VARID '(' expr ')' '=' expr '/'														# ElementAssign
	|	call_expr '/'																		# CallStatement
	|	'CALL' 'printto' '{' ('STDOUT'|'STDERR') DOT (VARID|expr|call_expr) '}' '/'			# Print
	|	'GIV' 'BACK' expr '/'																# Return
	|	func_block '/'				 														# BlockStatement
	;

vardef
	:	'DCLAR' 'CONSTANT' TYPEID VARID '=' expr '/'					# DeclareConstant
	|	'DCLAR' 'VARIABL' TYPEID VARID '=' expr '/'						# DeclareVariable
	;

expr
	:	expr operator expr									# Op
	|	'-' expr											# Negate
	|	'!' expr											# Not
	|	call_expr											# Call
	|	VARID '(' expr ')'									# Index
	|	'{' expr '}'										# Parens
	|	primary												# Atom
	;

operator  : DIV|ADD|SUB|GT|GE|LT|LE|EQUAL_EQUAL|NOT_EQUAL|OR|AND|DOT ; // no implicit precedence

call_expr
	: 'CALL' 'BUILD' TYPEID '{' expr_list? '}'						# TemplateInstantiation
	| 'CALL' '{' ( call_expr ) '}' ',' FUNCID '{' expr_list? '}'	# NestedCall
	| 'CALL' FUNCID '{' expr_list? '}'								# SimpleCall
	;

expr_list : expr ('.' expr)* ;

primary
	:	FUNCID												# FunctionIdentifier
	|	TYPEID												# TypeIdentifier
	|	VARID												# VaribleIdentifier
	|	INT													# Integer
	|	FLOAT												# Float
	|	STRING												# String
	|	'[' expr_list ']'									# Vector
	|	'NO'												# FalseLiteral
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
DOT : '.' ;
ARROW : '<-' ;

COMMENT      : '|*' .*? '*|'    	-> channel(HIDDEN) ;

MODULENAME : [a-zA-Z]+ ;
TYPEID : [A-Z]([a-z][A-z])+ ;
FUNCID  : [a-z_]([a-zA-Z0-9_])* ;
VARID : [a-z]{1}([a-zA-Z0-9_\-$]){9,} ;
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
