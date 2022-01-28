package edu.ucsd.CSE232B.impl;

import edu.ucsd.CSE232B.parsers.ExpressionGrammarBaseVisitor;
import edu.ucsd.CSE232B.parsers.ExpressionGrammarParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class XPathParser extends ExpressionGrammarBaseVisitor<ArrayList<Node>> {
    private Document xmlDocument;
    private Node currentNode;

    public Document getXmlDocument() {
        return xmlDocument;
    }

    public void setXmlDocument(Document xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    @Override public ArrayList<Node> visitDirectAbsolutePath(ExpressionGrammarParser.DirectAbsolutePathContext ctx) {
        visitDoc(ctx.doc());

        return visit(ctx.rp());
    }

    @Override public ArrayList<Node> visitIndirectAbsolutePath(ExpressionGrammarParser.IndirectAbsolutePathContext ctx) {
        visitDoc(ctx.doc());

        ArrayList<Node> result = new ArrayList<>();
        LinkedList<Node> bfsQueue = new LinkedList<>();
        bfsQueue.addLast(xmlDocument.getDocumentElement());
        while(!bfsQueue.isEmpty()){
            this.currentNode = bfsQueue.getFirst();
            result.addAll(visit(ctx.rp()));
            for(int i=0; i<this.currentNode.getChildNodes().getLength(); i++){
                bfsQueue.addLast(this.currentNode.getChildNodes().item(i));
            }
            bfsQueue.removeFirst();
        }
        return result;
    }

    @Override public ArrayList<Node> visitDoc(ExpressionGrammarParser.DocContext ctx) {
        try {
            File xmlFile = new File(ctx.fileName().getText().substring(1, ctx.fileName().getText().length()-1));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.xmlDocument = builder.parse(xmlFile);
            this.currentNode = xmlDocument.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override public ArrayList<Node> visitTagRelativePath(ExpressionGrammarParser.TagRelativePathContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        String tagName = ctx.getText();

        if(currentNode instanceof Element){
            Element currentElement = (Element)this.currentNode;
            if(currentElement.getTagName().equals(tagName)){
                result.add(this.currentNode);
            }
        }

        return result;
    }


}
