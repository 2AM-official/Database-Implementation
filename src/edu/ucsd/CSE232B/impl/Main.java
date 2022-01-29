package edu.ucsd.CSE232B.impl;

import edu.ucsd.CSE232B.parsers.ExpressionGrammarLexer;
import edu.ucsd.CSE232B.parsers.ExpressionGrammarParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        final String expression = "doc(\"j_caesar.xml\")//(ACT,PERSONAE)/TITLE";

        final ExpressionGrammarLexer lexer = new ExpressionGrammarLexer(CharStreams.fromString(expression));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final ExpressionGrammarParser parser = new ExpressionGrammarParser(tokens);

        final ParseTree tree = parser.ap();
        final XPathParser xPathParser = new XPathParser();
        final ArrayList<Node> nodes = xPathParser.visit(tree);
        for(Node node: nodes){
            System.out.println("node: "+node.getTextContent()+" "+node.hashCode());
        }
    }
}
