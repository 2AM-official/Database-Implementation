grammar ExpressionGrammar ;

@header {
package edu.ucsd.CSE232B.parsers;
}

xq  : var                                           #varXQ
    | STRCON                                        #strXQ
    | ap                                            #apXQ
    | '(' xq ')'                                    #braceXQ
    | xq ',' xq                                     #commaXQ
    | xq '/' rp                                     #directXQ
    | xq '//' rp                                    #indirectXQ
    | '<' tagName '>' '{'? xq* '}'? '</' tagName '>'   #tagNameXQ
    | forClause letClause? whereClause? returnClause #statementXQ
    | letClause xq                                   #letXQ
    | 'join' '(' xq ',' xq ',' nameListClause ',' nameListClause ')' #joinXQ
    ;

forClause  : 'for' var 'in' xq (',' var 'in' xq)* ;
letClause  : 'let' var ':=' xq (',' var 'in' xq)* ;
whereClause : 'where' cond ;
returnClause: 'return' xq ;
nameListClause: '[' ID (',' ID)* ']' ;

cond: xq 'eq' xq                                               #eq1Cond
    | xq '=' xq                                                #eq2Cond
    | xq IS xq                                                 #isCond
    | 'empty' '(' xq ')'                                       #emptyCond
    | 'some' var 'in' xq (',' var 'in' xq)* 'satisfies' cond   #parSatisfyCond
    | '(' cond ')'                                             #braceCond
    | cond 'and' cond                                          #andCond
    | cond 'or' cond                                           #orCond
    | 'not' cond                                               #notCond
    ;

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
    | rp '=' rp     #equal1Filter
    | rp 'eq' rp    #equal2Filter // can not merge to one?
    | rp IS rp      #isFilter
    | rp '=' STRCON #stringconstantFilter
    | '(' f ')'     #parentheseFilter
    | f 'and' f     #andFilter
    | f 'or' f      #orFilter
    | 'not' f       #notFilter
    ;

 var: '$' ID;
 tagName: ID;
 attName: ID;
 fileName: STRCON;

 IS: '==' | 'is';
 ID: [_a-zA-Z][a-zA-Z_0-9\-]*;

 STRCON:
    '"'('\\' (['"\\]) | ~["\\])* '"'
    |
    '\''('\\' (['"\\]) | ~['\\])* '\''
 ;

 WHITESPACE: [\r\n\t\f ]+ -> skip;

