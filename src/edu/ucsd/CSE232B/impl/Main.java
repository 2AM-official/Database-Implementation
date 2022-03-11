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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws TransformerException, IOException, ParserConfigurationException {
//        Scanner scanner = new Scanner(System.in);
//        while(true) {
//        System.out.println("Input number of lines:");
//        int n = scanner.nextInt();
//        System.out.println("Input your XQuery Query: ");
//        StringBuilder expression = new StringBuilder();
//        for(int i=0; i<n; i++){
//            expression.append(scanner.nextLine());
//        }


        String expression = "for $s in doc(\"j_caesar.xml\")//SPEAKER, $a in doc(\"j_caesar.xml\")//ACT,\n" +
                "\n" +
                "    $sp in $a//SPEAKER, $stxt in $s/text()\n" +
                "\n" +
                "where $sp eq $s and $stxt = \"CAESAR\"\n" +
                "\n" +
                "return <act> { $a/TITLE/text()} </act>";

        final ExpressionGrammarLexer lexer = new ExpressionGrammarLexer(CharStreams.fromString(expression.toString()));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final ExpressionGrammarParser parser = new ExpressionGrammarParser(tokens);

        final ParseTree tree = parser.xq();
        final XPathParser xPathParser = new XPathParser();
        final ArrayList<Node> nodes = xPathParser.visit(tree);

        // Set up the output transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.METHOD, "xml");

        // Print the DOM node
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);

        for (int i = 0; i < nodes.size(); i++) {
            DOMSource source = new DOMSource(nodes.get(i));
            trans.transform(source, result);
        }
        System.out.println(sw);
        System.out.println(nodes.size());
//        }

//        String expression = "for $b1 in doc(\"input\")/book,\n" +
//                "$aj in $b1/author/first/text(),\n" +
//                "$a1 in $b1/author,\n" +
//                "$af1 in $a1/first,\n" +
//                "$al1 in $a1/last,\n" +
//                "$b2 in doc(\"input\")/book,\n" +
//                "$a21 in $b2/author,\n" +
//                "$af21 in $a21/first,\n" +
//                "$al21 in $a21/last,\n" +
//                "$a22 in $b2/author,\n" +
//                "$af22 in $a22/first,\n" +
//                "$al22 in $a22/last,\n" +
//                "$b3 in doc(\"input\")/book,\n" +
//                "$a3 in $b3/author,\n" +
//                "$af3 in $a3/first,\n" +
//                "$al3 in $a3/last\n" +
//                "where $aj eq \"John\" and\n" +
//                "$af1 eq $af21 and $al1 eq $al21 and\n" +
//                "$af22 eq $af3 and $al22 eq $al3\n" +
//                "return <triplet> {$b1, $b2, $b3} </triplet>";

//        String expression = "for $s in doc(\"j_caesar.xml\")//SPEAKER, $a in doc(\"j_caesar.xml\")//ACT,\n" +
//                "\n" +
//                "    $sp in $a//SPEAKER, $stxt in $s/text()\n" +
//                "\n" +
//                "where $sp eq $s and $stxt = \"CAESAR\"\n" +
//                "\n" +
//                "return <act> { $a/TITLE/text()} </act>";
//
//        final ExpressionGrammarLexer lexer = new ExpressionGrammarLexer(CharStreams.fromString(expression));
//        final CommonTokenStream tokens = new CommonTokenStream(lexer);
//        final ExpressionGrammarParser parser = new ExpressionGrammarParser(tokens);
//
//        final ParseTree tree = parser.xq();
//        final XPathRewriter xPathRewriter = new XPathRewriter();
//        final String rewritten = xPathRewriter.visit(tree);
//
//        System.out.println(rewritten);
    }
}
