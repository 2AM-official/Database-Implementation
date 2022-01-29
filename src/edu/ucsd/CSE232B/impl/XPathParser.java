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
import java.util.*;

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

        HashSet<Node> result = new HashSet<>();
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
        return new ArrayList<>(result);
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
            NodeList elements = currentElement.getElementsByTagName(tagName);
            for(int i=0; i<elements.getLength(); i++){
                result.add(elements.item(i));
            }
        }

        return result;
    }

    @Override
    public ArrayList<Node> visitCurrentRelativePath(ExpressionGrammarParser.CurrentRelativePathContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        result.add(currentNode);
        return result;
    }

    @Override
    public ArrayList<Node> visitChildrenRelativePath(ExpressionGrammarParser.ChildrenRelativePathContext ctx) {
        HashSet<Node> result = new HashSet<>();
        LinkedList<Node> bfsQueue = new LinkedList<>();
        bfsQueue.addLast(currentNode);
        while(!bfsQueue.isEmpty()){
            Node node = bfsQueue.getFirst();
            for(int i=0; i<node.getChildNodes().getLength(); i++){
                result.add(node.getChildNodes().item(i));
                if(node.getChildNodes().item(i).hasChildNodes()){
                    bfsQueue.addLast(node.getChildNodes().item(i));
                }
            }
            bfsQueue.removeFirst();
        }
        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Node> visitUpperRelativePath(ExpressionGrammarParser.UpperRelativePathContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        result.add(currentNode.getParentNode());
        return result;
    }

    @Override
    public ArrayList<Node> visitTextRelativePath(ExpressionGrammarParser.TextRelativePathContext ctx) {
        ArrayList<Node> result = new ArrayList<>();
        NodeList children = currentNode.getChildNodes(); // "The text node associated to element node"? Does it mean the child node for text node?
        for(int i=0; i<children.getLength(); i++){
            if(children.item(i).getNodeType()==Node.TEXT_NODE){
                result.add(children.item(i));
            }
        }
        return result;
    }

    @Override
    public ArrayList<Node> visitAttribRelativePath(ExpressionGrammarParser.AttribRelativePathContext ctx) {
        String attrName = ctx.attName().ID().getText();
        ArrayList<Node> result = new ArrayList<>();
        result.add(currentNode.getAttributes().getNamedItem(attrName));
        return result;
    }

    @Override
    public ArrayList<Node> visitParentheseRelativePath(ExpressionGrammarParser.ParentheseRelativePathContext ctx) {
        return visit(ctx.rp());
    }

    @Override
    public ArrayList<Node> visitDirectRelativePath(ExpressionGrammarParser.DirectRelativePathContext ctx) {
        ArrayList<Node> x = visit(ctx.rp(0));
        HashSet<Node> result = new HashSet<>();
        for(Node node: x){
            this.currentNode = node;
            result.addAll(visit(ctx.rp(1)));
        }

        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Node> visitIndirectRelativePath(ExpressionGrammarParser.IndirectRelativePathContext ctx) {
        ArrayList<Node> x = visit(ctx.rp(0));

        HashSet<Node> result = new HashSet<>();
        LinkedList<Node> bfsQueue = new LinkedList<>();
        for(Node node: x){
            bfsQueue.addLast(node);
            while(!bfsQueue.isEmpty()){
                this.currentNode = bfsQueue.getFirst();
                result.addAll(visit(ctx.rp(1)));
                for(int i=0; i<this.currentNode.getChildNodes().getLength(); i++){
                    bfsQueue.addLast(this.currentNode.getChildNodes().item(i));
                }
                bfsQueue.removeFirst();
            }
        }
        return new ArrayList<>(result);
    }
}
