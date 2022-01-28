grammar ExpressionGrammar ;

@header {
package edu.ucsd.CSE232B.parsers;
}

/*Rules*/
doc: 'doc(' fileName ')' | 'document(' fileName ')';

ap  : doc '/' rp   #directAbsolutePath
    | doc '//' rp  #indirectAbsolutePath
    ;

rp  : tagName       #tagRelativePath
    | '*'           #childrenRelativePath
    | '.'           #currentRelativePath
    | '..'          #upperRelativePath
    | 'text()'      #textRelativePath
    | '@' attName   #attribRelativePath
    | '(' rp ')'    #parentheseRelativePath
    | rp '/' rp     #directRelativePath
    | rp '//' rp    #indirectRelativePath
    | rp '[' f ']'  #filterRelativePath
    | rp ',' rp     #commaRelativePath
    ;

f   : rp            #rpFilter
    | rp EQ rp      #equalFilter
    | rp IS rp      #isFilter
    | rp '=' STRCON #stringconstantFilter
    | '(' f ')'     #parentheseFilter
    | f 'and' f     #andFilter
    | f 'or' f      #orFilter
    | 'not' f       #notFilter
    ;

 tagName: ID;
 attName: ID;
 fileName: STRCON;

 EQ: '=' | 'eq';
 IS: '==' | 'is';
 ID: [_a-zA-Z][a-zA-Z_0-9]*;

 STRCON:
    '"'('\\' (['"\\]) | ~["\\])* '"'
    |
    '\''('\\' (['"\\]) | ~['\\])* '\''
 ;

 WHITESPACE: [\r\n\t\f ]+ -> skip;