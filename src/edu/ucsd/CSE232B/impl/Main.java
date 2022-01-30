package edu.ucsd.CSE232B.impl;

import edu.ucsd.CSE232B.parsers.ExpressionGrammarLexer;
import edu.ucsd.CSE232B.parsers.ExpressionGrammarParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws TransformerException {
        String expression = args[0];

        final ExpressionGrammarLexer lexer = new ExpressionGrammarLexer(CharStreams.fromString(expression));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final ExpressionGrammarParser parser = new ExpressionGrammarParser(tokens);

        final ParseTree tree = parser.ap();
        final XPathParser xPathParser = new XPathParser();
        final ArrayList<Node> nodes = xPathParser.visit(tree);

        // Set up the output transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        // Print the DOM node

//        System.out.println(nodes.size());
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        for(int i=0; i<nodes.size(); i++){
            sw.write("result "+i+":\n");
            DOMSource source = new DOMSource(nodes.get(i));
            trans.transform(source, result);
            sw.write("\n");
        }
        System.out.println(sw.toString());
    }
}
