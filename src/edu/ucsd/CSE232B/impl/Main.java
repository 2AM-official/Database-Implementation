package edu.ucsd.CSE232B.impl;

import edu.ucsd.CSE232B.parsers.ExpressionGrammarLexer;
import edu.ucsd.CSE232B.parsers.ExpressionGrammarParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static ArrayList<Node> evaluate(String expression) throws TransformerException, IOException {
        long time = System.currentTimeMillis();
        final ExpressionGrammarLexer lexer = new ExpressionGrammarLexer(CharStreams.fromString(expression));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final ExpressionGrammarParser parser = new ExpressionGrammarParser(tokens);

        final ParseTree tree = parser.xq();
        final XPathParser xPathParser = new XPathParser();
        final ArrayList<Node> nodes = xPathParser.visit(tree);
        System.out.println("time usage: " + (System.currentTimeMillis()-time) + "ms");

        // Set up the output transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.METHOD, "xml");

        // Print the DOM node
        FileWriter fw = new FileWriter("xquery_m3_result.txt");
        StreamResult result = new StreamResult(fw);

        for (int i = 0; i < nodes.size(); i++) {
            DOMSource source = new DOMSource(nodes.get(i));
            trans.transform(source, result);
        }
//        System.out.println(fw);
        fw.close();
        System.out.println("count: "+nodes.size());

        return nodes;
    }

    private static String rewrite(String expression) throws IOException {
        final ExpressionGrammarLexer lexer = new ExpressionGrammarLexer(CharStreams.fromString(expression));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final ExpressionGrammarParser parser = new ExpressionGrammarParser(tokens);

        final ParseTree tree = parser.xq();
        final XPathRewriter xPathRewriter = new XPathRewriter();
        final String rewritten = xPathRewriter.visit(tree);

        FileWriter fw = new FileWriter("xquery_rewritten.txt");

        fw.write(rewritten);

        fw.close();

        System.out.println(rewritten);

        return rewritten;
    }

    public static void main(String[] args) throws TransformerException, IOException, ParserConfigurationException {
        String inputFileName = args[0];

        FileInputStream fileInputStream = new FileInputStream(inputFileName);
        Scanner scanner = new Scanner(fileInputStream);

        StringBuilder expressionBuilder = new StringBuilder();
        while(scanner.hasNextLine()){
            expressionBuilder.append(scanner.nextLine());
            expressionBuilder.append("\n");
        }

        System.out.println("The rewritten query:");
        String exprRewritten = rewrite(expressionBuilder.toString());
        System.out.println();
        System.out.println("original query:");
        evaluate(expressionBuilder.toString());
        System.out.println();
        System.out.println("rewritten query:");
        evaluate(exprRewritten);
    }
}
