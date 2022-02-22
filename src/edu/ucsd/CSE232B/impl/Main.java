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
        Scanner scanner = new Scanner(System.in);
//        String expression = args[0];
        while(true) {
            System.out.println("Input your XQuery Query: ");
            String expression = scanner.nextLine();

            final ExpressionGrammarLexer lexer = new ExpressionGrammarLexer(CharStreams.fromString(expression));
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
        }
    }
}
