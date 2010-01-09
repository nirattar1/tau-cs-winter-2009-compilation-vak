package IC.Parser;

import java_cup.runtime.Symbol;
import java.lang.Math;

/**
 * this class is the lexical analyzer for the IC language
 */

%%

%class Lexer
%public
%function next_token
%type Token
%line
%scanerror LexicalError
%cup

/* Handling EOF: in case the file terminated inside a comment, throws an exception */
%eofval{
	if (yystate() == COMMENT2) throw new LexicalError("Lexical error: Unexpected end of file",yyline, null);
	else return new Token(sym.EOF,yyline,"EOF");
%eofval}
%state COMMENT1
%state COMMENT2

/* Macro definitions */
DIGIT = [0-9]
LOWER_CASE = [a-z]
UPPER_CASE = [A-Z]
LETTER = {LOWER_CASE}|{UPPER_CASE}
ALPHA_NUMERIC = {DIGIT}|{LETTER}|[_]
LINE_TERMINATOR = \r|\n|\r\n
WHITE_SPACE = {LINE_TERMINATOR}|[ \t\f]

%%

/* handling comments and white spaces */
<YYINITIAL> "//" {yybegin(COMMENT1);}
<YYINITIAL> "/*" {yybegin(COMMENT2);}
<YYINITIAL> {WHITE_SPACE} {}

/* handling mathematical operators */
<YYINITIAL> "/" { return new Token(sym.DIVIDE,yyline,yytext()); }
<YYINITIAL> "+" { return new Token(sym.PLUS,yyline,yytext()); }
<YYINITIAL> "-" { return new Token(sym.MINUS,yyline,yytext()); } 
<YYINITIAL> "*" { return new Token(sym.MULTIPLY,yyline,yytext()); }
<YYINITIAL> "%" { return new Token(sym.MOD,yyline,yytext()); }

/* handling parentheses */
<YYINITIAL> "(" { return new Token(sym.LP,yyline,yytext()); }
<YYINITIAL> "[" { return new Token(sym.LB,yyline,yytext()); }
<YYINITIAL> "{" { return new Token(sym.LCBR,yyline,yytext()); }
<YYINITIAL> ")" { return new Token(sym.RP,yyline,yytext()); }
<YYINITIAL> "]" { return new Token(sym.RB,yyline,yytext()); } 
<YYINITIAL> "}" { return new Token(sym.RCBR,yyline,yytext()); }

/* handling comparison operators and assignment */
<YYINITIAL> "==" { return new Token(sym.EQUAL,yyline,yytext()); }
<YYINITIAL> "=" { return new Token(sym.ASSIGN,yyline,yytext()); }
<YYINITIAL> ">=" { return new Token(sym.GTE,yyline,yytext()); }
<YYINITIAL> ">" { return new Token(sym.GT,yyline,yytext()); } 
<YYINITIAL> "<=" { return new Token(sym.LTE,yyline,yytext()); }
<YYINITIAL> "<" { return new Token(sym.LT,yyline,yytext()); }
<YYINITIAL> "!=" { return new Token(sym.NEQUAL,yyline,yytext()); }
<YYINITIAL> "!" { return new Token(sym.LNEG,yyline,yytext()); }

/* handling logical operators */
<YYINITIAL> "&&" { return new Token(sym.LAND,yyline,yytext()); }
<YYINITIAL> "||" { return new Token(sym.LOR,yyline,yytext()); }

/* handling punctuation */
<YYINITIAL> "," { return new Token(sym.COMMA,yyline,yytext()); }
<YYINITIAL> "." { return new Token(sym.DOT,yyline,yytext()); }
<YYINITIAL> ";" { return new Token(sym.SEMI,yyline,yytext()); }

/* handling flow control */
<YYINITIAL> "break" { return new Token(sym.BREAK,yyline,yytext()); }
<YYINITIAL> "continue" { return new Token(sym.CONTINUE,yyline,yytext()); }
<YYINITIAL> "extends" { return new Token(sym.EXTENDS,yyline,yytext()); }
<YYINITIAL> "while" { return new Token(sym.WHILE,yyline,yytext()); } 
<YYINITIAL> "if" { return new Token(sym.IF,yyline,yytext()); }
<YYINITIAL> "else" { return new Token(sym.ELSE,yyline,yytext()); }
<YYINITIAL> "true" { return new Token(sym.TRUE,yyline,yytext()); } 
<YYINITIAL> "false" { return new Token(sym.FALSE,yyline,yytext()); }
<YYINITIAL> "length" { return new Token(sym.LENGTH,yyline,yytext()); } 
<YYINITIAL> "new" { return new Token(sym.NEW,yyline,yytext()); } 
<YYINITIAL> "null" { return new Token(sym.NULL,yyline,yytext()); }
<YYINITIAL> "return" { return new Token(sym.RETURN,yyline,yytext()); } 
<YYINITIAL> "static" { return new Token(sym.STATIC,yyline,yytext()); }
<YYINITIAL> "this" { return new Token(sym.THIS,yyline,yytext()); }
<YYINITIAL> "void" { return new Token(sym.VOID,yyline,yytext()); } 

/* handling types */
<YYINITIAL> "boolean" { return new Token(sym.BOOLEAN,yyline,yytext()); }
<YYINITIAL> "int" { return new Token(sym.INT,yyline,yytext()); }
<YYINITIAL> "class" { return new Token(sym.CLASS,yyline,yytext()); }
<YYINITIAL> "string" { return new Token(sym.STRING,yyline,yytext()); }

/* handling IDs and values */
<YYINITIAL> (({DIGIT})+)({ALPHA_NUMERIC})+ { throw new LexicalError("Lexical error: illegal identifier, cannot start with a number",yyline,yytext());}
<YYINITIAL> ([1-9]({DIGIT})*)|([0]+) { if (Math.abs(Long.parseLong(yytext())) >= (long)Math.pow(2,31)) throw new LexicalError("Lexical error: integer out of bound",yyline,yytext());
									   else return new Token(sym.INTEGER,yyline,new Integer(yytext())); }
<YYINITIAL> {UPPER_CASE}({ALPHA_NUMERIC})* { return new Token(sym.CLASS_ID,yyline,yytext()); }
<YYINITIAL> {LOWER_CASE}({ALPHA_NUMERIC})* { return new Token(sym.ID,yyline,yytext()); }
<YYINITIAL> [\"]([ !#-\[\]-~]|"\\\\"|"\\\""|"\\t"|"\\n")*[\"] {
	String s = (yytext().substring(1,yytext().length()-1)).replace("\\n","\n").replace("\\\\","\\").replace("\\t","\t").replace("\\\"","\"");
	return new Token(sym.QUOTE,yyline,s); }

/* handling all the other crap */
<YYINITIAL> . { throw new LexicalError("Lexical error: illegal character",yyline,yytext()); }

/* states for comments */
<COMMENT1> [^\n] {}
<COMMENT1> [\n] {yybegin(YYINITIAL);}

<COMMENT2> [^\*] {}
<COMMENT2> "*/" {yybegin(YYINITIAL);}
<COMMENT2> "*" {}